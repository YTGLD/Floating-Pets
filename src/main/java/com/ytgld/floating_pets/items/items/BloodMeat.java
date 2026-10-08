package com.ytgld.floating_pets.items.items;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.entity.Entitys;
import com.ytgld.floating_pets.entity.BloodOrb;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.items.component.components.blood.Furious;
import com.ytgld.floating_pets.items.component.components.heal.Support;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.function.Consumer;

public class BloodMeat extends ItemFloatingPets {
    public BloodMeat(Item.Properties properties) {
        super(properties);
    }
    public static final String BLOOD_MEAT = "BloodMeat";

    public static void event(PlayerEvent.PlayerRespawnEvent event){
        Player player = event.getEntity();
        if (!Handler.has(player, InitItems.BloodMeat_.asItem())) {
            return;
        }
        BloodOrb bloodOrb = new BloodOrb(Entitys.BloodOrb_.get(), player.level());
        bloodOrb.setPos(player.position());
        bloodOrb.setOwner(player);
        bloodOrb.tame(player);
        player.level().addFreshEntity(bloodOrb);
    }
    public static void onKeyIsDown(Player player) {
        if (!Handler.has(player, InitItems.BloodMeat_.asItem())) {
            return;
        }
        PetsInventory chestInventory = Handler.getItem(player);
        if (chestInventory != null) {
            if (!player.level().isClientSide()) {
                for (int i = 0; i < chestInventory.getContainerSize(); i++) {
                    ItemStack stack = chestInventory.getItem(i);
                    if (stack.is(InitItems.BloodMeat_)) {
                        CompoundTag compoundTag = stack.get(DataReg.tag);
                        if (compoundTag != null) {
                            if (!compoundTag.getBooleanOr(BLOOD_MEAT, false)) {
                                BloodOrb bloodOrb = new BloodOrb(Entitys.BloodOrb_.get(), player.level());
                                bloodOrb.setPos(player.position());
                                bloodOrb.setOwner(player);
                                bloodOrb.tame(player);
                                player.level().addFreshEntity(bloodOrb);
                                compoundTag.putBoolean(BLOOD_MEAT, true);
                                break;
                            } else {
                                compoundTag.putBoolean(BLOOD_MEAT, false);
                            }
                        } else {
                            stack.set(DataReg.tag, new CompoundTag());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void text(ItemStack stack, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        float value = 0;
        CompoundTag compoundTag = stack.get(DataReg.tag);
        if (compoundTag !=null) {
            value = compoundTag.getIntOr(Furious.tag,0);
        }
        tooltipComponents.accept(Component.translatable("floating_pets.kill",value).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public int maxComponentNumber(ItemStack stack) {
        return 2;
    }
}


