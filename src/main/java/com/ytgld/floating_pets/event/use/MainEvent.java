package com.ytgld.floating_pets.event.use;

import com.ytgld.floating_pets.event.ComponentHandler;
import com.ytgld.floating_pets.event.TooltipsHandler;
import com.ytgld.floating_pets.items.component.components.blood.Furious;
import com.ytgld.floating_pets.items.component.components.heal.Pill;
import com.ytgld.floating_pets.items.component.components.heal.Support;
import com.ytgld.floating_pets.items.component.components.heal.SymbioticMeatballs;
import com.ytgld.floating_pets.items.items.Agreement;
import com.ytgld.floating_pets.items.items.BloodMeat;
import com.ytgld.floating_pets.items.items.YellowCube;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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

    @SubscribeEvent
    public void event(LivingDeathEvent event){
        Furious.event(event);
    }
    @SubscribeEvent
    public void event(LivingDamageEvent.Post event){
        Pill.event(event);
    }

}
