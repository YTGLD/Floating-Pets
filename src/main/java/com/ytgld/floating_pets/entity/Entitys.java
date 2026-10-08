package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

    public static final DeferredHolder<EntityType<?>, EntityType<ChaosCube>> ChaosCube_ = REGISTRY.register("chaos_cube", () ->
            EntityType.Builder.of(ChaosCube::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(50).build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(FloatingPets.MODID, "chaos_cube"))));
    public static final DeferredHolder<EntityType<?>, EntityType<BloodOrb>> BloodOrb_ = REGISTRY.register("blood_orb", () ->
            EntityType.Builder.of(BloodOrb::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(50).build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(FloatingPets.MODID, "blood_orb"))));

    public static final DeferredHolder<EntityType<?>, EntityType<AttackBlood>> AttackBlood_ = REGISTRY.register("attack_blood", () ->
            EntityType.Builder.<AttackBlood>of(AttackBlood::new, MobCategory.MISC).sized(0.05f, 0.05f).clientTrackingRange(50).build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(FloatingPets.MODID, "attack_blood"))));

    @SubscribeEvent
    public static void EntityAttributeCreationEvent(EntityAttributeCreationEvent event){
        event.put(Entitys.Reactor_.get(), IronGolem.createAttributes().build());
        event.put(Entitys.ChaosCube_.get(), ChaosCube.createAttributes().build());
        event.put(Entitys.BloodOrb_.get(), BloodOrb.createAttributes().build());
    }

}
