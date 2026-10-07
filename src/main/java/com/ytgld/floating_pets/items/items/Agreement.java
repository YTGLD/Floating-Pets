package com.ytgld.floating_pets.items.items;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.entity.Entitys;
import com.ytgld.floating_pets.entity.Reactor;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashSet;
import java.util.function.Consumer;

public class Agreement extends ItemFloatingPets {
    public Agreement(Item.Properties properties) {
        super(properties);
    }
    public static final String chestHasReactor= "ChestHasReactor";
    public static void onKeyIsDown(Player player) {
        PetsInventory chestInventory = Handler.getItem(player);
        if (chestInventory != null) {
            if (!player.level().isClientSide()) {
                for (int i = 0; i < chestInventory.getContainerSize(); i++) {
                    ItemStack stack = chestInventory.getItem(i);
                    if (stack.is(InitItems.Agreement_)) {
                        CompoundTag compoundTag = stack.get(DataReg.tag);
                        if (compoundTag != null) {
                            if (!compoundTag.getBooleanOr(chestHasReactor, false)) {
                                Reactor reactor = new Reactor(Entitys.Reactor_.get(), player.level());
                                reactor.setPos(player.position());
                                reactor.setOwner(player);
                                reactor.tame(player);
                                player.level().addFreshEntity(reactor);
                                compoundTag.putBoolean(chestHasReactor, true);
                                break;
                            } else {
                                compoundTag.putBoolean(chestHasReactor, false);
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
    public void text(ItemStack stack, Consumer<Component> tooltipComponents, TooltipFlag flag){
        tooltipComponents.accept(Component.translatable("item.chest_item.agreement.string.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.accept(Component.translatable("item.chest_item.agreement.string.2").withStyle(ChatFormatting.GOLD));
    }
}

