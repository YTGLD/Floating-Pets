package com.ytgld.floating_pets.event.use;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.event.ComponentHandler;
import com.ytgld.floating_pets.event.TooltipsHandler;
import com.ytgld.floating_pets.items.component.components.Support;
import com.ytgld.floating_pets.items.component.components.SymbioticMeatballs;
import com.ytgld.floating_pets.items.items.Agreement;
import com.ytgld.floating_pets.items.items.BloodMeat;
import com.ytgld.floating_pets.items.items.YellowCube;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class MainEvent {
    @SubscribeEvent
    public void event(AddAttributeTooltipsEvent event){
        TooltipsHandler.event(event);
    }
    @SubscribeEvent
    public void event(PlayerTickEvent.Pre event){
        ComponentHandler.event(event);
    }

    @SubscribeEvent
    public void event(PlayerEvent.PlayerRespawnEvent event) {
        YellowCube.event(event);
        Agreement.event(event);
        BloodMeat.event(event);
    }
    @SubscribeEvent
    public void event(LivingEntityUseItemEvent.Finish event){
        SymbioticMeatballs.event(event);
    }

    @SubscribeEvent
    public void event(LivingHealEvent event){
        Support.event(event);
    }

}
