package com.ytgld.floating_pets.items.component.components;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.List;

public class Support extends PetComponentBase {
    public static final String tag = "giveSupport";
    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"support");
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }

    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.support.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.support.tip.2").withStyle(ChatFormatting.GOLD));

    }

    public static void event(LivingHealEvent event){
        if (event.getEntity() instanceof Player player) {
            if (!IPetComponent.isHasComponent(player, PetComponents.support.get())) {
                float value = event.getAmount() + 1;
                IPetComponent.addNumber(player, InitItems.YellowCube_.asItem(),tag,900,PetComponents.support.get(), (int) value);
            }
        }
    }
}
