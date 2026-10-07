package com.ytgld.floating_pets;

import com.mojang.logging.LogUtils;
import com.ytgld.floating_pets.entity.Entitys;
import com.ytgld.floating_pets.event.OpenHandler;
import com.ytgld.floating_pets.event.UseSkillHandler;
import com.ytgld.floating_pets.event.use.MainEvent;
import com.ytgld.floating_pets.inventory.PetsMenuTypes;
import com.ytgld.floating_pets.items.FloatingPetsBook;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.other.DataReg;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(FloatingPets.MODID)
public class FloatingPets {
    public static final String MODID = "floating_pets";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FloatingPets(IEventBus modEventBus, ModContainer modContainer) {
        InitItems.ITEMS.register(modEventBus);
        InitItems.CREATIVE_MODE_TABS.register(modEventBus);


        Entitys.REGISTRY.register(modEventBus);
        PetsMenuTypes.register.register(modEventBus);
        DataReg.REGISTRY.register(modEventBus);
        PetComponents.REGISTER.register(modEventBus);

        NeoForge.EVENT_BUS.register(new MainEvent());
        modEventBus.addListener(PetComponents::event);
        modEventBus.addListener(this::registerPayloadHandler);
    }
    private void registerPayloadHandler(final RegisterPayloadHandlersEvent evt) {
        FloatingPetsBook.register(evt);
        OpenHandler.register(evt);
        UseSkillHandler.register(evt);
    }
}
