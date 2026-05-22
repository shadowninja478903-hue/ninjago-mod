package com.ninjago.mod.client.event;

import com.ninjago.mod.client.gui.WeaponSelectScreen;
import com.ninjago.mod.network.OpenWeaponSelectPacket;
import com.ninjago.mod.network.ParticlePacket;
import com.ninjago.mod.network.SpinjitzuPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@OnlyIn(Dist.CLIENT)
public class ClientNetworkHandler {

    public static void handleOpenWeaponSelect(OpenWeaponSelectPacket p, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(new WeaponSelectScreen()));
    }

    public static void handleSpinjitzuPacket(SpinjitzuPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity e = mc.level.getEntity(packet.entityId());
            if (e == null) return;
            double x=e.getX(), y=e.getY()+1, z=e.getZ();
            int r = packet.radius();
            var pt = switch(packet.element()) {
                case "fire"      -> ParticleTypes.FLAME;
                case "ice"       -> ParticleTypes.SNOWFLAKE;
                case "lightning" -> ParticleTypes.ELECTRIC_SPARK;
                case "nature"    -> ParticleTypes.HAPPY_VILLAGER;
                default          -> ParticleTypes.EXPLOSION;
            };
            for (int i=0; i<80; i++) {
                double a=(Math.PI*2/80)*i;
                double rr=r*(0.5+mc.level.random.nextDouble()*0.5);
                mc.level.addParticle(pt, x+Math.sin(a)*rr, y+(mc.level.random.nextDouble()-0.3)*3,
                    z+Math.cos(a)*rr, 0,0.1,0);
            }
            for (int i=0; i<5; i++)
                mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                    x+(mc.level.random.nextDouble()-0.5)*2, y, z+(mc.level.random.nextDouble()-0.5)*2,0,0,0);
        });
    }

    public static void handleParticlePacket(ParticlePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity e = mc.level.getEntity(packet.entityId());
            if (e == null) return;
            double x=e.getX(), y=e.getY()+0.5, z=e.getZ();
            String el = packet.element();
            int ab = packet.abilityIndex();

            // Pick particle type per element
            var main = switch(el) {
                case "fire"      -> ParticleTypes.FLAME;
                case "ice"       -> ParticleTypes.SNOWFLAKE;
                case "lightning" -> ParticleTypes.ELECTRIC_SPARK;
                case "nature"    -> ParticleTypes.HAPPY_VILLAGER;
                default          -> ParticleTypes.EXPLOSION;
            };
            var secondary = switch(el) {
                case "fire"      -> ParticleTypes.LAVA;
                case "ice"       -> ParticleTypes.ITEM_SNOWBALL;
                case "lightning" -> ParticleTypes.FIREWORK;
                case "nature"    -> ParticleTypes.HAPPY_VILLAGER;
                default          -> ParticleTypes.CLOUD;
            };

            if (ab == 4) {
                // Spinjitzu — already handled by SpinjitzuPacket
                return;
            }

            // Burst ring
            int count = switch(ab) { case 0->20; case 1->15; case 2->30; case 3->25; default->10; };
            for (int i=0; i<count; i++) {
                double a = (Math.PI*2/count)*i;
                double r = 1.5 + mc.level.random.nextDouble()*2;
                mc.level.addParticle(main,
                    x+Math.sin(a)*r, y+mc.level.random.nextDouble()*1.5, z+Math.cos(a)*r,
                    Math.sin(a)*0.1, 0.05, Math.cos(a)*0.1);
            }
            // Central burst
            for (int i=0; i<8; i++)
                mc.level.addParticle(secondary,
                    x+(mc.level.random.nextDouble()-0.5), y+mc.level.random.nextDouble(),
                    z+(mc.level.random.nextDouble()-0.5), 0,0.05,0);

            // Element-specific extras
            switch(el) {
                case "fire" -> {
                    for (int i=0;i<10;i++)
                        mc.level.addParticle(ParticleTypes.LARGE_SMOKE,
                            x+(mc.level.random.nextDouble()-0.5)*3,y+mc.level.random.nextDouble()*2,
                            z+(mc.level.random.nextDouble()-0.5)*3,0,0.02,0);
                }
                case "lightning" -> {
                    for (int i=0;i<6;i++)
                        mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                            x+(mc.level.random.nextDouble()-0.5)*4,y+mc.level.random.nextDouble()*3,
                            z+(mc.level.random.nextDouble()-0.5)*4,
                            (mc.level.random.nextDouble()-0.5)*0.3,0.1,(mc.level.random.nextDouble()-0.5)*0.3);
                }
                case "nature" -> {
                    for (int i=0;i<12;i++)
                        mc.level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR,
                            x+(mc.level.random.nextDouble()-0.5)*4,y+mc.level.random.nextDouble()*2,
                            z+(mc.level.random.nextDouble()-0.5)*4,0,-0.01,0);
                }
                case "earth" -> {
                    for (int i=0;i<10;i++)
                        mc.level.addParticle(ParticleTypes.CLOUD,
                            x+(mc.level.random.nextDouble()-0.5)*4,y+2,
                            z+(mc.level.random.nextDouble()-0.5)*4,0,-0.1,0);
                }
            }
        });
    }
}
