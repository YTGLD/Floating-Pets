package com.ytgld.floating_pets.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.client.RenderPetComponent;
import com.ytgld.floating_pets.items.component.IPetComponent;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemFloatingPets extends Item implements IPetComponent {
    public ItemFloatingPets(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component component = super.getName(stack);
        MutableComponent co = component.copy();
        co.withStyle(ChatFormatting.GOLD);
        return co;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new RenderPetComponent(stack,this));
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltipComponents, TooltipFlag flag) {
        text(stack, tooltipComponents, flag);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> doAttribute(ItemStack stack, Player player){
        return HashMultimap.create();
    }
    public void text(ItemStack stack, Consumer<Component> tooltipComponents, TooltipFlag flag){
    }
    public Identifier identifier(){
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,this.getDescriptionId());
    }

    @Override
    public int maxComponentNumber(ItemStack stack) {
        return 0;
    }
}
