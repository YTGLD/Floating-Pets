package com.ytgld.floating_pets.client.warp;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class FloatingPetsFrameGraph {
    public static final BindGroupLayout BUILD =
            BindGroupLayout.builder()
                    .withUniform("WarpedColor", UniformType.UNIFORM_BUFFER)
                    .withUniform("MainSampler", UniformType.COMBINED_IMAGE_SAMPLER)
                    .withUniform("DepthSampler", UniformType.COMBINED_IMAGE_SAMPLER)
                    .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                    .withUniform("Globals", UniformType.UNIFORM_BUFFER)
                    .build();
    public static final RenderPipeline PIPELINE =
            RenderPipeline.builder()
                    .withLocation(Identifier.fromNamespaceAndPath(FloatingPets.MODID ,
                            "pipeline/warp_post"))
                    .withVertexShader(Identifier.fromNamespaceAndPath(FloatingPets.MODID ,
                            "warped/screenquad"))
                    .withFragmentShader(Identifier.fromNamespaceAndPath(FloatingPets.MODID ,
                            "warped/warp_post"))
                    .withBindGroupLayout(BUILD)
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    .build();



    private static final int MAX_WARP_SIZE = 32;
    private static final int BUFFER_SIZE = MAX_WARP_SIZE * 16 * 2;
    private static final List<WarpedVec3> WARP_LIST = new ArrayList<>();
    private static GpuBuffer theGpuBuffer;


    private FloatingPetsFrameGraph() {
    }

    public static void addPostPass(
            FrameGraphBuilder frame,
            LevelTargetBundle targets
    ) {
        ResourceHandle<RenderTarget> main =
                targets.main;

        FramePass warpPass = frame.addPass("Floating Pets Post");
        warpPass.reads(main);
        ResourceHandle<RenderTarget> warpTarget =
                warpPass.createsInternal("Floating Pets Target",
                        new RenderTargetDescriptor(main.get().width, main.get().height,
                                new RenderTargetDescriptor.TextureProperties(null, GpuFormat.RGBA8_UNORM),
                                null
                        )
                );

        warpPass.executes(() -> {
            RenderTarget source = main.get();
            RenderTarget destination = warpTarget.get();

            try (RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder().createRenderPass(() -> "Floating Pets Post",
                            destination.getColorTextureView(),
                            Optional.empty(), null, OptionalDouble.empty())
            ) {
                renderPass.setPipeline(RenderSystem.getCompiledPipeline(PIPELINE));
                RenderSystem.bindDefaultUniforms(
                        renderPass
                );
                renderPass.setUniform("Projection",
                        RenderSystem.getProjectionMatrixBuffer());
                renderPass.setUniform("Globals",
                        RenderSystem.getGlobalSettingsUniform());
                renderPass.setUniform("MainSampler", source.getColorTextureView(),
                        RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
                renderPass.setUniform("DepthSampler",
                        source.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));

                renderPass.setUniform("WarpedColor", getTheGpuBuffer());
                renderPass.draw(3, 1, 0, 0
                );
            }
        });
        FramePass copyPass = frame.addPass("Floating Pets Copy");
        copyPass.reads(warpTarget);
        ResourceHandle<RenderTarget> newMain = copyPass.readsAndWrites(main);
        copyPass.executes(() -> {
            RenderTarget source = warpTarget.get();
            RenderTarget destination = newMain.get();
            destination.copyColorFrom(source);
        });
        targets.replace(LevelTargetBundle.MAIN_TARGET_ID, newMain);
    }
    private static void ensureBuffer() {

        if (theGpuBuffer != null) {
            return;
        }

        theGpuBuffer = RenderSystem
                .getDevice().createBuffer(() -> "Floating Pets Warped Color",
                        GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
                        BUFFER_SIZE
                );
    }
    public static void upload(
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4fc viewRotationMatrix
    ) {
        ensureBuffer();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(BUFFER_SIZE);
            Std140Builder builder =
                    Std140Builder.intoBuffer(buffer);

            for (int i = 0; i < MAX_WARP_SIZE; i++) {
                if (i < WARP_LIST.size()) {
                    WarpedVec3 warpedVec3 = WARP_LIST.get(i);
                    float x = (float) (warpedVec3.vec3.x - cameraX);
                    float y = (float) (warpedVec3.vec3.y - cameraY);
                    float z = (float) (warpedVec3.vec3.z - cameraZ);
                    Vector3f viewPosition = new Vector3f(x, y, z);
                    viewRotationMatrix.transformPosition(viewPosition);
                    builder.putVec4(viewPosition.x, viewPosition.y, viewPosition.z,warpedVec3.valueStronger);
                } else {
                    builder.putVec4(0.0F, 0.0F, 0.0F,0.0f);
                }
            }
            for (int i = 0; i < MAX_WARP_SIZE; i++) {
                if (i < WARP_LIST.size()) {
                    WarpedVec3 warpedVec3 = WARP_LIST.get(i);
                    builder.putVec4(warpedVec3.r, warpedVec3.g, warpedVec3.b, 1);
                } else {
                    builder.putVec4(0.0F, 0.0F, 0.0F, 0.0F);
                }
            }

            ByteBuffer data =
                    builder.get();

            RenderSystem.getDevice().createCommandEncoder()
                    .writeToBuffer(theGpuBuffer.slice(0, BUFFER_SIZE), data);
        }
    }
    public static GpuBuffer getTheGpuBuffer() {
        ensureBuffer();
        return theGpuBuffer;
    }
    public static void putWarpedVec3(WarpedVec3 warpedVec3){
        WARP_LIST.add(warpedVec3);
    }

    public static void registerPipelines(
            RegisterRenderPipelinesEvent event
    ) {
        event.registerPipeline(
                PIPELINE
        );
    }

    public static void clear() {
        WARP_LIST.clear();
    }

    public record WarpedVec3(Vec3 vec3 , float valueStronger,float r,float g, float b){}
}