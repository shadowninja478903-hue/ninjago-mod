package com.ninjago.mod.item;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Ninja armor — abilities are on the CHESTPLATE only (keybinds R/T/Y).
 * The chestplate detects element from its own "element" field.
 */
public class NinjaArmorItem extends ArmorItem {

    private static final String NBT_ROOT     = "ninjago";
    private static final String NBT_COOLDOWN = "armor_cd_";

    private final String element;

    public NinjaArmorItem(Holder<ArmorMaterial> material, Type type, String element, Properties props) {
        super(material, type, props);
        this.element = element;
    }

    public String getElement() { return element; }
    public boolean isNinjaArmor() { return true; }

    // ── Cooldown helpers ──────────────────────────────────────────────────────

    public boolean isOnCooldown(Player player, int index) {
        CompoundTag tag = getTag(player);
        return player.level().getGameTime() < tag.getLong(NBT_COOLDOWN + index);
    }

    public long getRemaining(Player player, int index) {
        CompoundTag tag = getTag(player);
        return Math.max(0, tag.getLong(NBT_COOLDOWN + index) - player.level().getGameTime());
    }

    public void setCooldown(Player player, int index, int ticks) {
        CompoundTag root = player.getPersistentData();
        CompoundTag tag  = root.contains(NBT_ROOT) ? root.getCompound(NBT_ROOT) : new CompoundTag();
        tag.putLong(NBT_COOLDOWN + index, player.level().getGameTime() + ticks);
        root.put(NBT_ROOT, tag);
    }

    private CompoundTag getTag(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.contains(NBT_ROOT) ? root.getCompound(NBT_ROOT) : new CompoundTag();
    }

    // ── Ability activation (called from ModEvents via keybind packet) ─────────

    public boolean tryActivate(Player player, Level level, int index) {
        if (level.isClientSide) return false;
        if (isOnCooldown(player, index)) {
            long rem = getRemaining(player, index);
            player.displayClientMessage(
                Component.literal("§c[" + getAbilityName(index) + "] cooldown: " + (rem/20) + "s"), true);
            return false;
        }
        activateAbility(player, level, index);
        setCooldown(player, index, getCooldown(index));
        return true;
    }

    private String getAbilityName(int i) {
        return switch (element) {
            case "earth"     -> switch(i){case 0->"Ground Slam";case 1->"Stone Armor";case 2->"Quake";case 3->"Vine Trap";default->"Earth Spinjitzu";};
            case "ice"       -> switch(i){case 0->"Ice Shard";case 1->"Frost Nova";case 2->"Blizzard";case 3->"Ice Armor";default->"Ice Spinjitzu";};
            case "fire"      -> switch(i){case 0->"Flame Dash";case 1->"Burn Slash";case 2->"Ember Aura";case 3->"Phoenix Rise";default->"Fire Spinjitzu";};
            case "lightning" -> switch(i){case 0->"Thunder";case 1->"Chain Lightning";case 2->"Speed Boost";case 3->"Shock Nova";default->"Lightning Spinjitzu";};
            case "nature"    -> switch(i){case 0->"Vine Whip";case 1->"Embrace";case 2->"Thorn Aura";case 3->"Pollen Cloud";default->"Nature Spinjitzu";};
            default -> "Ability " + i;
        };
    }

    private int getCooldown(int index) {
        return switch (index) {
            case 0 -> 100;
            case 1 -> 180;
            case 2 -> 160;
            case 3 -> 200;
            default -> 1200; // spinjitzu
        };
    }

    private void activateAbility(Player player, Level level, int index) {
        switch (element) {
            case "earth"     -> activateEarth(player, level, index);
            case "ice"       -> activateIce(player, level, index);
            case "fire"      -> activateFire(player, level, index);
            case "lightning" -> activateLightning(player, level, index);
            case "nature"    -> activateNature(player, level, index);
        }
        // Send particle packet to all nearby clients
        com.ninjago.mod.network.ParticlePacket.send(player, level, element, index);
    }

    private AABB box(Player p, double r) { return p.getBoundingBox().inflate(r); }

