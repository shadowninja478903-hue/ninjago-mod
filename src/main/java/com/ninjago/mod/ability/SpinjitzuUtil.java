package com.ninjago.mod.ability;

import com.ninjago.mod.config.NinjagoConfig;
import com.ninjago.mod.init.ModEffects;
import com.ninjago.mod.network.SpinjitzuPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SpinjitzuUtil {

    public static void activate(Player player, Level level, String element, int color) {
        if (level.isClientSide || !(level instanceof ServerLevel sl)) return;

        int radius   = NinjagoConfig.SPINJITZU_RADIUS.get();
        double damage = NinjagoConfig.SPINJITZU_DAMAGE.get();

        player.addEffect(new MobEffectInstance(ModEffects.SPINJITZU, 40, 0));
        player.displayClientMessage(
            net.minecraft.network.chat.Component.literal("§6★ SPINJITZU (" + element.toUpperCase() + ")! ★"), true);

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
            player.getBoundingBox().inflate(radius), e -> e != player);

        for (LivingEntity e : targets)
            applyElementalSpinjitzu(player, level, e, element, damage);

        PacketDistributor.sendToPlayersNear(sl, null,
            player.getX(), player.getY(), player.getZ(), 64,
            new SpinjitzuPacket(player.getId(), element, color, radius));
    }

    private static void applyElementalSpinjitzu(Player player, Level level,
                                                 LivingEntity target, String element, double baseDmg) {
        switch (element) {
            case "earth" -> {
                target.hurt(level.damageSources().playerAttack(player), (float) baseDmg);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                double dx = target.getX()-player.getX(), dz = target.getZ()-player.getZ();
                double d = Math.sqrt(dx*dx+dz*dz);
                if (d > 0) target.setDeltaMovement(dx/d*2, 0.6, dz/d*2);
            }
            case "ice" -> {
                target.hurt(level.damageSources().freeze(), (float) baseDmg);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 10));
            }
            case "fire" -> {
                target.hurt(level.damageSources().playerAttack(player), (float) baseDmg);
                target.igniteForSeconds(8);
            }
            case "lightning" -> {
                target.hurt(level.damageSources().playerAttack(player), (float) baseDmg);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(target.getX(), target.getY(), target.getZ());
                    if (player instanceof ServerPlayer sp) bolt.setCause(sp);
                    level.addFreshEntity(bolt);
                }
            }
            case "nature" -> {
                target.hurt(level.damageSources().playerAttack(player), (float) baseDmg);
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 2));
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
            }
        }
    }
}
