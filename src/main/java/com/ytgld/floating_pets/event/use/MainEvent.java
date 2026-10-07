package com.ytgld.floating_pets.event.use;

import com.ytgld.floating_pets.event.ComponentHandler;
import com.ytgld.floating_pets.event.TooltipsHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
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
}
