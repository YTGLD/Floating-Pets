package com.ytgld.floating_pets.items.component.components;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.List;

public class SymbioticMeatballs extends PetComponentBase {
    public static final String tag = "giveSymbioticMeatballs";
    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"symbiotic_meatballs");
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }

    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.symbiotic_meatballs.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.symbiotic_meatballs.tip.2").withStyle(ChatFormatting.GOLD));

    }

    public static void event(LivingEntityUseItemEvent.Finish event){
        if (event.getEntity() instanceof Player player) {
            if (event.getItem().is(Items.ENCHANTED_GOLDEN_APPLE)) {
                if (!IPetComponent.isHasComponent(player, PetComponents.symbiotic_meatballs.get())) {
                    IPetComponent.addNumber(player, InitItems.YellowCube_.asItem(), tag, 1,
                            PetComponents.symbiotic_meatballs.get(), 1);
                }
            }
        }
    }
}
