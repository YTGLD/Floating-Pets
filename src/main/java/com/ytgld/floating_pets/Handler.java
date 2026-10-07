package com.ytgld.floating_pets;

import com.ytgld.floating_pets.inventory.IPlayer;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.inventory.PetsMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class Handler {

    public static void updateAttribute(Player player,ItemStack stack) {
        if (player instanceof IPlayer iPlayer) {
            iPlayer.floatingPets$updatePetsInventory(stack);
        }
    }
    public static boolean has(Player player, Item item) {
        PetsInventory petsInventory = Handler.getItem(player);
        if (petsInventory != null) {
            for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                ItemStack stack = petsInventory.getItem(i);
                if (stack.is(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static @Nullable PetsInventory getItem(Player player) {
        if (player instanceof IPlayer iPlayer) {
            return iPlayer.getPetsInventory().get();
        }
        return null;
    }



    public static void openMune(Player player) {
        if (player instanceof IPlayer iPlayer) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHEST_OPEN, SoundSource.AMBIENT, 1, 1);
            player.openMenu(new SimpleMenuProvider(
                    (i, inventory, p_53126_) -> new PetsMenu(i, inventory,
                            iPlayer.getPetsInventory().get()), Component.translatable("itemGroup.floating_pets")
            ));

        }
    }

}
