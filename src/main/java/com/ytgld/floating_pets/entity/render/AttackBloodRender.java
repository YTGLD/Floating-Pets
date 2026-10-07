package com.ytgld.floating_pets.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.HandlerClient;
import com.ytgld.floating_pets.client.MRender;
import com.ytgld.floating_pets.client.light.LightRenders;
import com.ytgld.floating_pets.entity.AttackBlood;
import com.ytgld.floating_pets.entity.state.AttackBloodRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AttackBloodRender extends EntityRenderer<AttackBlood, AttackBloodRenderState> {
    public AttackBloodRender(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    @Override
    public boolean shouldRender(AttackBlood livingEntity, Frustum camera, double camX, double camY, double camZ, float partialTicks) {
        return true;
    }

    @Override
    public @NotNull AttackBloodRenderState createRenderState() {
        return new AttackBloodRenderState();
    }
    @Override
    public void extractRenderState(AttackBlood entity, AttackBloodRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.entity = entity;
        reusedState.partialTick = partialTick;
    }


    @Override
    public void submit(AttackBloodRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        AttackBlood entity = renderState.entity;

        HandlerClient.setShowRenderLight(true);
        LightRenders.addLight( entity.getX(),entity.getY(),entity.getZ(),
                8,
                1 ,0, 0
                ,1);

        double x = Mth.lerp(renderState.partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(renderState.partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(renderState.partialTick, entity.zOld, entity.getZ());
        poseStack.pushPose();
        poseStack.translate(entity.getX()-x, entity.getY()-y,entity.getZ() -z);


        collector.submitCustomGeometry(poseStack, MRender.lightning, (pose, bufferSource) -> {
            addTrail(pose,entity,bufferSource,1,0,0,0.15f);
        });

        collector.submitCustomGeometry(poseStack, MRender.endBlack, (pose, bufferSource) -> {
            renderSphere1(pose, bufferSource, 0, 0.15f);
        });
        poseStack.popPose();
    }

    private void addTrail(PoseStack.Pose matrices,
                          AttackBlood entity,
                          VertexConsumer vertexConsumers,
                          float r, float g, float b,float w) {

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

            Vec3 side = dir.cross(toCamera).normalize().scale(alpha * w);

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
    public void renderSphere1(@NotNull PoseStack.Pose matrices, @NotNull VertexConsumer vertexConsumer, int light, float a ) {
        int stacks = 20; // 垂直方向的分割数
        int slices = 20; // 水平方向的分割数
        for (int i = 0; i < stacks; ++i) {
            float phi0 = (float) Math.PI * ((i + 0) / (float) stacks);
            float phi1 = (float) Math.PI * ((i + 1) / (float) stacks);

            for (int j = 0; j < slices; ++j) {
                float theta0 = (float) (2 * Math.PI) * ((j + 0) / (float) slices);
                float theta1 = (float) (2 * Math.PI) * ((j + 1) / (float) slices);

                float x0 = a * (float) Math.sin(phi0) * (float) Math.cos(theta0);
                float y0 = a * (float) Math.cos(phi0);
                float z0 = a * (float) Math.sin(phi0) * (float) Math.sin(theta0);
                float x1 = a * (float) Math.sin(phi0) * (float) Math.cos(theta1);
                float y1 = a * (float) Math.cos(phi0);
                float z1 = a * (float) Math.sin(phi0) * (float) Math.sin(theta1);
                float x2 = a * (float) Math.sin(phi1) * (float) Math.cos(theta1);
                float y2 = a * (float) Math.cos(phi1);
                float z2 = a * (float) Math.sin(phi1) * (float) Math.sin(theta1);
                float x3 = a * (float) Math.sin(phi1) * (float) Math.cos(theta0);
                float y3 = a * (float) Math.cos(phi1);
                float z3 = a * (float) Math.sin(phi1) * (float) Math.sin(theta0);

                vertexConsumer.addVertex(matrices, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv(0, 0).setUv2(255,255).setNormal(matrices,1, 0, 0);
                vertexConsumer.addVertex(matrices, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv(0, 0).setUv2(255,255).setNormal(matrices,1, 0, 0);
                vertexConsumer.addVertex(matrices, x2, y2, z2).setColor(1.0f, 1.0f, 1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv(0, 0).setUv2(255,255).setNormal(matrices,1, 0, 0);
                vertexConsumer.addVertex(matrices, x3, y3, z3).setColor(1.0f, 1.0f, 1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setUv(0, 0).setUv2(255,255).setNormal(matrices,1, 0, 0);
            }
        }
    }
}



