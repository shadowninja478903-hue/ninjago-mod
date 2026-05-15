package com.ninjago.mod.event;

import com.ninjago.mod.init.ModItems;
import com.ninjago.mod.item.NinjaArmorItem;
import com.ninjago.mod.network.OpenWeaponSelectPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

public class ModEvents {

    private static final String NBT_HAS_CHOSEN = "ninjago_chosen";
    private static final String NBT_WEAPON_IDX = "ninjago_weapon_idx";

    // ── First join — open selection GUI ───────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean(NBT_HAS_CHOSEN)) {
            // Tell client to open the weapon selection GUI
            PacketDistributor.sendToPlayer(sp, new OpenWeaponSelectPacket());
        } else {
            // Re-equip armor silently on rejoin if missing
            restoreArmorIfMissing(sp, data.getInt(NBT_WEAPON_IDX));
        }
    }

    // ── Death — save + restore armor ─────────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean(NBT_HAS_CHOSEN)) return;
        // Mark so respawn handler can re-equip
        data.putBoolean("ninjago_needs_restore", true);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean("ninjago_needs_restore")) return;
        data.remove("ninjago_needs_restore");
        int idx = data.getInt(NBT_WEAPON_IDX);
        // Small delay via scheduled server task is not directly available here;
        // restore immediately — armor is given fresh (infinite durability mechanic)
        restoreArmorIfMissing(sp, idx);
    }

    // ── Weapon + armor give ───────────────────────────────────────────────────

    /** Called by WeaponSelectPacket handler on server. weaponIndex 0-4. */
    public static void giveWeaponAndArmor(ServerPlayer sp, int weaponIndex) {
        CompoundTag data = sp.getPersistentData();
        if (data.getBoolean(NBT_HAS_CHOSEN)) {
            sp.sendSystemMessage(Component.literal("§cYou have already chosen your element!"));
            return;
        }
        data.putBoolean(NBT_HAS_CHOSEN, true);
        data.putInt(NBT_WEAPON_IDX, weaponIndex);

        List<ItemStack> weapons = getWeaponList();
        if (weaponIndex < 0 || weaponIndex >= weapons.size()) return;

        ItemStack weapon = weapons.get(weaponIndex);
        sp.getInventory().add(weapon);
        giveArmorForIndex(sp, weaponIndex);

        String element = getElementName(weaponIndex);
        sp.sendSystemMessage(Component.literal("§6★ You have chosen the §l" + element + " §r§6element! ★"));
        sp.sendSystemMessage(Component.literal("§7Use §fR§7, §fT§7, §fY §7keys for abilities 3/4/Spinjitzu!"));
    }

    private static void restoreArmorIfMissing(ServerPlayer sp, int weaponIndex) {
        List<List<ItemStack>> armorSets = getArmorSets();
        if (weaponIndex < 0 || weaponIndex >= armorSets.size()) return;
        List<ItemStack> armor = armorSets.get(weaponIndex);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack current = sp.getItemBySlot(slots[i]);
            if (!(current.getItem() instanceof NinjaArmorItem)) {
                sp.setItemSlot(slots[i], armor.get(i).copy());
            }
        }
    }

    private static void giveArmorForIndex(ServerPlayer sp, int weaponIndex) {
        List<List<ItemStack>> armorSets = getArmorSets();
        if (weaponIndex < 0 || weaponIndex >= armorSets.size()) return;
        List<ItemStack> armor = armorSets.get(weaponIndex);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            sp.setItemSlot(slots[i], armor.get(i).copy());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static List<ItemStack> getWeaponList() {
        return List.of(
            new ItemStack(ModItems.SCYTHE.get()),
            new ItemStack(ModItems.SHURIKEN.get()),
            new ItemStack(ModItems.KATANA.get()),
            new ItemStack(ModItems.NUNCHUCKS.get()),
            new ItemStack(ModItems.NATURE_STAFF.get())
        );
    }

    private static List<List<ItemStack>> getArmorSets() {
        return List.of(
            // Earth
            List.of(new ItemStack(ModItems.EARTH_HELMET.get()),
                    new ItemStack(ModItems.EARTH_CHESTPLATE.get()),
                    new ItemStack(ModItems.EARTH_LEGGINGS.get()),
                    new ItemStack(ModItems.EARTH_BOOTS.get())),
            // Ice
            List.of(new ItemStack(ModItems.ICE_HELMET.get()),
                    new ItemStack(ModItems.ICE_CHESTPLATE.get()),
                    new ItemStack(ModItems.ICE_LEGGINGS.get()),
                    new ItemStack(ModItems.ICE_BOOTS.get())),
            // Fire
            List.of(new ItemStack(ModItems.FIRE_HELMET.get()),
                    new ItemStack(ModItems.FIRE_CHESTPLATE.get()),
                    new ItemStack(ModItems.FIRE_LEGGINGS.get()),
                    new ItemStack(ModItems.FIRE_BOOTS.get())),
            // Lightning
            List.of(new ItemStack(ModItems.LIGHTNING_HELMET.get()),
                    new ItemStack(ModItems.LIGHTNING_CHESTPLATE.get()),
                    new ItemStack(ModItems.LIGHTNING_LEGGINGS.get()),
                    new ItemStack(ModItems.LIGHTNING_BOOTS.get())),
            // Nature
            List.of(new ItemStack(ModItems.NATURE_HELMET.get()),
                    new ItemStack(ModItems.NATURE_CHESTPLATE.get()),
                    new ItemStack(ModItems.NATURE_LEGGINGS.get()),
                    new ItemStack(ModItems.NATURE_BOOTS.get()))
        );
    }

    private static String getElementName(int idx) {
        return switch (idx) {
            case 0 -> "Earth";
            case 1 -> "Ice";
            case 2 -> "Fire";
            case 3 -> "Lightning";
            case 4 -> "Nature";
            default -> "Unknown";
        };
    }
}
