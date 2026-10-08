package com.ytgld.floating_pets.items.component.components.blood;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.entity.BloodOrb;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.other.AttReg;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class Furious extends PetComponentBase {

    public static final String tag = "giveFurious";

    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"furious");
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> attributeModifierMultimap(OwnableEntity entity){
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        int  time = 0;
        int  damage = 0;
        if (entity.getOwner() instanceof Player player && IPetComponent.isHasComponent(player,PetComponents.furious.get())) {
            time = -10;
            damage = 5;
        }
        Identifier id = Identifier.fromNamespaceAndPath(FloatingPets.MODID,"furious");
        modifiers.put(AttReg.blood_attack_time, new AttributeModifier(id,
                time, AttributeModifier.Operation.ADD_VALUE));
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(id,
                damage, AttributeModifier.Operation.ADD_VALUE));
        return modifiers;
    }
    public static void event(LivingDeathEvent event){
        if (event.getSource().getEntity() instanceof BloodOrb orb) {
            if (orb.getOwner() instanceof Player player) {
                if (!IPetComponent.isHasComponent(player, PetComponents.furious.get())) {
                    IPetComponent.addNumber(player, InitItems.BloodMeat_.asItem(), tag, 30, PetComponents.furious.get());
                }
            }
        }
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }
    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.furious.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.furious.tip.2").withStyle(ChatFormatting.GOLD));




    }
}
