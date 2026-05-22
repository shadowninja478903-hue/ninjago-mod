package com.ninjago.mod.init;

import com.ninjago.mod.NinjagoMod;
import com.ninjago.mod.entity.ThrownShurikenEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, NinjagoMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownShurikenEntity>> THROWN_SHURIKEN =
        ENTITIES.register("thrown_shuriken", () ->
            EntityType.Builder.<ThrownShurikenEntity>of(ThrownShurikenEntity::new, MobCategory.MISC)
                .sized(0.25f, 0.25f)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("thrown_shuriken"));
}
