package com.ytgld.floating_pets.other;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.entity.Entitys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = FloatingPets.MODID)
public class AttReg {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, FloatingPets.MODID);
    public static final DeferredHolder<Attribute,?> blood_attack_time = REGISTRY.register("blood_attack_time",()->{
        return new RangedAttribute("attribute.name.floating_pets.blood_attack_time", 20, 1, 100).setSyncable(true);
    });
    @SubscribeEvent
    public static void EntityAttributeCreationEvent(EntityAttributeModificationEvent event){
        event.add(Entitys.BloodOrb_.get() , AttReg.blood_attack_time,20);
    }
}
