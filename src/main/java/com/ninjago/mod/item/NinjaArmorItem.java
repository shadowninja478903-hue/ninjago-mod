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

public class NinjaArmorItem extends ArmorItem {

    private static final String NBT_ROOT     = "ninjago";
    private static final String NBT_COOLDOWN = "armor_cd_";

    private final String element;

    public NinjaArmorItem(Holder<ArmorMaterial> material, Type type, String element, Properties props) {
        super(material, type, props.rarity(net.minecraft.world.item.Rarity.EPIC));
        this.element = element;
    }

    public String getElement() { return element; }
    public boolean isNinjaArmor() { return true; }

    // ── UNBREAKABLE — armor never loses durability ────────────────────────────
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairItem) { return false; }

    @Override
    public <T extends net.minecraft.world.item.Item> boolean canBeDepleted() { return false; }

    // ── Set bonus detection ───────────────────────────────────────────────────
    public static boolean hasFullSet(Player player, String element) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack s = player.getItemBySlot(slot);
            if (!(s.getItem() instanceof NinjaArmorItem a) || !a.getElement().equals(element))
                return false;
        }
        return true;
    }

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
        // Set bonus: 20% cooldown reduction when wearing full set
        int cd = hasFullSet(player, element) ? (int)(ticks * 0.8f) : ticks;
        tag.putLong(NBT_COOLDOWN + index, player.level().getGameTime() + cd);
        root.put(NBT_ROOT, tag);
    }

    private CompoundTag getTag(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.contains(NBT_ROOT) ? root.getCompound(NBT_ROOT) : new CompoundTag();
    }

    // ── Damage multiplier when full set is worn ───────────────────────────────
    public float getDamageMultiplier(Player player) {
        return hasFullSet(player, element) ? 1.5f : 1.0f;
    }

    // ── Ability activation ────────────────────────────────────────────────────
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
            case 0 -> 100; case 1 -> 180; case 2 -> 160; case 3 -> 200;
            default -> 1200;
        };
    }

    private void activateAbility(Player player, Level level, int index) {
        float dmg = getDamageMultiplier(player);
        boolean fullSet = hasFullSet(player, element);
        switch (element) {
            case "earth"     -> activateEarth(player, level, index, dmg, fullSet);
            case "ice"       -> activateIce(player, level, index, dmg, fullSet);
            case "fire"      -> activateFire(player, level, index, dmg, fullSet);
            case "lightning" -> activateLightning(player, level, index, dmg, fullSet);
            case "nature"    -> activateNature(player, level, index, dmg, fullSet);
        }
        com.ninjago.mod.network.ParticlePacket.send(player, level, element, index);
    }

    private AABB box(Player p, double r) { return p.getBoundingBox().inflate(r); }

    // ── EARTH ─────────────────────────────────────────────────────────────────
    private void activateEarth(Player p, Level l, int i, float dmg, boolean full) {
        switch (i) {
            case 0 -> {
                double r = full ? 6.0 : 4.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p), 10f * dmg);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                    if (full) e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
                });
                p.displayClientMessage(Component.literal("§2⚙ Ground Slam" + (full?"§a [SET BONUS]":"")), true);
            }
            case 1 -> {
                int dur = full ? 300 : 200; int amp = full ? 2 : 1;
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, dur, amp));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                p.displayClientMessage(Component.literal("§2🪨 Stone Armor" + (full?"§a [SET BONUS]":"")), true);
            }
            case 2 -> {
                double r = full ? 9.0 : 6.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    double dx=e.getX()-p.getX(), dz=e.getZ()-p.getZ();
                    double d=Math.sqrt(dx*dx+dz*dz);
                    if(d>0) e.setDeltaMovement(dx/d*2.5, 0.8, dz/d*2.5);
                    e.hurt(l.damageSources().playerAttack(p), 8f * dmg);
                });
                p.displayClientMessage(Component.literal("§2🌍 Quake!"), true);
            }
            case 3 -> {
                double r = full ? 8.0 : 5.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 4));
                    e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 3));
                    if (full) e.hurt(l.damageSources().playerAttack(p), 4f);
                });
                p.displayClientMessage(Component.literal("§2🌿 Vine Trap!"), true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p, l, "earth", 0x228B22);
        }
    }

    // ── ICE ───────────────────────────────────────────────────────────────────
    private void activateIce(Player p, Level l, int i, float dmg, boolean full) {
        switch (i) {
            case 0 -> {
                if (!l.isClientSide) {
                    var s = new com.ninjago.mod.entity.ThrownShurikenEntity(l, p);
                    s.shootFromRotation(p, p.getXRot(), p.getYRot(), 0f, full ? 3.5f : 2.5f, 0.3f);
                    l.addFreshEntity(s);
                    if (full) { // Fire a second shuriken slightly offset
                        var s2 = new com.ninjago.mod.entity.ThrownShurikenEntity(l, p);
                        s2.shootFromRotation(p, p.getXRot(), p.getYRot() + 10, 0f, 3.0f, 0.3f);
                        l.addFreshEntity(s2);
                    }
                }
                p.displayClientMessage(Component.literal("§b❄ Ice Shard" + (full?" x2":"") + "!"), true);
            }
            case 1 -> {
                double r = full ? 8.0 : 5.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 10));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, full?2:1));
                    if (full) e.hurt(l.damageSources().freeze(), 4f);
                });
                p.displayClientMessage(Component.literal("§b❄ Frost Nova!"), true);
            }
            case 2 -> {
                double r = full ? 10.0 : 7.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().freeze(), 8f * dmg);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 3));
                });
                p.displayClientMessage(Component.literal("§b🌨 Blizzard!"), true);
            }
            case 3 -> {
                int dur = full ? 240 : 160; int abs = full ? 3 : 1;
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, dur, 1));
                p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, dur, abs));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 1));
                p.displayClientMessage(Component.literal("§b🛡 Ice Armor!"), true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p, l, "ice", 0x00FFFF);
        }
    }

    // ── FIRE ──────────────────────────────────────────────────────────────────
    private void activateFire(Player p, Level l, int i, float dmg, boolean full) {
        switch (i) {
            case 0 -> {
                double scale = full ? 9.0 : 6.0;
                var look = p.getLookAngle().normalize().scale(scale);
                p.setDeltaMovement(look.x, full ? 0.5 : 0.3, look.z);
                p.igniteForSeconds(full ? 5 : 3);
                // Burn anyone in the dash path
                if (full) l.getEntitiesOfClass(LivingEntity.class, box(p, 3), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p), 6f);
                    e.igniteForSeconds(3);
                });
                p.displayClientMessage(Component.literal("§c🔥 Flame Dash!"), true);
            }
            case 1 -> {
                double r = full ? 8.0 : 5.0; int secs = full ? 8 : 5;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p), 14f * dmg);
                    e.igniteForSeconds(secs);
                });
                p.displayClientMessage(Component.literal("§c⚔ Burning Slash!"), true);
            }
            case 2 -> {
                int dur = full ? 300 : 200; int amp = full ? 2 : 1;
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, dur, amp));
                p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, dur, 0));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, dur, 1));
                p.getPersistentData().putLong("ninjago_ember_aura_end", l.getGameTime() + dur);
                p.displayClientMessage(Component.literal("§c🔥 Ember Aura!"), true);
            }
            case 3 -> {
                p.heal(full ? 30f : 20f);
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, full ? 200 : 100, 2));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
                p.displayClientMessage(Component.literal("§c🦅 Phoenix Rise!"), true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p, l, "fire", 0xFF4500);
        }
    }

    // ── LIGHTNING ─────────────────────────────────────────────────────────────
    private void activateLightning(Player p, Level l, int i, float dmg, boolean full) {
        switch (i) {
            case 0 -> {
                int hits = full ? 3 : 1;
                l.getEntitiesOfClass(LivingEntity.class, box(p, full?20:15), e->e!=p)
                    .stream().sorted((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .limit(hits).forEach(t->spawnLightning(l,p,t));
                p.displayClientMessage(Component.literal("§e⚡ Thunder Strike" + (full?" x3":"") + "!"), true);
            }
            case 1 -> {
                int hits = full ? 5 : 3;
                l.getEntitiesOfClass(LivingEntity.class, box(p, 14), e->e!=p)
                    .stream().sorted((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .limit(hits).forEach(t->spawnLightning(l,p,t));
                p.displayClientMessage(Component.literal("§e⚡ Chain Lightning x" + hits + "!"), true);
            }
            case 2 -> {
                int amp = full ? 4 : 3; int jmp = full ? 3 : 2;
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, amp));
                p.addEffect(new MobEffectInstance(MobEffects.JUMP, 120, jmp));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 1));
                p.displayClientMessage(Component.literal("§e💨 Speed Boost!"), true);
            }
            case 3 -> {
                double r = full ? 9.0 : 6.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.hurt(l.damageSources().playerAttack(p), 10f * dmg);
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, full?2:1));
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                    if (full) spawnLightning(l, p, e);
                });
                p.displayClientMessage(Component.literal("§e⚡ Shock Nova!"), true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p, l, "lightning", 0xFFFF00);
        }
    }

    private void spawnLightning(Level l, Player p, LivingEntity t) {
        var bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(l);
        if (bolt != null) {
            bolt.moveTo(t.getX(), t.getY(), t.getZ());
            if (p instanceof net.minecraft.server.level.ServerPlayer sp) bolt.setCause(sp);
            l.addFreshEntity(bolt);
        }
    }

    // ── NATURE ────────────────────────────────────────────────────────────────
    private void activateNature(Player p, Level l, int i, float dmg, boolean full) {
        switch (i) {
            case 0 -> {
                double r = full ? 16.0 : 12.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p)
                    .stream().sorted((a,b)->Double.compare(a.distanceTo(p),b.distanceTo(p)))
                    .limit(full ? 3 : 1).forEach(t->{
                        double dx=p.getX()-t.getX(), dy=p.getY()-t.getY()+0.5, dz=p.getZ()-t.getZ();
                        double d=Math.sqrt(dx*dx+dy*dy+dz*dz);
                        if(d>0) t.setDeltaMovement(dx/d*1.8, dy/d*1.8, dz/d*1.8);
                    });
                p.displayClientMessage(Component.literal("§2🌿 Vine Whip" + (full?" x3":"") + "!"), true);
            }
            case 1 -> {
                int amp = full ? 3 : 2;
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, full?300:200, amp));
                p.addEffect(new MobEffectInstance(MobEffects.SATURATION, 150, 1));
                if (full) {
                    p.heal(10f);
                    p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
                }
                p.displayClientMessage(Component.literal("§2💚 Nature's Embrace!"), true);
            }
            case 2 -> {
                p.addEffect(new MobEffectInstance(com.ninjago.mod.init.ModEffects.THORN_AURA, full?300:180, full?1:0));
                if (full) p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                p.displayClientMessage(Component.literal("§2🌵 Thorn Aura!"), true);
            }
            case 3 -> {
                double r = full ? 9.0 : 6.0;
                l.getEntitiesOfClass(LivingEntity.class, box(p,r), e->e!=p).forEach(e->{
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, full?160:100, 0));
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, full?160:100, full?2:1));
                    if (full) e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
                });
                p.displayClientMessage(Component.literal("§2🌸 Pollen Cloud!"), true);
            }
            default -> com.ninjago.mod.ability.SpinjitzuUtil.activate(p, l, "nature", 0x00AA00);
        }
    }

    // ── Tooltip ───────────────────────────────────────────────────────────────
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal(" "));
        tip.add(Component.literal("§l§6" + element.toUpperCase() + " NINJA ARMOR"));
        tip.add(Component.literal("§7Abilities trigger from §fchestplate§7 via keybinds."));
        tip.add(Component.literal("§7[R] Ab1  [T] Ab2  [Sh+R] Ab3  [Sh+T] Ab4  [Y] Spinjitzu"));
        tip.add(Component.literal(" "));
        tip.add(Component.literal("§6★ SET BONUS (full set):"));
        tip.add(Component.literal("§7• §f+50% ability damage"));
        tip.add(Component.literal("§7• §f-20% cooldowns"));
        tip.add(Component.literal("§7• §fEnhanced ability effects"));
        tip.add(Component.literal(" "));
        tip.add(Component.literal("§a✦ Unbreakable — never loses durability"));
        if (this.getType() == Type.CHESTPLATE) {
            tip.add(Component.literal(" "));
            tip.add(Component.literal("§e[Abilities]"));
            tip.add(Component.literal("§7" + getAbilityName(0) + " / " + getAbilityName(1)));
            tip.add(Component.literal("§7" + getAbilityName(2) + " / " + getAbilityName(3)));
            tip.add(Component.literal("§6★ " + getAbilityName(4)));
        }
    }
}