    // ── EARTH ─────────────────────────────────────────────────────────────────
    private void activateEarth(Player p, Level l, int i) {
        switch (i) {
            case 0 -> { // Ground Slam
                l.getEntitiesOfClass(LivingEntity.class, box(p,4), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p),8f);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,80,2));
                });
                p.displayClientMessage(Component.literal("§2⚙ Ground Slam!"),true);
            }
            case 1 -> { // Stone Armor
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,200,1));
                p.displayClientMessage(Component.literal("§2🪨 Stone Armor!"),true);
            }
            case 2 -> { // Quake
                l.getEntitiesOfClass(LivingEntity.class, box(p,6), e->e!=p).forEach(e->{
                    double dx=e.getX()-p.getX(), dz=e.getZ()-p.getZ();
                    double d=Math.sqrt(dx*dx+dz*dz);
                    if(d>0) e.setDeltaMovement(dx/d*2,0.6,dz/d*2);
                    e.hurt(l.damageSources().playerAttack(p),5f);
                });
                p.displayClientMessage(Component.literal("§2🌍 Quake!"),true);
            }
            case 3 -> { // Vine Trap
                l.getEntitiesOfClass(LivingEntity.class, box(p,5), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,120,4));
                    e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,120,2));
                });
                p.displayClientMessage(Component.literal("§2🌿 Vine Trap!"),true);
            }
            default -> { // Spinjitzu
                com.ninjago.mod.ability.SpinjitzuUtil.activate(p,l,"earth",0x228B22);
            }
        }
    }

    // ── ICE ───────────────────────────────────────────────────────────────────
    private void activateIce(Player p, Level l, int i) {
        switch (i) {
            case 0 -> { // Ice Shard (throws a shuriken)
                if (!l.isClientSide) {
                    com.ninjago.mod.entity.ThrownShurikenEntity s =
                        new com.ninjago.mod.entity.ThrownShurikenEntity(l, p);
                    s.shootFromRotation(p, p.getXRot(), p.getYRot(), 0f, 2.5f, 0.5f);
                    l.addFreshEntity(s);
                }
                p.displayClientMessage(Component.literal("§b❄ Ice Shard!"),true);
            }
            case 1 -> { // Frost Nova
                l.getEntitiesOfClass(LivingEntity.class, box(p,5), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,80,10));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,80,1));
                });
                p.displayClientMessage(Component.literal("§b❄ Frost Nova!"),true);
            }
            case 2 -> { // Blizzard
                l.getEntitiesOfClass(LivingEntity.class, box(p,7), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().freeze(),6f);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,120,2));
                });
                p.displayClientMessage(Component.literal("§b🌨 Blizzard!"),true);
            }
            case 3 -> { // Ice Armor
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,160,1));
                p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,160,1));
                p.displayClientMessage(Component.literal("§b🛡 Ice Armor!"),true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p,l,"ice",0x00FFFF);
        }
    }

    // ── FIRE ──────────────────────────────────────────────────────────────────
    private void activateFire(Player p, Level l, int i) {
        switch (i) {
            case 0 -> { // Flame Dash
                var look = p.getLookAngle().normalize().scale(6);
                p.setDeltaMovement(look.x,0.3,look.z);
                p.igniteForSeconds(3);
                p.displayClientMessage(Component.literal("§c🔥 Flame Dash!"),true);
            }
            case 1 -> { // Burning Slash
                l.getEntitiesOfClass(LivingEntity.class, box(p,5), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p),10f);
                    e.igniteForSeconds(5);
                });
                p.displayClientMessage(Component.literal("§c⚔ Burning Slash!"),true);
            }
            case 2 -> { // Ember Aura
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,200,1));
                p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,200,0));
                p.displayClientMessage(Component.literal("§c🔥 Ember Aura!"),true);
            }
            case 3 -> { // Phoenix Rise
                p.heal(20f);
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,100,2));
                p.displayClientMessage(Component.literal("§c🦅 Phoenix Rise!"),true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p,l,"fire",0xFF4500);
        }
    }

    // ── LIGHTNING ─────────────────────────────────────────────────────────────
    private void activateLightning(Player p, Level l, int i) {
        switch (i) {
            case 0 -> { // Thunder Strike
                l.getEntitiesOfClass(LivingEntity.class, box(p,15), e->e!=p)
                    .stream().min((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .ifPresent(t -> spawnLightning(l,p,t));
                p.displayClientMessage(Component.literal("§e⚡ Thunder Strike!"),true);
            }
            case 1 -> { // Chain Lightning
                l.getEntitiesOfClass(LivingEntity.class, box(p,12), e->e!=p)
                    .stream().sorted((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .limit(3).forEach(t->spawnLightning(l,p,t));
                p.displayClientMessage(Component.literal("§e⚡ Chain Lightning!"),true);
            }
            case 2 -> { // Speed Boost
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,100,3));
                p.addEffect(new MobEffectInstance(MobEffects.JUMP,100,2));
                p.displayClientMessage(Component.literal("§e💨 Speed Boost!"),true);
            }
            case 3 -> { // Shock Nova
                l.getEntitiesOfClass(LivingEntity.class, box(p,6), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p),7f);
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,80,1));
                });
                p.displayClientMessage(Component.literal("§e⚡ Shock Nova!"),true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p,l,"lightning",0xFFFF00);
        }
    }

    private void spawnLightning(Level l, Player p, LivingEntity t) {
        var bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(l);
        if (bolt != null) {
            bolt.moveTo(t.getX(),t.getY(),t.getZ());
            if (p instanceof net.minecraft.server.level.ServerPlayer sp) bolt.setCause(sp);
            l.addFreshEntity(bolt);
        }
    }

    // ── NATURE ────────────────────────────────────────────────────────────────
    private void activateNature(Player p, Level l, int i) {
        switch (i) {
            case 0 -> { // Vine Whip
                l.getEntitiesOfClass(LivingEntity.class, box(p,12), e->e!=p)
                    .stream().min((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .ifPresent(t->{
                        double dx=p.getX()-t.getX(),dy=p.getY()-t.getY()+0.5,dz=p.getZ()-t.getZ();
                        double d=Math.sqrt(dx*dx+dy*dy+dz*dz);
                        if(d>0) t.setDeltaMovement(dx/d*1.5,dy/d*1.5,dz/d*1.5);
                    });
                p.displayClientMessage(Component.literal("§2🌿 Vine Whip!"),true);
            }
            case 1 -> { // Nature's Embrace
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,2));
                p.addEffect(new MobEffectInstance(MobEffects.SATURATION,100,1));
                p.displayClientMessage(Component.literal("§2💚 Nature's Embrace!"),true);
            }
            case 2 -> { // Thorn Aura
                p.addEffect(new MobEffectInstance(com.ninjago.mod.init.ModEffects.THORN_AURA,180,0));
                p.displayClientMessage(Component.literal("§2🌵 Thorn Aura!"),true);
            }
            case 3 -> { // Pollen Cloud
                l.getEntitiesOfClass(LivingEntity.class, box(p,6), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100,0));
                    e.addEffect(new MobEffectInstance(MobEffects.POISON,100,1));
                });
                p.displayClientMessage(Component.literal("§2🌸 Pollen Cloud!"),true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p,l,"nature",0x00AA00);
        }
    }

    // ── Tooltip ───────────────────────────────────────────────────────────────
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack,ctx,tip,flag);
        if (this.getType() == Type.CHESTPLATE) {
            tip.add(Component.literal(" "));
            tip.add(Component.literal("§l§6" + element.toUpperCase() + " ABILITIES (Chestplate)"));
            tip.add(Component.literal("§7[R]   §f" + getAbilityName(0)));
            tip.add(Component.literal("§7[T]   §f" + getAbilityName(1)));
            tip.add(Component.literal("§7[R+Sh]§f" + getAbilityName(2)));
            tip.add(Component.literal("§7[T+Sh]§f" + getAbilityName(3)));
            tip.add(Component.literal("§6[Y]   §e★ " + getAbilityName(4) + " (SPINJITZU)"));
        }
    }
}
