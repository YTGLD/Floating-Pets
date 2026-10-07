package com.ytgld.floating_pets.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;

import static com.mojang.renderpearl.api.pipeline.BlendFactor.*;
import static net.minecraft.client.renderer.RenderPipelines.GLOBALS_SNIPPET;

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

}
