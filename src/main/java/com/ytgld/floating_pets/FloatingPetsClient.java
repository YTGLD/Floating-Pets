package com.ytgld.floating_pets;

import com.ytgld.floating_pets.client.RenderPetComponent;
import com.ytgld.floating_pets.client.gui_particles.BlackParticlesAdd;
import com.ytgld.floating_pets.client.warp.FloatingPetsFrameGraph;
import com.ytgld.floating_pets.entity.Entitys;
import com.ytgld.floating_pets.entity.LightBulb;
import com.ytgld.floating_pets.entity.render.*;
import com.ytgld.floating_pets.other.Keys;
import com.ytgld.floating_pets.event.OpenHandler;
import com.ytgld.floating_pets.event.UseSkillHandler;
import com.ytgld.floating_pets.inventory.PetsMenuScreen;
import com.ytgld.floating_pets.inventory.PetsMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.Function;

@Mod(value = FloatingPets.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FloatingPets.MODID, value = Dist.CLIENT)
public class FloatingPetsClient {
    public FloatingPetsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    public static void event(ClientTickEvent.Pre event) {
        BlackParticlesAdd.tick();
    }
    @SubscribeEvent
    public static void event(RegisterRenderPipelinesEvent event) {
        FloatingPetsFrameGraph.registerPipelines(event);
    }
    @SubscribeEvent
    public static void event(ClientTickEvent.Post evt) {
        if (Keys.R.consumeClick()) {
            ClientPacketDistributor.sendToServer(new OpenHandler.OpenScreen());
        }

        if (Keys.C.consumeClick()) {
            ClientPacketDistributor.sendToServer(new UseSkillHandler.UseSkill());
        }
    }
    @SubscribeEvent
    public static void event(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(RenderPetComponent.class, Function.identity());
    }
    @SubscribeEvent
    public static void event(RegisterMenuScreensEvent event){
        event.register(PetsMenuTypes.GENERIC_3.get(), PetsMenuScreen::new);
    }
    @SubscribeEvent
    public static void event(RegisterKeyMappingsEvent event) {
        event.register(Keys.R);
        event.register(Keys.C);
    }
    @SubscribeEvent
    public static void event(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(Entitys.Reactor_.get(), ReactorRender::new);
        event.registerEntityRenderer(Entitys.ChaosCube_.get(), ChaosCubeRender::new);
        event.registerEntityRenderer(Entitys.AttackBlood_.get(), AttackBloodRender::new);
        event.registerEntityRenderer(Entitys.BloodOrb_.get(), BloodOrbRender::new);
        event.registerEntityRenderer(Entitys.LightBulb_.get(), LightBulbRender::new);
    }
}
