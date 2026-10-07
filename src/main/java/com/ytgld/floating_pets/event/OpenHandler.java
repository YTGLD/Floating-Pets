package com.ytgld.floating_pets.event;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.Handler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nonnull;

public class OpenHandler {


    public record OpenScreen() implements CustomPacketPayload {

        public static final Type<OpenScreen> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(FloatingPets.MODID, "open"));

        public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreen> STREAM_CODEC =
                StreamCodec.unit(new OpenScreen());
        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                OpenScreen.TYPE,
                OpenScreen.STREAM_CODEC,
                OpenBookHandler::handle
        );
    }
    public static class OpenBookHandler {
        public static void handle(OpenScreen payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    Handler.openMune(serverPlayer);
                }
            });
        }
    }
}
