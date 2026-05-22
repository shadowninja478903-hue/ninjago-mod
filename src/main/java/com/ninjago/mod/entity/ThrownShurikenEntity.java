package com.ninjago.mod.entity;

import com.ninjago.mod.init.ModEntities;
import com.ninjago.mod.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownShurikenEntity extends ThrowableItemProjectile {

    public ThrownShurikenEntity(Level level, LivingEntity owner) {
        super(ModEntities.THROWN_SHURIKEN.get(), owner, level);
    }

    public ThrownShurikenEntity(EntityType<ThrownShurikenEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SHURIKEN.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                getX(), getY(), getZ(), 3, 0.1, 0.1, 0.1, 0.01);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        var entity = result.getEntity();
        entity.hurt(damageSources().thrown(this, getOwner()), 8.0f);
        if (entity instanceof LivingEntity living) {
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
        }
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 20, 0.3, 0.3, 0.3, 0.1);
            sl.sendParticles(ParticleTypes.EXPLOSION,  getX(), getY(), getZ(), 1,  0, 0, 0, 0);
        }
        discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.BLOCK) {
            if (level() instanceof ServerLevel sl)
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 10, 0.2, 0.2, 0.2, 0.05);
            discard();
        }
    }

    @Override
    protected double getDefaultGravity() { return 0.01; }
}
