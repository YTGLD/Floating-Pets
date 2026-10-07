package com.ytgld.floating_pets.inventory;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PetsSlot extends Slot {
    public PetsSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }
    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ItemFloatingPets;
    }
    @Override
    public boolean mayPickup(Player player) {
        ItemStack itemstack = this.getItem();
        Handler.updateAttribute(player,itemstack);
        return true;
    }

}
