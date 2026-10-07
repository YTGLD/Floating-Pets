package com.ytgld.floating_pets.client.light;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.LevelTargetBundle;
import org.joml.Vector4fc;

import java.util.Optional;
import java.util.OptionalDouble;
public final class LightFrameGraph {
    private LightFrameGraph() {
    }

    public static void addPostPass(FrameGraphBuilder frame, LevelTargetBundle targets) {
        ResourceHandle<RenderTarget> main = targets.main;
        FramePass lightPass = frame.addPass("Floating Pets Light Post");
        lightPass.reads(main);
        ResourceHandle<RenderTarget> lightTarget = lightPass.createsInternal("Floating Pets Light Target", new RenderTargetDescriptor(((RenderTarget)main.get()).width, ((RenderTarget)main.get()).height, new RenderTargetDescriptor.TextureProperties((Vector4fc)null, GpuFormat.RGBA8_UNORM), (RenderTargetDescriptor.TextureProperties)null));
        lightPass.executes(() -> {
            RenderTarget source = (RenderTarget)main.get();
            RenderTarget destination = (RenderTarget)lightTarget.get();
            if (source != null && destination != null) {
                try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() ->
                        "Floating Pets Light Post", destination.getColorTextureView(), Optional.empty(),
                        (GpuTextureView)null, OptionalDouble.empty())) {

                    renderPass.setPipeline(RenderSystem.getCompiledPipeline(LightRenders.COLORED_LIGHT_POST));
                    RenderSystem.bindDefaultUniforms(renderPass);
                    renderPass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
                    renderPass.setUniform("MainSampler", source.getColorTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
                    renderPass.setUniform("DepthSampler", source.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                    renderPass.setUniform("ColoredLights", LightRenders.getColoredLightsBuffer());
                    renderPass.draw(3, 1, 0, 0);

                }
            }
        });
        FramePass copyPass = frame.addPass("Floating Pets Light Copy");
        copyPass.reads(lightTarget);
        ResourceHandle<RenderTarget> newMain = copyPass.readsAndWrites(main);
        copyPass.executes(() -> {
            RenderTarget source = (RenderTarget)lightTarget.get();
            RenderTarget destination = (RenderTarget)newMain.get();
            if (source != null && destination != null) {
                destination.copyColorFrom(source);
            }
        });
        targets.replace(LevelTargetBundle.MAIN_TARGET_ID, newMain);
    }
}