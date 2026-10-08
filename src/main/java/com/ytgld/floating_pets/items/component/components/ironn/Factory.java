package com.ytgld.floating_pets.items.component.components.ironn;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Factory extends PetComponentBase {
    public static final String tag = "giveFactory";

    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"factory");
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }

    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.factory.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.factory.tip.2").withStyle(ChatFormatting.GOLD));
    }
}
