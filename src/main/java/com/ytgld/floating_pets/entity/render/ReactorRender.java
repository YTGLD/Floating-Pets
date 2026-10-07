package com.ytgld.floating_pets.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ytgld.floating_pets.HandlerClient;
import com.ytgld.floating_pets.client.light.LightRenders;
import com.ytgld.floating_pets.client.warp.FloatingPetsFrameGraph;
import com.ytgld.floating_pets.entity.Reactor;
import com.ytgld.floating_pets.entity.state.ReactorRenderState;
import com.ytgld.floating_pets.items.InitItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class ReactorRender extends EntityRenderer<Reactor, ReactorRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final ItemModelResolver itemModelResolver2;
    public ReactorRender(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
        itemModelResolver2 = context.getItemModelResolver();
    }

    @Override
    public boolean shouldRender(Reactor livingEntity, Frustum camera, double camX, double camY, double camZ, float partialTicks) {
        return true;
    }

    @Override
    public @NotNull ReactorRenderState createRenderState() {
        return new ReactorRenderState();
    }

    @Override
    public void extractRenderState(Reactor entity, ReactorRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.entity = entity;
        reusedState.partialTick = partialTick;
        this.itemModelResolver.updateForNonLiving(reusedState.item,
                InitItems.Reactor_.asItem().getDefaultInstance(), ItemDisplayContext.FIXED, entity);

        this.itemModelResolver2.updateForNonLiving(reusedState.item2,
                InitItems.ReactorIn_.asItem().getDefaultInstance(), ItemDisplayContext.FIXED, entity);
    }


    @Override
    public void submit(ReactorRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        HandlerClient.setShowRenderWarped(true);
        HandlerClient.setShowRenderLight(true);
        Reactor entity = renderState.entity;
        LightRenders.addLight( entity.getX(),entity.getY(),entity.getZ(),
                12,
                0.1f ,1, 0.1f
                ,1);
        double x = Mth.lerp(renderState.partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(renderState.partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(renderState.partialTick, entity.zOld, entity.getZ());
        poseStack.pushPose();
        poseStack.translate(entity.getX()-x, entity.getY()-y,entity.getZ() -z);
        {
            poseStack.pushPose();
            poseStack.rotate(Axis.YN.rotation((Mth.lerp(renderState.partialTick, entity.oldArrowAxis, entity.arrowAxis) / 20)));

            poseStack.scale(0.33f,0.33f,0.33f);

            renderState.item.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            renderState.item2.submit(poseStack, collector, 255, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

}




