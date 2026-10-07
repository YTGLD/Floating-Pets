package com.ytgld.floating_pets.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public interface IPlayer {
    AtomicReference<PetsInventory> getPetsInventory();
    void floatingPets$updatePetsInventory(ItemStack itemStack);
}
