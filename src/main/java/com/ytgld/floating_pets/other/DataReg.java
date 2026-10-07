package com.ytgld.floating_pets.other;

import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataReg {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, FloatingPets.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> tag =
            REGISTRY.register("tag",()-> DataComponentType.<CompoundTag>builder().persistent(CompoundTag.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PetComponentData>> component =
            REGISTRY.register("component",() -> DataComponentType.<PetComponentData>builder().persistent(PetComponentData.CODEC).build());

}


