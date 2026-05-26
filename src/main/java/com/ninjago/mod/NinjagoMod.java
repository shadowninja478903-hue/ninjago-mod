package com.ninjago.mod;

import com.mojang.logging.LogUtils;
import com.ninjago.mod.config.NinjagoConfig;
import com.ninjago.mod.event.ModEvents;
import com.ninjago.mod.init.ModArmorMaterials;
import com.ninjago.mod.init.ModEffects;
import com.ninjago.mod.init.ModEntities;
import com.ninjago.mod.init.ModItems;
import com.ninjago.mod.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(NinjagoMod.MOD_ID)
public class NinjagoMod {

    public static final String MOD_ID = "ninjago";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NinjagoMod(IEventBus modEventBus, ModContainer modContainer) {
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        modEventBus.addListener(ModNetwork::register);
        NeoForge.EVENT_BUS.register(ModEvents.class);
        modContainer.registerConfig(ModConfig.Type.COMMON, NinjagoConfig.SPEC);
        LOGGER.info("[Ninjago] Mod initialized. Choose your element, ninja!");
    }
}
