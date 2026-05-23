package com.ninjago.mod.event;

import com.ninjago.mod.init.ModItems;
import com.ninjago.mod.item.NinjaArmorItem;
import com.ninjago.mod.network.OpenWeaponSelectPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ModEvents {

    private static final String NBT_HAS_CHOSEN = "ninjago_chosen";
    private static final String NBT_WEAPON_IDX = "ninjago_weapon_idx";

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean(NBT_HAS_CHOSEN)) {
            PacketDistributor.sendToPlayer(sp, new OpenWeaponSelectPacket());
        } else {
            restoreArmorIfMissing(sp, data.getInt(NBT_WEAPON_IDX));
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean(NBT_HAS_CHOSEN)) return;
        data.putBoolean("ninjago_needs_restore", true);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.getBoolean("ninjago_needs_restore")) return;
        data.remove("ninjago_needs_restore");
        restoreArmorIfMissing(sp, data.getInt(NBT_WEAPON_IDX));
    }

    /** Tick — apply passive set bonus effects every 2 seconds */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (player.tickCount % 40 != 0) return; // every 2 seconds

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof NinjaArmorItem armor)) return;
        if (!NinjaArmorItem.hasFullSet(player, armor.getElement())) return;

        // Passive set bonus effects
        switch (armor.getElement()) {
            case "earth" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, -1, true, false)); // cancel slow
            }
            case "ice" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
            }
            case "fire" -> {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, true, false));
            }
            case "lightning" -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 1, true, false));
            }
            case "nature" -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 60, 0, true, false));
            }
        }
    }

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

        sp.getInventory().add(weapons.get(weaponIndex));
        giveArmorForIndex(sp, weaponIndex);

        String element = getElementName(weaponIndex);
        sp.sendSystemMessage(Component.literal("§6★ You chose §l" + element + " §r§6element! ★"));
        sp.sendSystemMessage(Component.literal("§7Abilities: §f[R]§7 / §f[T]§7 / §f[Sh+R]§7 / §f[Sh+T]§7 / §f[Y] Spinjitzu"));
        sp.sendSystemMessage(Component.literal("§6Wear the full armor set for §lSET BONUS§r§6!"));
    }

    private static void restoreArmorIfMissing(ServerPlayer sp, int weaponIndex) {
        List<List<ItemStack>> armorSets = getArmorSets();
        if (weaponIndex < 0 || weaponIndex >= armorSets.size()) return;
        List<ItemStack> armor = armorSets.get(weaponIndex);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack current = sp.getItemBySlot(slots[i]);
            if (!(current.getItem() instanceof NinjaArmorItem))
                sp.setItemSlot(slots[i], armor.get(i).copy());
        }
    }

    private static void giveArmorForIndex(ServerPlayer sp, int idx) {
        List<List<ItemStack>> sets = getArmorSets();
        if (idx < 0 || idx >= sets.size()) return;
        List<ItemStack> armor = sets.get(idx);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) sp.setItemSlot(slots[i], armor.get(i).copy());
    }

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
            List.of(new ItemStack(ModItems.EARTH_HELMET.get()), new ItemStack(ModItems.EARTH_CHESTPLATE.get()), new ItemStack(ModItems.EARTH_LEGGINGS.get()), new ItemStack(ModItems.EARTH_BOOTS.get())),
            List.of(new ItemStack(ModItems.ICE_HELMET.get()), new ItemStack(ModItems.ICE_CHESTPLATE.get()), new ItemStack(ModItems.ICE_LEGGINGS.get()), new ItemStack(ModItems.ICE_BOOTS.get())),
            List.of(new ItemStack(ModItems.FIRE_HELMET.get()), new ItemStack(ModItems.FIRE_CHESTPLATE.get()), new ItemStack(ModItems.FIRE_LEGGINGS.get()), new ItemStack(ModItems.FIRE_BOOTS.get())),
            List.of(new ItemStack(ModItems.LIGHTNING_HELMET.get()), new ItemStack(ModItems.LIGHTNING_CHESTPLATE.get()), new ItemStack(ModItems.LIGHTNING_LEGGINGS.get()), new ItemStack(ModItems.LIGHTNING_BOOTS.get())),
            List.of(new ItemStack(ModItems.NATURE_HELMET.get()), new ItemStack(ModItems.NATURE_CHESTPLATE.get()), new ItemStack(ModItems.NATURE_LEGGINGS.get()), new ItemStack(ModItems.NATURE_BOOTS.get()))
        );
    }

    private static String getElementName(int idx) {
        return switch(idx) { case 0->"Earth"; case 1->"Ice"; case 2->"Fire"; case 3->"Lightning"; default->"Nature"; };
    }
}
