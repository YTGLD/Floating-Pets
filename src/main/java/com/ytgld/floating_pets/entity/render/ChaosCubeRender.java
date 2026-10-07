package com.ytgld.floating_pets.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ytgld.floating_pets.HandlerClient;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.client.MRender;
import com.ytgld.floating_pets.client.warp.FloatingPetsFrameGraph;
import com.ytgld.floating_pets.entity.ChaosCube;
import com.ytgld.floating_pets.entity.state.ChaosCubeRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class ChaosCubeRender extends EntityRenderer<@NotNull ChaosCube, ChaosCubeRenderState> {
    private final SulfurCubeModel model;
    public ChaosCubeRender(EntityRendererProvider.Context context) {
        super(context);
        model = new SulfurCubeModel(context.getModelSet().bakeLayer(ModelLayers.SULFUR_CUBE));
    }

    @Override
    public boolean shouldRender(@NotNull ChaosCube entity, Frustum culler, double camX, double camY, double camZ, float partialTicks) {
        return true;
    }

    @Override
    public void submit(ChaosCubeRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        HandlerClient.setShowRenderWarped(true);;
        render(state, poseStack, submitNodeCollector, camera,false);
        int age = state.entity.tickCount;
        if (age > 100) {
            age = 100;
        }
        float value = age / 50f;
        FloatingPetsFrameGraph.putWarpedVec3(new FloatingPetsFrameGraph.WarpedVec3(
                state.entity.position(),value,
                5 * value,
                5 * value,
                0 * value
        ));
    }
    public void render(ChaosCubeRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera,boolean outline){
        ChaosCube entity = state.entity;
        float size = 0.75F;
        double x = Mth.lerp(state.partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(state.partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(state.partialTick, entity.zOld, entity.getZ());
        poseStack.pushPose();
        poseStack.translate(entity.getX()-x, entity.getY()-y,entity.getZ() -z);
        submitNodeCollector.submitCustomGeometry(poseStack, MRender.lightning,
                (pose, bufferSource) -> {
                    addTrail(pose,entity,bufferSource,1,1,0);
                });

        poseStack.popPose();


        poseStack.pushPose();
        poseStack.scale(0.33f,0.33f,0.33f);

        {
            poseStack.pushPose();
            poseStack.rotate(Axis.XP.rotation(Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            poseStack.rotate(Axis.YP.rotation(Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            poseStack.rotate(Axis.ZP.rotation(Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            submitNodeCollector.submitModel(model, new SulfurCubeRenderState(), poseStack, RenderTypes.entityTranslucent(
                    Identifier.withDefaultNamespace("textures/entity/sulfur_cube/sulfur_cube_outer.png")
            ), 255, NO_OVERLAY, state.outlineColor);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.rotate(Axis.XP.rotation(-Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            poseStack.rotate(Axis.YP.rotation(-Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            poseStack.rotate(Axis.ZP.rotation(-Mth.lerp(state.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20));
            poseStack.translate(-size, -size, -size);
            submitNodeCollector.submitCustomGeometry(poseStack, MRender.lightning,
                    (pose, bufferSource) -> {
                        Matrix4f mat = new Matrix4f(pose.pose());
                        renderCubeFace(mat, bufferSource, 6, Light.ARGB.color(25, 255, 255, 0), size);
                    });
            poseStack.popPose();
        }
        poseStack.popPose();
    }


    private void addTrail(PoseStack.Pose matrices,
                          ChaosCube entity,
                          VertexConsumer vertexConsumers,
                          float r, float g, float b) {

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();

        for (int i = 1; i < entity.trailPositions.size(); i++) {
            Vec3 prevPos = entity.trailPositions.get(i - 1);
            Vec3 currPos = entity.trailPositions.get(i);

            Vec3 adjustedPrevPos = new Vec3(
                    prevPos.x - entity.getX(),
                    prevPos.y - entity.getY(),
                    prevPos.z - entity.getZ()
            );
            Vec3 adjustedCurrPos = new Vec3(
                    currPos.x - entity.getX(),
                    currPos.y - entity.getY(),
                    currPos.z - entity.getZ()
            );
            float alpha = (float) i / entity.trailPositions.size();

            Vec3 dir = adjustedCurrPos.subtract(adjustedPrevPos).normalize();
            Vec3 toCamera = cameraPos.subtract(
                    entity.getX() + adjustedPrevPos.x,
                    entity.getY() + adjustedPrevPos.y,
                    entity.getZ() + adjustedPrevPos.z
            ).normalize();

            Vec3 side = dir.cross(toCamera).normalize().scale(alpha * 0.15f);

            Vec3 p0 = adjustedPrevPos.add(side);
            Vec3 p1 = adjustedPrevPos.subtract(side);
            Vec3 p2 = adjustedCurrPos.subtract(side);
            Vec3 p3 = adjustedCurrPos.add(side);

            vertexConsumers.addVertex(matrices, (float) p0.x, (float) p0.y, (float) p0.z)
                    .setColor(r, g, b, alpha).setUv(0, 0);

            vertexConsumers.addVertex(matrices, (float) p1.x, (float) p1.y, (float) p1.z)
                    .setColor(r, g, b, alpha).setUv(1, 0);

            vertexConsumers.addVertex(matrices, (float) p2.x, (float) p2.y, (float) p2.z)
                    .setColor(r, g, b, alpha).setUv(1, 1);

            vertexConsumers.addVertex(matrices, (float) p3.x, (float) p3.y, (float) p3.z)
                    .setColor(r, g, b, alpha).setUv(0, 1);
        }
    }
    @Override
    public ChaosCubeRenderState createRenderState() {
        return new ChaosCubeRenderState();
    }
    @Override
    public void extractRenderState(ChaosCube entity, ChaosCubeRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.entity = entity;
        reusedState.partialTick = partialTick;
    }
    public void renderCubeFace(@NotNull Matrix4f matrix4f, @NotNull VertexConsumer vertexConsumer, int face, int color,float size) {
        float x = size;
        float y = size;
        float z = size;
        if (face > 0) { // 前面
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
        if (face > 1) { // 后面
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
        if (face > 2) { // 左面
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
        if (face > 3) { // 右面
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
        if (face > 4) { // 上面
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y + size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
        if (face > 5) { // 下面
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z + size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x + size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
            vertexConsumer.addVertex(matrix4f, x - size, y - size, z - size).setUv(0, 0).setUv2(255,255).setOverlay(NO_OVERLAY).setNormal(0, 0, 0).setColor(color);
        }
    }
}
