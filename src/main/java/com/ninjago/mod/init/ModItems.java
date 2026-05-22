package com.ninjago.mod.init;

import com.ninjago.mod.item.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems("ninjago");

    // ── Weapons (passive — abilities now come from armor) ─────────────────────
    public static final DeferredItem<ScytheItem>      SCYTHE      = ITEMS.register("scythe",      ScytheItem::new);
    public static final DeferredItem<ShurikenItem>    SHURIKEN    = ITEMS.register("shuriken",    ShurikenItem::new);
    public static final DeferredItem<KatanaItem>      KATANA      = ITEMS.register("katana",      KatanaItem::new);
    public static final DeferredItem<NunchucksItem>   NUNCHUCKS   = ITEMS.register("nunchucks",   NunchucksItem::new);
    public static final DeferredItem<NatureStaffItem> NATURE_STAFF= ITEMS.register("nature_staff",NatureStaffItem::new);

    // ── Throwable Shuriken projectile item ────────────────────────────────────
    public static final DeferredItem<Item> SHURIKEN_THROW =
        ITEMS.register("shuriken_throw", () -> new Item(new Item.Properties().stacksTo(16)));

    // ── Earth Armor ───────────────────────────────────────────────────────────
    public static final DeferredItem<NinjaArmorItem> EARTH_HELMET =
        ITEMS.register("earth_helmet", () -> new NinjaArmorItem(ModArmorMaterials.EARTH, ArmorItem.Type.HELMET, "earth", new Item.Properties().durability(407)));
    public static final DeferredItem<NinjaArmorItem> EARTH_CHESTPLATE =
        ITEMS.register("earth_chestplate", () -> new NinjaArmorItem(ModArmorMaterials.EARTH, ArmorItem.Type.CHESTPLATE, "earth", new Item.Properties().durability(592)));
    public static final DeferredItem<NinjaArmorItem> EARTH_LEGGINGS =
        ITEMS.register("earth_leggings", () -> new NinjaArmorItem(ModArmorMaterials.EARTH, ArmorItem.Type.LEGGINGS, "earth", new Item.Properties().durability(555)));
    public static final DeferredItem<NinjaArmorItem> EARTH_BOOTS =
        ITEMS.register("earth_boots", () -> new NinjaArmorItem(ModArmorMaterials.EARTH, ArmorItem.Type.BOOTS, "earth", new Item.Properties().durability(481)));

    // ── Ice Armor ─────────────────────────────────────────────────────────────
    public static final DeferredItem<NinjaArmorItem> ICE_HELMET =
        ITEMS.register("ice_helmet", () -> new NinjaArmorItem(ModArmorMaterials.ICE, ArmorItem.Type.HELMET, "ice", new Item.Properties().durability(407)));
    public static final DeferredItem<NinjaArmorItem> ICE_CHESTPLATE =
        ITEMS.register("ice_chestplate", () -> new NinjaArmorItem(ModArmorMaterials.ICE, ArmorItem.Type.CHESTPLATE, "ice", new Item.Properties().durability(592)));
    public static final DeferredItem<NinjaArmorItem> ICE_LEGGINGS =
        ITEMS.register("ice_leggings", () -> new NinjaArmorItem(ModArmorMaterials.ICE, ArmorItem.Type.LEGGINGS, "ice", new Item.Properties().durability(555)));
    public static final DeferredItem<NinjaArmorItem> ICE_BOOTS =
        ITEMS.register("ice_boots", () -> new NinjaArmorItem(ModArmorMaterials.ICE, ArmorItem.Type.BOOTS, "ice", new Item.Properties().durability(481)));

    // ── Fire Armor ────────────────────────────────────────────────────────────
    public static final DeferredItem<NinjaArmorItem> FIRE_HELMET =
        ITEMS.register("fire_helmet", () -> new NinjaArmorItem(ModArmorMaterials.FIRE, ArmorItem.Type.HELMET, "fire", new Item.Properties().durability(407)));
    public static final DeferredItem<NinjaArmorItem> FIRE_CHESTPLATE =
        ITEMS.register("fire_chestplate", () -> new NinjaArmorItem(ModArmorMaterials.FIRE, ArmorItem.Type.CHESTPLATE, "fire", new Item.Properties().durability(592)));
    public static final DeferredItem<NinjaArmorItem> FIRE_LEGGINGS =
        ITEMS.register("fire_leggings", () -> new NinjaArmorItem(ModArmorMaterials.FIRE, ArmorItem.Type.LEGGINGS, "fire", new Item.Properties().durability(555)));
    public static final DeferredItem<NinjaArmorItem> FIRE_BOOTS =
        ITEMS.register("fire_boots", () -> new NinjaArmorItem(ModArmorMaterials.FIRE, ArmorItem.Type.BOOTS, "fire", new Item.Properties().durability(481)));

    // ── Lightning Armor ───────────────────────────────────────────────────────
    public static final DeferredItem<NinjaArmorItem> LIGHTNING_HELMET =
        ITEMS.register("lightning_helmet", () -> new NinjaArmorItem(ModArmorMaterials.LIGHTNING, ArmorItem.Type.HELMET, "lightning", new Item.Properties().durability(407)));
    public static final DeferredItem<NinjaArmorItem> LIGHTNING_CHESTPLATE =
        ITEMS.register("lightning_chestplate", () -> new NinjaArmorItem(ModArmorMaterials.LIGHTNING, ArmorItem.Type.CHESTPLATE, "lightning", new Item.Properties().durability(592)));
    public static final DeferredItem<NinjaArmorItem> LIGHTNING_LEGGINGS =
        ITEMS.register("lightning_leggings", () -> new NinjaArmorItem(ModArmorMaterials.LIGHTNING, ArmorItem.Type.LEGGINGS, "lightning", new Item.Properties().durability(555)));
    public static final DeferredItem<NinjaArmorItem> LIGHTNING_BOOTS =
        ITEMS.register("lightning_boots", () -> new NinjaArmorItem(ModArmorMaterials.LIGHTNING, ArmorItem.Type.BOOTS, "lightning", new Item.Properties().durability(481)));

    // ── Nature Armor ──────────────────────────────────────────────────────────
    public static final DeferredItem<NinjaArmorItem> NATURE_HELMET =
        ITEMS.register("nature_helmet", () -> new NinjaArmorItem(ModArmorMaterials.NATURE, ArmorItem.Type.HELMET, "nature", new Item.Properties().durability(407)));
    public static final DeferredItem<NinjaArmorItem> NATURE_CHESTPLATE =
        ITEMS.register("nature_chestplate", () -> new NinjaArmorItem(ModArmorMaterials.NATURE, ArmorItem.Type.CHESTPLATE, "nature", new Item.Properties().durability(592)));
    public static final DeferredItem<NinjaArmorItem> NATURE_LEGGINGS =
        ITEMS.register("nature_leggings", () -> new NinjaArmorItem(ModArmorMaterials.NATURE, ArmorItem.Type.LEGGINGS, "nature", new Item.Properties().durability(555)));
    public static final DeferredItem<NinjaArmorItem> NATURE_BOOTS =
        ITEMS.register("nature_boots", () -> new NinjaArmorItem(ModArmorMaterials.NATURE, ArmorItem.Type.BOOTS, "nature", new Item.Properties().durability(481)));
}
