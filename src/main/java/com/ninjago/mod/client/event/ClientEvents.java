package com.ninjago.mod.client.event;

import com.ninjago.mod.client.keybind.ModKeybinds;
import com.ninjago.mod.item.NinjaArmorItem;
import com.ninjago.mod.network.AbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        // Abilities come from chestplate
        ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof NinjaArmorItem)) return;

        boolean shift = mc.player.isShiftKeyDown();
        while (ModKeybinds.ABILITY_2.consumeClick())
            PacketDistributor.sendToServer(new AbilityPacket(shift ? 2 : 0));
        while (ModKeybinds.ABILITY_3.consumeClick())
            PacketDistributor.sendToServer(new AbilityPacket(shift ? 3 : 1));
        while (ModKeybinds.SPINJITZU.consumeClick())
            PacketDistributor.sendToServer(new AbilityPacket(4));
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof NinjaArmorItem armor)) return;

        GuiGraphics g = event.getGuiGraphics();
        int x = 10, y = mc.getWindow().getGuiScaledHeight() - 120;
        g.fill(x-2, y-2, x+130, y+56, 0x88000000);
        String[][] labels = {{"[R]","[T]"},{"[Sh+R]","[Sh+T]"},{"[Y] ★SPINJITZU"}};
        String[] names = {armor.getElement().toUpperCase() + " ARMOR ABILITIES",
            "Ab1  Ab2", "Ab3  Ab4", "Ultimate"};
        g.drawString(mc.font, "§l§6" + armor.getElement().toUpperCase() + " NINJA", x, y, 0xFFD700, true);
        String[] keys = {"[R]","[T]","[Sh+R]","[Sh+T]","[Y] Spinjitzu"};
        for (int i=0; i<5; i++) {
            long cd = armor.getRemaining(mc.player, i);
            String cdStr = cd > 0 ? " §c"+String.format("%.1fs",cd/20.0) : " §aREADY";
            g.drawString(mc.font, "§7"+keys[i]+cdStr, x, y+10+i*9, 0xFFFFFF, true);
        }
    }
}
