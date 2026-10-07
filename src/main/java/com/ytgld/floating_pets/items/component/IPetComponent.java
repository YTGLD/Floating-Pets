package com.ytgld.floating_pets.items.component;


import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.other.DataReg;
import com.ytgld.floating_pets.other.PetComponentData;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;

public interface IPetComponent {

    int maxComponentNumber(ItemStack stack);


    static HashSet<PetComponentBase> theComponent(ItemStack stack){
        HashSet<PetComponentBase> hashSet = new HashSet<>();

        PetComponentData petComponentData = petsData(stack);
        Registry<PetComponentBase> registry =  PetComponents.GiftRegister;
        if (petComponentData == null) {
            return hashSet;
        }
        for (String string : petComponentData.hashSet()){
            PetComponentBase petComponentBase =
                    registry.getValue(Identifier.parse(
                            string
                    ));
            hashSet.add(petComponentBase);
        }
        return hashSet;
    }
    static @Nullable PetComponentData petsData(ItemStack stack){
        return stack.get(DataReg.component.get());
    }


    static boolean addComponent(ItemStack stack , PetComponentBase giftBase){
        PetComponentData petComponentData = stack.get(DataReg.component.get());
        if (petComponentData == null) {
            stack.set(DataReg.component.get(),new PetComponentData(new HashSet<>()));
        }
        if (petComponentData != null && stack.getItem() instanceof IPetComponent iPetComponent) {
            if (theComponent(stack).contains(giftBase)) {
                return false;
            }
            if (petComponentData.hashSet().size() < iPetComponent.maxComponentNumber(stack)) {
                petComponentData.add(giftBase.id().toString());
                upData(stack);
                return true;
            }
        }
        return false;
    }
    static boolean isHasComponent(ItemStack stack , PetComponentBase giftBase){
        if (stack.getItem() instanceof IPetComponent) {
            PetComponentData petComponentData = petsData(stack);
            if (petComponentData == null) {
                return false;
            }
            return petComponentData.hashSet().contains(giftBase.id().toString());
        }
        return false;
    }

    static boolean isHasComponent(Player player , PetComponentBase giftBase){
        PetsInventory petsInventory = Handler.getItem(player);
        if (petsInventory != null) {
            for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                ItemStack stack = petsInventory.getItem(i);
                if (stack.getItem() instanceof IPetComponent) {
                    PetComponentData petComponentData = petsData(stack);
                    if (petComponentData == null) {
                        return false;
                    }
                    return petComponentData.hashSet().contains(giftBase.id().toString());
                }
            }
        }
        return false;
    }
    static void upData(ItemStack stack){
        stack.set(DataReg.component.get(),stack.get(DataReg.component.get()));
    }

}
