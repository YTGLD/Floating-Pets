package com.ytgld.floating_pets.inventory;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.InitItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PetsInventory extends SimpleContainer {
    public PetsInventory(Player player) {
        super(3);
    }
    public void fromSlots(ValueInput.TypedInputList<ItemStackWithSlot> input) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }
        for (ItemStackWithSlot itemstackwithslot : input) {
            if (itemstackwithslot.isValidInContainer(this.getContainerSize())) {
                this.setItem(itemstackwithslot.slot(), itemstackwithslot.stack());
            }
        }
    }
    public void storeAsSlots(ValueOutput.TypedOutputList<ItemStackWithSlot> output) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                output.add(new ItemStackWithSlot(i, itemstack));
            }
        }
    }
}

