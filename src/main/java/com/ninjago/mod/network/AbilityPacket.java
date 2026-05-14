package com.ninjago.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.ninjago.mod.NinjagoMod;

/** Sent from client to server when the player presses ability keybind R (index 2), T (3), Y (4). */
public record AbilityPacket(int abilityIndex)
    implements CustomPacketPayload {

    public static final Type<AbilityPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, "ability"));

    public static final StreamCodec<ByteBuf, AbilityPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, AbilityPacket::abilityIndex,
            AbilityPacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
