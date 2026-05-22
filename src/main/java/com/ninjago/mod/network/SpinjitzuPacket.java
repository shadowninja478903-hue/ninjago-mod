package com.ninjago.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.ninjago.mod.NinjagoMod;

public record SpinjitzuPacket(int entityId, String element, int color, int radius)
    implements CustomPacketPayload {

    public static final Type<SpinjitzuPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, "spinjitzu"));

    public static final StreamCodec<ByteBuf, SpinjitzuPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,    SpinjitzuPacket::entityId,
            ByteBufCodecs.STRING_UTF8, SpinjitzuPacket::element,
            ByteBufCodecs.INT,    SpinjitzuPacket::color,
            ByteBufCodecs.INT,    SpinjitzuPacket::radius,
            SpinjitzuPacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
