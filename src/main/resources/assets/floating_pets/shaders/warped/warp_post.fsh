#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) out vec4 fragColor;
layout(location = 1) in vec2 texCoord;

uniform sampler2D MainSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform Globals {
    ivec3 CameraBlockPos;
    float GlintAlpha;
    vec3 CameraOffset;
    float GameTime;
    vec2 ScreenSize;
    int MenuBlurRadius;
    int UseRgss;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};

layout(std140) uniform WarpedColor {
    vec4 WarpPosition[32];
    vec4 WarpColor[32];
};

float hash21(vec2 p)
{
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);

    return fract(p.x * p.y);
}


/*
 * ============================================================
 * Noise
 * ============================================================
 */

float noise(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);

    f = f * f * (3.0 - 2.0 * f);

    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));

    return mix(
        mix(a, b, f.x),
        mix(c, d, f.x),
        f.y
    );
}


/*
 * ============================================================
 * FBM
 *
 * 只有 2 层，控制性能
 * ============================================================
 */

float fbm(vec2 p)
{
    float value = 0.0;

    value += noise(p) * 0.5;

    p *= 2.0;

    value += noise(p) * 0.25;

    return value;
}


/*
 * ============================================================
 * Main
 * ============================================================
 */
void main()
{
    vec4 baseColor =
    texture(
        MainSampler,
        texCoord
    );


    vec2 offset = vec2(0.0);

    float maxEdge = 0.0;

    vec3 warpColor = vec3(0.0);

/*
     * 当前像素最相关的实体深度
     */
    float nearestEntityDepth = -999999.0;

/*
     * 当前像素实际的最大扭曲强度
     */
    float maxWarpStrength = 0.0;

    bool hasWarp = false;


/*
     * ========================================================
     * Entity Loop
     * ========================================================
     */

    for (int i = 0; i < 32; i++)
    {
        if (WarpPosition[i].w <= 0.0)
        continue;


    /*
         * ----------------------------------------------------
         * View -> Clip
         * ----------------------------------------------------
         */

        vec4 clipPos =
        ProjMat *
        vec4(
        WarpPosition[i].xyz,
        1.0
        );


        if (clipPos.w <= 0.0)
        continue;
    /*
 * ----------------------------------------------------
 * Distance Falloff
 *
 * view-space 中：
 * xyz 就是相对于摄像机的位置
 * ----------------------------------------------------
 */

        float cameraDistance =
        length(WarpPosition[i].xyz);


    /*
 * 最大作用距离
 */
        const float maxDistance = 32.0;


    /*
 * 0 = 远处完全消失
 * 1 = 近处完整效果
 */
        float distanceFactor =
        1.0 -
        smoothstep(
            8.0,
            maxDistance,
            cameraDistance
        );


    /*
 * 让远处衰减更自然
 */
        distanceFactor *= distanceFactor;


    /*
 * 太远直接跳过
 */
        if (distanceFactor <= 0.001)
        continue;

    /*
         * ----------------------------------------------------
         * Entity UV
         * ----------------------------------------------------
         */

        vec2 entityUV =
        clipPos.xy /
        clipPos.w;

        entityUV =
        entityUV * 0.5 +
        0.5;


        if (entityUV.x < 0.0 ||
        entityUV.x > 1.0 ||
        entityUV.y < 0.0 ||
        entityUV.y > 1.0)
        {
            continue;
        }


    /*
         * ----------------------------------------------------
         * Entity Depth
         *
         * Reverse-Z
         * ----------------------------------------------------
         */

        float entityDepth =
        clipPos.z /
        clipPos.w;


    /*
         * ----------------------------------------------------
         * Distance
         * ----------------------------------------------------
         */

        vec2 delta =
        texCoord -
        entityUV;

        float distance =
        length(delta);


        float radius =0.1 +  WarpPosition[i].w / 50.0;


        if (distance > radius)
        continue;


        hasWarp = true;


    /*
         * ----------------------------------------------------
         * Direction
         * ----------------------------------------------------
         */

        vec2 direction;

        if (distance > 0.0001)
        {
            direction =
            delta / distance;
        }
        else
        {
            direction =
            vec2(0.0);
        }


    /*
         * ----------------------------------------------------
         * Radial
         *
         * 外围逐渐增强，
         * 中心保留一定扭曲。
         * ----------------------------------------------------
         */

        float radial =
        1.0 -
        smoothstep(
            0.0,
            radius,
            distance
        );

        radial *= radial;


    /*
         * 中心不归零
         *
         * 中心约 30%
         * 边缘约 100%
         */

        float centerDistortion =
        mix(
            0.30,
            0.5,
            radial
        );


    /*
         * ----------------------------------------------------
         * Edge
         * ----------------------------------------------------
         */

        float edge =
        smoothstep(
            radius * 0.30,
            radius * 0.95,
            distance
        );


    /*
         * ----------------------------------------------------
         * Scene Depth
         * ----------------------------------------------------
         */

        float sceneDepth =
        texture(
            DepthSampler,
            texCoord
        ).r;


    /*
         * ----------------------------------------------------
         * Occlusion
         *
         * Reverse-Z：
         *
         * sceneDepth > entityDepth
         * = 实体被前方物体遮挡
         * ----------------------------------------------------
         */

        float depthDifference =
        sceneDepth -
        entityDepth;


        float occlusion =
        1.0 -
        smoothstep(
            0.0005,
            0.004,
            depthDifference
        );


        centerDistortion *= occlusion;
        radial *= occlusion;


    /*
         * ----------------------------------------------------
         * 当前实体的实际扭曲强度
         * ----------------------------------------------------
         */

        float localWarpStrength =
        centerDistortion *
        WarpPosition[i].w *
        distanceFactor;

        if (localWarpStrength > maxWarpStrength)
        {
            maxWarpStrength =
            localWarpStrength;

            nearestEntityDepth =
            entityDepth;
        }


    /*
         * ----------------------------------------------------
         * Noise UV
         * ----------------------------------------------------
         */

        vec2 noiseUV =
        delta * 14.0;

        noiseUV +=
        vec2(
        GameTime * 150.0 * 0.25,
        GameTime * 150.0 * 0.18
        );


    /*
         * ----------------------------------------------------
         * Main FBM
         * ----------------------------------------------------
         */

        float n1 =
        fbm(noiseUV);


    /*
         * ----------------------------------------------------
         * Secondary FBM
         * ----------------------------------------------------
         */

        float n2 =
        fbm(
            noiseUV * 1.8 +
            vec2(
            -GameTime * 150.0 * 0.15,
            GameTime * 150.0 * 0.27
            )
        );


    /*
         * ----------------------------------------------------
         * Combined Noise
         * ----------------------------------------------------
         */

        float noiseValue =
        (
        n1 * 0.65 +
        n2 * 0.35
        )
        * 2.0
        - 1.0;


    /*
         * ----------------------------------------------------
         * Noise Direction
         * ----------------------------------------------------
         */

        float nx =
        noise(
            noiseUV +
            vec2(17.3, 4.2)
        )
        - 0.5;


        float ny =
        noise(
            noiseUV +
            vec2(3.7, 29.1)
        )
        - 0.5;


        vec2 noiseDirection =
        normalize(
            vec2(nx, ny) +
            vec2(0.0001)
        );


    /*
         * ----------------------------------------------------
         * Water Ripple
         * ----------------------------------------------------
         */

        float wave =
        sin(
            distance * 110.0
            -
            GameTime * 150.0 * 18.0
        );


    /*
         * 中心附近降低圆环感
         */

        float waveMask =
        smoothstep(
            0.0,
            0.04,
            distance
        );


        float ripple =
        wave *
        waveMask;


    /*
         * ----------------------------------------------------
         * Water Direction
         * ----------------------------------------------------
         *
         * 中心：
         *     主要使用噪声
         *
         * 外围：
         *     稍微增加径向方向
         * ----------------------------------------------------
         */

        float radialDirection =
        smoothstep(
            0.02,
            radius,
            distance
        );


        vec2 waterDirection =
        noiseDirection *
        (0.5 - radialDirection * 0.20)
        +
        direction *
        (0.5 + radialDirection * 0.20);


        waterDirection =
        normalize(
            waterDirection +
            vec2(0.0001)
        );


    /*
         * ----------------------------------------------------
         * Water Strength
         * ----------------------------------------------------
         */

        float waterStrength =
        noiseValue * 0.80
        +
        ripple * 0.20;


    /*
         * ----------------------------------------------------
         * Main Distortion
         *
         * 这里是最重要的地方。
         *
         * 0.035 比之前的 0.015
         * 明显增强实际画面折射。
         * ----------------------------------------------------
         */
        offset +=
        waterDirection *
        waterStrength *
        centerDistortion *
        WarpPosition[i].w *
        distanceFactor *
        0.012;
    /*
         * ----------------------------------------------------
         * Secondary Distortion
         * ----------------------------------------------------
         */

        vec2 secondaryDirection =
        vec2(
        noise(
            noiseUV +
            vec2(31.7, 11.4)
        ) - 0.5,

        noise(
            noiseUV +
            vec2(8.2, 43.6)
        ) - 0.5
        );


        secondaryDirection =
        normalize(
            secondaryDirection +
            vec2(0.0001)
        );

        offset +=
        secondaryDirection *
        noiseValue *
        centerDistortion *
        WarpPosition[i].w *
        distanceFactor *
        0.003;
    /*
         * ----------------------------------------------------
         * Edge
         * ----------------------------------------------------
         */

        maxEdge =
        max(
            maxEdge,
            edge *
            centerDistortion *
            WarpPosition[i].w *
            distanceFactor * 10
        );

    /*
         * ----------------------------------------------------
         * Warp Color
         * ----------------------------------------------------
         */

        warpColor =
        max(
            warpColor,
            WarpColor[i].rgb *
            centerDistortion *
            WarpPosition[i].w *
            distanceFactor
        );
    }


/*
     * ========================================================
     * No Warp
     * ========================================================
     */

    if (!hasWarp ||
    maxWarpStrength <= 0.00001 ||
    length(offset) < 0.000001)
    {
        fragColor =
        baseColor;

        return;
    }


/*
     * ========================================================
     * Limit Maximum Offset
     *
     * 防止多个实体叠加导致 UV 飞出去。
     * ========================================================
     */

    float offsetLength =
    length(offset);


    const float maxOffset = 0.020;

    if (offsetLength > maxOffset)
    {
        offset =
        offset /
        offsetLength *
        maxOffset;
    }


/*
     * ========================================================
     * Distorted UV
     * ========================================================
     */

    vec2 distortedUV =
    texCoord +
    offset;


    distortedUV =
    clamp(
        distortedUV,
        vec2(0.001),
        vec2(0.999)
    );


/*
     * ========================================================
     * Distorted Depth
     * ========================================================
     */

    float distortedDepth =
    texture(
        DepthSampler,
        distortedUV
    ).r;


/*
     * ========================================================
     * Final Depth Occlusion
     * ========================================================
     */

    float distortedDifference =
    distortedDepth -
    nearestEntityDepth;


    float distortedOcclusion =
    1.0 -
    smoothstep(
        0.0005,
        0.004,
        distortedDifference
    );


/*
     * ========================================================
     * Chromatic Aberration
     * ========================================================
     */

    float edgeStrength =
    clamp(
        maxEdge,
        0.0,
        10.0
    );


    float colorAmount =
    clamp(
        max(
            max(
                warpColor.r,
                warpColor.g
            ),
            warpColor.b
        ),
        0.0,
        1.0
    );


/*
     * 中心也有一点色散，
     * 边缘明显增强。
     */

    float dispersion =
    mix(
        0.05,
        0.25,
        edgeStrength
    )
    *
    colorAmount;


/*
     * ========================================================
     * RGB UV
     * ========================================================
     */

    vec2 redUV =
    distortedUV +
    offset *
    dispersion;


    vec2 greenUV =
    distortedUV;


    vec2 blueUV =
    distortedUV -
    offset *
    dispersion;


    redUV =
    clamp(
        redUV,
        vec2(0.001),
        vec2(0.999)
    );


    blueUV =
    clamp(
        blueUV,
        vec2(0.001),
        vec2(0.999)
    );


    float r =
    texture(
        MainSampler,
        redUV
    ).r;


    float g =
    texture(
        MainSampler,
        greenUV
    ).g;


    float b =
    texture(
        MainSampler,
        blueUV
    ).b;


    vec3 distortedColor =
    vec3(
    r,
    g,
    b
    );


/*
     * ========================================================
     * Final Color
     *
     * 重点：
     *
     * 不再把 distortedColor 当成一层
     * “覆盖色”。
     *
     * distortedUV 本身就是新的画面采样位置。
     * ========================================================
     */

    vec3 finalColor =
    mix(
        baseColor.rgb,
        distortedColor,
        distortedOcclusion
    );


/*
     * ========================================================
     * Final
     * ========================================================
     */

    fragColor =
    vec4(
    finalColor,
    baseColor.a
    );
}