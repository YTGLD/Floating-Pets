package com.ytgld.floating_pets.event;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;

public class ComponentHandler {

    public static void event(PlayerTickEvent.Pre event){
        Player player = event.getEntity();
        PetsInventory petsInventory = Handler.getItem(player);
        if (petsInventory != null) {
            for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                ItemStack stack = petsInventory.getItem(i);
                if (stack.getItem() instanceof IPetComponent iPetComponent) {
                    if (iPetComponent.maxComponentNumber(stack) <= 0) {
                        continue;
                    }
                    HashSet<PetComponentBase> hashSet = IPetComponent.theComponent(stack);
                    if (!hashSet.isEmpty()) {
                        for (PetComponentBase petComponentBase : hashSet.stream().toList()) {
                            petComponentBase.tick(player,stack);
                        }
                    }
                }
            }
        }
    }
}
