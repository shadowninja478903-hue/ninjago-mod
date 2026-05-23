package com.ninjago.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.ninjago.mod.NinjagoMod;

/** Sent from server → client to open the weapon selection GUI on first join. */
public record OpenWeaponSelectPacket() implements CustomPacketPayload {

    public static final Type<OpenWeaponSelectPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, "open_weapon_select"));

    public static final StreamCodec<ByteBuf, OpenWeaponSelectPacket> STREAM_CODEC =
        StreamCodec.unit(new OpenWeaponSelectPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
