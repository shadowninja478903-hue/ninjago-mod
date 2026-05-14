package com.ninjago.mod.init;

import com.ninjago.mod.NinjagoMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
        DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, NinjagoMod.MOD_ID);

    /** Marks a player as currently performing Spinjitzu — triggers particle rendering on client */
    public static final DeferredHolder<MobEffect, MobEffect> SPINJITZU =
        EFFECTS.register("spinjitzu", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFFFAA00) {});

    /** Thorn aura — deals retaliatory damage when hit */
    public static final DeferredHolder<MobEffect, MobEffect> THORN_AURA =
        EFFECTS.register("thorn_aura", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF228B22) {});
}
