package com.ytgld.floating_pets.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.*;
import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import static com.mojang.renderpearl.api.pipeline.BlendFactor.*;
import static net.minecraft.client.renderer.RenderPipelines.*;

public class MRender {
    public static final RenderPipeline.Snippet  GUI_TEXTURED_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).
            withBindGroupLayout(BindGroupLayouts.PROJECTION).
            withVertexShader(Identifier.fromNamespaceAndPath(FloatingPets.MODID,"core/ci_position_tex_color"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(FloatingPets.MODID,"core/ci_position_tex_color"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0).withColorTargetState(new ColorTargetState(new BlendFunction(
                    SRC_ALPHA,
                    ONE,
                    ONE,
                    ZERO))).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();

    public static final RenderPipeline GUI_TEXTURED =
            (RenderPipeline.builder(GUI_TEXTURED_SNIPPET).
                    withLocation("pipeline/gui_textured").withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).build());
    public static RenderType endBlack =RenderType.create(
            "end_gateway",
            RenderSetup.builder(RenderPipeline.builder(END_PORTAL_SNIPPET).withLocation("pipeline/end_portal").withShaderDefine("PORTAL_LAYERS", 15).build())
                    .withTexture("Sampler0", Identifier.fromNamespaceAndPath(FloatingPets.MODID,"textures/render/red.png"))
                    .withTexture("Sampler1", Identifier.fromNamespaceAndPath(FloatingPets.MODID,"textures/render/red.png"))
                    .createRenderSetup());
    public static RenderType lightning = RenderType.create(
            "lightning", RenderSetup.builder(RenderPipeline.builder(MATRICES_FOG_SNIPPET).withLocation("pipeline/lightning")
                            .withVertexShader("core/rendertype_lightning").withFragmentShader("core/rendertype_lightning")
                            .withColorTargetState(new ColorTargetState(new BlendFunction(SRC_ALPHA, ONE, ONE, ZERO)))
                            .withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(DepthStencilState.DEFAULT)
                            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withCull(false).build())
                    .sortOnUpload().createRenderSetup()
    );
}
