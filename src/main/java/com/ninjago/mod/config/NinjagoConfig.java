package com.ninjago.mod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NinjagoConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // ── General ──────────────────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue SPINJITZU_COOLDOWN;
    public static final ModConfigSpec.IntValue SPINJITZU_RADIUS;
    public static final ModConfigSpec.DoubleValue SPINJITZU_DAMAGE;

    // ── Scythe (Earth) ───────────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue SCYTHE_ABILITY1_COOLDOWN;
    public static final ModConfigSpec.IntValue SCYTHE_ABILITY2_COOLDOWN;
    public static final ModConfigSpec.IntValue SCYTHE_ABILITY3_COOLDOWN;
    public static final ModConfigSpec.IntValue SCYTHE_ABILITY4_COOLDOWN;
    public static final ModConfigSpec.DoubleValue SCYTHE_DAMAGE_BONUS;

    // ── Shuriken (Ice) ───────────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue SHURIKEN_ABILITY1_COOLDOWN;
    public static final ModConfigSpec.IntValue SHURIKEN_ABILITY2_COOLDOWN;
    public static final ModConfigSpec.IntValue SHURIKEN_ABILITY3_COOLDOWN;
    public static final ModConfigSpec.IntValue SHURIKEN_ABILITY4_COOLDOWN;
    public static final ModConfigSpec.DoubleValue SHURIKEN_DAMAGE_BONUS;

    // ── Katana (Fire) ────────────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue KATANA_ABILITY1_COOLDOWN;
    public static final ModConfigSpec.IntValue KATANA_ABILITY2_COOLDOWN;
    public static final ModConfigSpec.IntValue KATANA_ABILITY3_COOLDOWN;
    public static final ModConfigSpec.IntValue KATANA_ABILITY4_COOLDOWN;
    public static final ModConfigSpec.DoubleValue KATANA_DAMAGE_BONUS;

    // ── Nunchucks (Lightning) ────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue NUNCHUCKS_ABILITY1_COOLDOWN;
    public static final ModConfigSpec.IntValue NUNCHUCKS_ABILITY2_COOLDOWN;
    public static final ModConfigSpec.IntValue NUNCHUCKS_ABILITY3_COOLDOWN;
    public static final ModConfigSpec.IntValue NUNCHUCKS_ABILITY4_COOLDOWN;
    public static final ModConfigSpec.DoubleValue NUNCHUCKS_DAMAGE_BONUS;

    // ── Staff (Nature) ───────────────────────────────────────────────────────
    public static final ModConfigSpec.IntValue STAFF_ABILITY1_COOLDOWN;
    public static final ModConfigSpec.IntValue STAFF_ABILITY2_COOLDOWN;
    public static final ModConfigSpec.IntValue STAFF_ABILITY3_COOLDOWN;
    public static final ModConfigSpec.IntValue STAFF_ABILITY4_COOLDOWN;
    public static final ModConfigSpec.DoubleValue STAFF_DAMAGE_BONUS;

    static {
        BUILDER.comment("Ninjago Mod Configuration").push("general");
        SPINJITZU_COOLDOWN = BUILDER.comment("Spinjitzu cooldown in ticks (default 1200 = 60 seconds)")
            .defineInRange("spinjitzuCooldown", 1200, 100, 6000);
        SPINJITZU_RADIUS = BUILDER.comment("Spinjitzu blast radius in blocks")
            .defineInRange("spinjitzuRadius", 12, 4, 30);
        SPINJITZU_DAMAGE = BUILDER.comment("Spinjitzu base damage")
            .defineInRange("spinjitzuDamage", 120.0, 1.0, 500.0);
        BUILDER.pop();

        BUILDER.push("scythe_earth");
        SCYTHE_ABILITY1_COOLDOWN = BUILDER.comment("Ground Slam cooldown (ticks)").defineInRange("ability1Cooldown", 100, 20, 400);
        SCYTHE_ABILITY2_COOLDOWN = BUILDER.comment("Stone Armor cooldown (ticks)").defineInRange("ability2Cooldown", 200, 20, 600);
        SCYTHE_ABILITY3_COOLDOWN = BUILDER.comment("Quake cooldown (ticks)").defineInRange("ability3Cooldown", 160, 20, 500);
        SCYTHE_ABILITY4_COOLDOWN = BUILDER.comment("Vine Trap cooldown (ticks)").defineInRange("ability4Cooldown", 180, 20, 500);
        SCYTHE_DAMAGE_BONUS = BUILDER.comment("Scythe base damage bonus").defineInRange("damageBonus", 7.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.push("shuriken_ice");
        SHURIKEN_ABILITY1_COOLDOWN = BUILDER.comment("Ice Shard cooldown (ticks)").defineInRange("ability1Cooldown", 60, 20, 400);
        SHURIKEN_ABILITY2_COOLDOWN = BUILDER.comment("Frost Nova cooldown (ticks)").defineInRange("ability2Cooldown", 160, 20, 600);
        SHURIKEN_ABILITY3_COOLDOWN = BUILDER.comment("Blizzard cooldown (ticks)").defineInRange("ability3Cooldown", 200, 20, 500);
        SHURIKEN_ABILITY4_COOLDOWN = BUILDER.comment("Ice Armor cooldown (ticks)").defineInRange("ability4Cooldown", 240, 20, 600);
        SHURIKEN_DAMAGE_BONUS = BUILDER.comment("Shuriken base damage bonus").defineInRange("damageBonus", 5.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.push("katana_fire");
        KATANA_ABILITY1_COOLDOWN = BUILDER.comment("Flame Dash cooldown (ticks)").defineInRange("ability1Cooldown", 80, 20, 400);
        KATANA_ABILITY2_COOLDOWN = BUILDER.comment("Burning Slash cooldown (ticks)").defineInRange("ability2Cooldown", 100, 20, 400);
        KATANA_ABILITY3_COOLDOWN = BUILDER.comment("Ember Aura cooldown (ticks)").defineInRange("ability3Cooldown", 180, 20, 500);
        KATANA_ABILITY4_COOLDOWN = BUILDER.comment("Phoenix Rise cooldown (ticks)").defineInRange("ability4Cooldown", 300, 20, 600);
        KATANA_DAMAGE_BONUS = BUILDER.comment("Katana base damage bonus").defineInRange("damageBonus", 6.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.push("nunchucks_lightning");
        NUNCHUCKS_ABILITY1_COOLDOWN = BUILDER.comment("Thunder Strike cooldown (ticks)").defineInRange("ability1Cooldown", 80, 20, 400);
        NUNCHUCKS_ABILITY2_COOLDOWN = BUILDER.comment("Chain Lightning cooldown (ticks)").defineInRange("ability2Cooldown", 160, 20, 600);
        NUNCHUCKS_ABILITY3_COOLDOWN = BUILDER.comment("Speed Boost cooldown (ticks)").defineInRange("ability3Cooldown", 200, 20, 500);
        NUNCHUCKS_ABILITY4_COOLDOWN = BUILDER.comment("Shock Nova cooldown (ticks)").defineInRange("ability4Cooldown", 140, 20, 500);
        NUNCHUCKS_DAMAGE_BONUS = BUILDER.comment("Nunchucks base damage bonus").defineInRange("damageBonus", 5.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.push("staff_nature");
        STAFF_ABILITY1_COOLDOWN = BUILDER.comment("Vine Whip cooldown (ticks)").defineInRange("ability1Cooldown", 80, 20, 400);
        STAFF_ABILITY2_COOLDOWN = BUILDER.comment("Nature's Embrace cooldown (ticks)").defineInRange("ability2Cooldown", 200, 20, 600);
        STAFF_ABILITY3_COOLDOWN = BUILDER.comment("Thorn Aura cooldown (ticks)").defineInRange("ability3Cooldown", 180, 20, 500);
        STAFF_ABILITY4_COOLDOWN = BUILDER.comment("Pollen Cloud cooldown (ticks)").defineInRange("ability4Cooldown", 160, 20, 500);
        STAFF_DAMAGE_BONUS = BUILDER.comment("Staff base damage bonus").defineInRange("damageBonus", 4.0, 0.0, 20.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
