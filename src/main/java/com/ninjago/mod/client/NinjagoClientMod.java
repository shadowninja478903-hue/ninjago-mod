package com.ninjago.mod.client;

import com.ninjago.mod.NinjagoMod;
import com.ninjago.mod.client.event.ClientEvents;
import com.ninjago.mod.client.keybind.ModKeybinds;
import com.ninjago.mod.init.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NinjagoMod.MOD_ID, dist = Dist.CLIENT)
public class NinjagoClientMod {

    public NinjagoClientMod(IEventBus modEventBus) {
        modEventBus.addListener(ModKeybinds::register);
        modEventBus.addListener(NinjagoClientMod::registerRenderers);
        NeoForge.EVENT_BUS.register(ClientEvents.class);
        NinjagoMod.LOGGER.info("[Ninjago] Client mod initialized.");
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Use the built-in ThrownItemRenderer — renders as a flat item spinning through air
        event.registerEntityRenderer(ModEntities.THROWN_SHURIKEN.get(), ThrownItemRenderer::new);
    }
}
