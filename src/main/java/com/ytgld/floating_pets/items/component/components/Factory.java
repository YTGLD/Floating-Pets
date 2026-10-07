package com.ytgld.floating_pets.items.component.components;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashMap;
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
    public AttHolderModify attHolderModify() {
        AttHolderModify attHolderModify = new AttHolderModify(new HashMap<>());

        attHolderModify.multimap().put(Attributes.MAX_HEALTH,
                new AttributeModifier(this.id(),
                        4, AttributeModifier.Operation.ADD_VALUE));

        return attHolderModify;
    }

    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.factory.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.factory.tip.2").withStyle(ChatFormatting.GOLD));
    }
}
