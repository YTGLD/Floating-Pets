package com.ytgld.floating_pets.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemFloatingPets extends Item {
    public ItemFloatingPets(Properties properties) {
        super(properties);
    }
    public Identifier identifier(){
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,this.getDescriptionId());
    }
    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component component = super.getName(stack);
        MutableComponent co = component.copy();
        int c = 0XFFF80F3F;
        co.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(c)));
        return co;
    }
    public Multimap<Holder<Attribute>, AttributeModifier> doAttribute(ItemStack stack, Player player){
        return HashMultimap.create();
    }
    public void text(ItemStack stack, Consumer<Component> tooltipComponents, TooltipFlag flag){
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltipComponents, TooltipFlag flag) {
        text(stack, tooltipComponents, flag);
    }
}
