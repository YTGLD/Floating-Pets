package com.ytgld.floating_pets.event.use;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class MainEvent {
    @SubscribeEvent
    public void ItemTooltipEvent(LivingDamageEvent.Pre event){
    }
}
