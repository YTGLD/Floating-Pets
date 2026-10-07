package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = FloatingPets.MODID)
public class Entitys {
    public static final DeferredRegister.Entities REGISTRY =
            DeferredRegister.createEntities(FloatingPets.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Reactor>> Reactor_ = REGISTRY.register("reactor", () ->
            EntityType.Builder.of(Reactor::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(50).build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(FloatingPets.MODID, "reactor"))));
    @SubscribeEvent
    public static void EntityAttributeCreationEvent(EntityAttributeCreationEvent event){
        event.put(Entitys.Reactor_.get(), IronGolem.createAttributes().build());
    }

}
