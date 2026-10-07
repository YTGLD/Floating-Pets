package com.ytgld.floating_pets.event;

import com.google.common.collect.Multimap;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.GatherSkippedAttributeTooltipsEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TooltipsHandler {
    public static void event(AddAttributeTooltipsEvent evt){
        AttributeTooltipContext context = evt.getContext();
        ItemStack stack = evt.getStack();
        GatherSkippedAttributeTooltipsEvent skipped =
                NeoForge.EVENT_BUS.post(new GatherSkippedAttributeTooltipsEvent(stack, context));

        if (skipped.isSkippingAll()) {
            return;
        }
        List<Component> attributesTooltip = new ArrayList<>();
        Player player = context.player();
        if (player!=null) {
            if (stack.getItem() instanceof ItemFloatingPets itemFloatingPets) {
                Multimap<Holder<Attribute>, AttributeModifier> attributes = itemFloatingPets.doAttribute(stack, player);
                HashSet<PetComponentBase> hashSet =IPetComponent.theComponent(stack);
                if (!hashSet.isEmpty()) {
                    for (PetComponentBase petComponentBase : hashSet.stream().toList()) {
                        PetComponentBase.AttHolderModify attHolderModify = petComponentBase.attHolderModify();
                        petComponentBase.text(stack,attributesTooltip,evt.getContext().flag());
                        for (Holder<Attribute> attributeHolder : attHolderModify.multimap().keySet()) {
                            AttributeModifier modifier = attHolderModify.multimap().get(attributeHolder);
                            attributes.put(attributeHolder, modifier);
                        }
                    }
                }

                if (!attributes.isEmpty()) {
                    attributes.values().removeIf(modifier -> skipped.isSkipped(modifier.id()));
                    evt.addTooltipLines(Component.empty());
                    attributesTooltip.add(Component.translatable("event.floating_pets.equip").withStyle(ChatFormatting.GOLD));
                    AttributeUtil.applyTextFor(
                            stack,
                            attributesTooltip::add,
                            attributes,
                            AttributeTooltipContext.of(player, context, context.tooltipDisplay(), context.flag()));
                    for (Component component : attributesTooltip) {
                        MutableComponent co = component.copy();
                        co.withStyle(ChatFormatting.GOLD);
                        evt.addTooltipLines(co);
                    }
                }
            }
        }
    }
}
