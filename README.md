# Ninjago Elemental Weapons Mod
**NeoForge 1.21.1 · Both Client & Server**

## Features
| Weapon | Element | Abilities |
|--------|---------|-----------|
| Scythe | Earth 🟢 | Ground Slam · Stone Armor · Quake · Vine Trap · **Earth Spinjitzu** |
| Shuriken | Ice 🔵 | Ice Shard · Frost Nova · Blizzard · Ice Armor · **Ice Spinjitzu** |
| Katana | Fire 🔴 | Flame Dash · Burning Slash · Ember Aura · Phoenix Rise · **Fire Spinjitzu** |
| Nunchucks | Lightning 🟡 | Thunder Strike · Chain Lightning · Speed Boost · Shock Nova · **Lightning Spinjitzu** |
| Nature Staff | Nature 🟩 | Vine Whip · Nature's Embrace · Thorn Aura · Pollen Cloud · **Nature Spinjitzu** |

## Keybinds
| Key | Action |
|-----|--------|
| Right-Click | Ability 1 |
| Shift + Right-Click | Ability 2 |
| **R** | Ability 3 |
| **T** | Ability 4 |
| **Y** | ★ SPINJITZU (Ultimate) |

## Build Instructions

### Prerequisites
- Java 21 JDK
- Internet connection (to download NeoForge & Gradle)

### Steps
```bash
# 1. Clone / extract this project
cd ninjago-mod

# 2. Generate the Gradle wrapper (one-time)
gradle wrapper --gradle-version=8.8

# 3. Run initial setup (downloads NeoForge MDK)
./gradlew --refresh-dependencies

# 4. Build the mod
./gradlew build

# Output JAR will be at: build/libs/ninjago-1.0.0.jar
```

### Installing
1. Copy `build/libs/ninjago-1.0.0.jar` to your `.minecraft/mods/` folder.
2. Ensure NeoForge 21.1.172 (for MC 1.21.1) is installed.
3. Launch Minecraft.

## Configuration
Edit `config/ninjago-common.toml` (generated on first run) to adjust:
- All ability cooldowns (in ticks, 20 ticks = 1 second)
- Spinjitzu radius and damage
- Per-weapon damage bonuses

## Textures
Placeholder colored textures are included. Replace PNGs in:
`src/main/resources/assets/ninjago/textures/item/`
with your custom 16×16 artwork, then rebuild.

Armor layer textures go in:
`src/main/resources/assets/ninjago/textures/entity/armor/`
