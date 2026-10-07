package com.ytgld.floating_pets.items;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.client.screen.FloatingPetsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class FloatingPetsBook extends Item {
    public FloatingPetsBook(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenBookPayload());
        }
        return InteractionResult.SUCCESS;
    }

    public record OpenBookPayload() implements CustomPacketPayload {
        public static final Type<OpenBookPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(FloatingPets.MODID, "open_book"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, OpenBookPayload> STREAM_CODEC =
                StreamCodec.unit(new OpenBookPayload());
    }
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenBookPayload.TYPE,
                OpenBookPayload.STREAM_CODEC,
                OpenBookHandler::handle
        );
    }
    public static class OpenBookHandler {
        public static void handle(OpenBookPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> ClientOnly.openBook(context.player()));
        }
    }
    public static class ClientOnly {
        public static void openBook(Player player) {
            Minecraft.getInstance().setScreenAndShow(new FloatingPetsScreen(player));
        }
    }
}

