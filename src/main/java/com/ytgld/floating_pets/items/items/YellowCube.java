package com.ytgld.floating_pets.items.items;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.entity.Entitys;
import com.ytgld.floating_pets.entity.ChaosCube;
import com.ytgld.floating_pets.entity.ChaosCube;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class YellowCube extends ItemFloatingPets {
    public YellowCube(Properties properties) {
        super(properties);
    }
    public static void event(PlayerEvent.PlayerRespawnEvent event){
        Player player = event.getEntity();
        if (!Handler.has(player, InitItems.Agreement_.asItem())) {
            return;
        }
        ChaosCube chaosCube = new ChaosCube(Entitys.ChaosCube_.get(), player.level());
        chaosCube.setPos(player.position());
        chaosCube.setOwner(player);
        chaosCube.tame(player);
        player.level().addFreshEntity(chaosCube);
    }
    public static final String YELLOW_CUBE = "YellowCube";
    public static void onKeyIsDown(Player player) {
        if (!Handler.has(player, InitItems.YellowCube_.asItem())) {
            return;
        }
        PetsInventory chestInventory = Handler.getItem(player);
        if (chestInventory != null) {
            if (!player.level().isClientSide()) {
                for (int i = 0; i < chestInventory.getContainerSize(); i++) {
                    ItemStack stack = chestInventory.getItem(i);
                    if (stack.is(InitItems.YellowCube_)) {
                        CompoundTag compoundTag = stack.get(DataReg.tag);
                        if (compoundTag != null) {
                            if (!compoundTag.getBooleanOr(YELLOW_CUBE, false)) {
                                ChaosCube chaosCube = new ChaosCube(Entitys.ChaosCube_.get(), player.level());
                                chaosCube.setPos(player.position());
                                chaosCube.setOwner(player);
                                chaosCube.tame(player);
                                player.level().addFreshEntity(chaosCube);
                                compoundTag.putBoolean(YELLOW_CUBE, true);
                                break;
                            } else {
                                compoundTag.putBoolean(YELLOW_CUBE, false);
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
    public int maxComponentNumber(ItemStack stack) {
        return 2;
    }
}
