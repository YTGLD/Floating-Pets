package com.ytgld.floating_pets.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ytgld.floating_pets.HandlerClient;
import com.ytgld.floating_pets.client.light.LightRenders;
import com.ytgld.floating_pets.entity.LightBulb;
import com.ytgld.floating_pets.entity.state.LightBulbRenderState;
import com.ytgld.floating_pets.items.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.logging.Level;

public class LightBulbRender extends EntityRenderer<LightBulb, LightBulbRenderState> {
    private final ItemModelResolver itemModelResolver;
    public LightBulbRender(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public boolean shouldRender(LightBulb livingEntity, Frustum camera, double camX, double camY, double camZ, float partialTicks) {
        return true;
    }

    @Override
    public @NotNull LightBulbRenderState createRenderState() {
        return new LightBulbRenderState();
    }

    @Override
    public void extractRenderState(LightBulb entity, LightBulbRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.entity = entity;
        reusedState.partialTick = partialTick;
        this.itemModelResolver.updateForNonLiving(reusedState.item,
                Items.GLOWSTONE.asItem().getDefaultInstance(), ItemDisplayContext.FIXED, entity);
    }


    @Override
    public void submit(LightBulbRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        HandlerClient.setShowRenderLight(true);
        LightBulb entity = renderState.entity;
         ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        int brightness = level.getMaxLocalRawBrightness(entity.blockPosition());
        float light = 1 - (Lightmap.getBrightness(level.dimensionType(), brightness));
        LightRenders.addLight( entity.getX(),entity.getY(),entity.getZ(),
                24 * light,
                4 * light,0,0
                ,1);
        LightRenders.addLight( entity.getX(),entity.getY(),entity.getZ(),
                24 * light,
                0.20f * light ,2 * light, 0.2f * light
                ,1);

        double x = Mth.lerp(renderState.partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(renderState.partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(renderState.partialTick, entity.zOld, entity.getZ());
        poseStack.pushPose();
        poseStack.translate(entity.getX()-x, entity.getY()-y + 0.1666,entity.getZ() -z);
        {
            poseStack.pushPose();
            poseStack.rotate(Axis.YN.rotation((Mth.lerp(renderState.partialTick, entity.oldArrowAxis, entity.arrowAxis))));
            poseStack.scale(0.75f,0.75f,0.75f);
            renderState.item.submit(poseStack, collector, 255, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

}




