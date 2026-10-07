package com.ytgld.floating_pets.inventory;

import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PetsMenuTypes {
    public static final DeferredRegister<MenuType<?>> register = DeferredRegister.create(BuiltInRegistries.MENU, FloatingPets.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<PetsMenu>> GENERIC_3 = register.register("pets_mune",
            ()-> new MenuType<>((i,inventory)->{
                return new PetsMenu(i,inventory,new SimpleContainer(3));
            },FeatureFlags.DEFAULT_FLAGS));

}
