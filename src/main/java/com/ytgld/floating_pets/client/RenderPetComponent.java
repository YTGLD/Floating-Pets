package com.ytgld.floating_pets.client;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class RenderPetComponent implements ClientTooltipComponent, TooltipComponent {
    private final ItemStack stack;
    private final IPetComponent iPetComponent;
    public RenderPetComponent(ItemStack stack, IPetComponent iPetComponent) {
        this.stack = stack;
        this.iPetComponent = iPetComponent;
    }

    @Override
    public int getHeight(Font font) {
        int a = 0;
        if (iPetComponent.maxComponentNumber(stack) <= 0) {
            return a;
        }
        a = 20 + IPetComponent.theComponent(stack).size() * 8;
        return a;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return this.backgroundWidth();
    }

    private int backgroundWidth() {
        return iPetComponent.maxComponentNumber(stack) * 16;
    }

    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int imageSize= 16;
        graphics.pose().pushMatrix();
        for (int j = 0; j < iPetComponent.maxComponentNumber(stack); j++) {

            graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                    "textures/components/back.png"),
                    x + j * 16, y ,
                    0, 0,
                    imageSize, imageSize, imageSize, imageSize);

        }
        HashSet<PetComponentBase> petComponentBases = IPetComponent.theComponent(stack);
        if (!petComponentBases.isEmpty()) {
            for (int j = 0; j < petComponentBases.size(); j++) {
                graphics.blit(RenderPipelines.GUI_TEXTURED,
                        petComponentBases.stream().toList().get(j).image(),
                        x + j * 16, y ,
                        0, 0,
                        imageSize, imageSize, imageSize, imageSize);

                graphics.text(Minecraft.getInstance().font, Component.translatable(
                                FloatingPets.MODID + ".component." +
                                        petComponentBases.stream().toList().get(j).id().getPath()
                                        +".name")
                        , x, y + 18 + j * 8,Light.ARGB.color(255,205,200,50));
            }
        }
        graphics.pose().popMatrix();
    }
}


