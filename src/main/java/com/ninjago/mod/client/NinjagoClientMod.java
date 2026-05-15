package com.ninjago.mod.client;

import com.ninjago.mod.NinjagoMod;
import com.ninjago.mod.client.event.ClientEvents;
import com.ninjago.mod.client.keybind.ModKeybinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NinjagoMod.MOD_ID, dist = Dist.CLIENT)
public class NinjagoClientMod {

    public NinjagoClientMod(IEventBus modEventBus) {
        modEventBus.addListener(ModKeybinds::register);
        NeoForge.EVENT_BUS.register(ClientEvents.class);
        NinjagoMod.LOGGER.info("[Ninjago] Client mod initialized.");
    }
}
