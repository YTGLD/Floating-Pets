package com.ytgld.floating_pets.items.component;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.component.components.Factory;
import com.ytgld.floating_pets.items.component.components.Support;
import com.ytgld.floating_pets.items.component.components.SymbioticMeatballs;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class PetComponents {
    public static final ResourceKey<Registry<PetComponentBase>> GiftRegisterBase = key("gift");
    public static final Registry<PetComponentBase> GiftRegister = new RegistryBuilder<>(GiftRegisterBase).create();
    public static final DeferredRegister<PetComponentBase> REGISTER = DeferredRegister.create(GiftRegister, FloatingPets.MODID);

    public static DeferredHolder<PetComponentBase, ?> factory = REGISTER.register("factory", Factory::new);
    public static DeferredHolder<PetComponentBase, ?> support = REGISTER.register("support", Support::new);
    public static DeferredHolder<PetComponentBase, ?> symbiotic_meatballs = REGISTER.register("symbiotic_meatballs", SymbioticMeatballs::new);


    public static void event(NewRegistryEvent event){
        event.register(GiftRegister);
    }

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(FloatingPets.MODID, name));
    }
}
