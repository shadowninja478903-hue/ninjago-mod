package com.ninjago.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import com.ninjago.mod.NinjagoMod;

public record ParticlePacket(int entityId, String element, int abilityIndex)
    implements CustomPacketPayload {

    public static final Type<ParticlePacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, "particle"));

    public static final StreamCodec<ByteBuf, ParticlePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,         ParticlePacket::entityId,
            ByteBufCodecs.STRING_UTF8, ParticlePacket::element,
            ByteBufCodecs.INT,         ParticlePacket::abilityIndex,
            ParticlePacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    /** Called server-side to broadcast particle effect to nearby clients */
    public static void send(Player player, Level level, String element, int abilityIndex) {
        if (level instanceof ServerLevel sl) {
            PacketDistributor.sendToPlayersNear(sl, null,
                player.getX(), player.getY(), player.getZ(), 48,
                new ParticlePacket(player.getId(), element, abilityIndex));
        }
    }
}
