package com.ninjago.mod.network;

import com.ninjago.mod.NinjagoMod;
import com.ninjago.mod.client.event.ClientNetworkHandler;
import com.ninjago.mod.item.NinjaArmorItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(NinjagoMod.MOD_ID).versioned("1.0.0");

        // Client → Server
        reg.playToServer(AbilityPacket.TYPE,      AbilityPacket.STREAM_CODEC,      ModNetwork::handleAbility);
        reg.playToServer(WeaponSelectPacket.TYPE,  WeaponSelectPacket.STREAM_CODEC,  ModNetwork::handleWeaponSelect);

        // Server → Client
        reg.playToClient(SpinjitzuPacket.TYPE,     SpinjitzuPacket.STREAM_CODEC,     ClientNetworkHandler::handleSpinjitzuPacket);
        reg.playToClient(OpenWeaponSelectPacket.TYPE, OpenWeaponSelectPacket.STREAM_CODEC, ClientNetworkHandler::handleOpenWeaponSelect);
        reg.playToClient(ParticlePacket.TYPE,      ParticlePacket.STREAM_CODEC,      ClientNetworkHandler::handleParticlePacket);
    }

    private static void handleAbility(AbilityPacket packet,
                                       net.neoforged.neoforge.network.handling.IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;
            // Abilities now come from chestplate
            ItemStack chest = sp.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if (chest.getItem() instanceof NinjaArmorItem armor) {
                armor.tryActivate(sp, sp.serverLevel(), packet.abilityIndex());
            }
        });
    }

    private static void handleWeaponSelect(WeaponSelectPacket packet,
                                            net.neoforged.neoforge.network.handling.IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer sp)
                com.ninjago.mod.event.ModEvents.giveWeaponAndArmor(sp, packet.weaponIndex());
        });
    }
}
