package com.ytgld.floating_pets.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PetsMenu extends AbstractContainerMenu {
    private final Container container;
    private final int value = 3;

    public PetsMenu(int containerId, Inventory playerInventory, Container container) {
        super(PetsMenuTypes.GENERIC_3.get(), containerId);
        checkContainerSize(container, value);
        this.container = container;
        container.startOpen(playerInventory.player);
        this.addChestGrid(container, 18 * 3 + 8, 17);
        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    private void addChestGrid(Container container, int x, int y) {
        for (int j = 0; j < value; ++j) {
            this.addSlot(new PetsSlot(container, j, x + j * 18, y + 18));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < value) {
                if (!this.moveItemStackTo(itemstack1, value, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}

