package com.ninjago.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.ninjago.mod.NinjagoMod;

/** Sent from client → server after player picks a weapon in the selection GUI. */
public record WeaponSelectPacket(int weaponIndex)
    implements CustomPacketPayload {

    public static final Type<WeaponSelectPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, "weapon_select"));

    public static final StreamCodec<ByteBuf, WeaponSelectPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, WeaponSelectPacket::weaponIndex,
            WeaponSelectPacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
