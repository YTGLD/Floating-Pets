package com.ytgld.floating_pets.event;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.items.Agreement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class UseSkillHandler {

    public record UseSkill() implements CustomPacketPayload {
        public static final Type<UseSkill> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(FloatingPets.MODID, "use_pets"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, UseSkill> STREAM_CODEC =
                StreamCodec.unit(new UseSkill());
    }
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                UseSkill.TYPE,
                UseSkill.STREAM_CODEC,
                OpenBookHandler::handle
        );
    }
    public static class OpenBookHandler {
        public static void handle(UseSkill payload, IPayloadContext context) {
            context.enqueueWork(() -> ClientOnly.use(context.player()));
        }
    }
    public static class ClientOnly {
        public static void use(Player player) {
            Agreement.onKeyIsDown(player);
        }
    }
}
