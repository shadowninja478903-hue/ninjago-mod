package com.ninjago.mod.client.gui;

import com.ninjago.mod.network.WeaponSelectPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class WeaponSelectScreen extends Screen {

    // ── Data ──────────────────────────────────────────────────────────────────
    private static final String[] NAMES    = {"Earth Scythe", "Ice Shuriken", "Fire Katana", "Nunchucks", "Nature Staff"};
    private static final String[] NINJA    = {"Cole - Earth", "Zane - Ice", "Kai - Fire", "Jay - Lightning", "Lloyd - Nature"};
    private static final String[] ABILITY1 = {"Ground Slam", "Ice Shard", "Flame Dash", "Thunder Strike", "Vine Whip"};
    private static final String[] ABILITY2 = {"Stone Armor", "Frost Nova", "Burning Slash", "Chain Lightning", "Nature Embrace"};
    private static final String[] ABILITY3 = {"Quake", "Blizzard", "Ember Aura", "Speed Boost", "Thorn Aura"};
    private static final String[] ABILITY4 = {"Vine Trap", "Ice Armor", "Phoenix Rise", "Shock Nova", "Pollen Cloud"};
    private static final String[] ULTIMATE = {"Earth Spinjitzu", "Ice Spinjitzu", "Fire Spinjitzu", "Lightning Spinjitzu", "Nature Spinjitzu"};

    // Fix 2: ASCII art icons instead of emoji — renders in any MC font
    private static final String[] ICONS   = {"[E]", "[I]", "[F]", "[L]", "[N]"};

    // Fix 1: Distinct colours for all 5 elements
    private static final int[] BG_DARK = {0xFF0D1A0D, 0xFF001A1A, 0xFF1A0500, 0xFF1A1400, 0xFF0A1A05};
    private static final int[] ACCENT  = {0xFF2E7D32, 0xFF0097A7, 0xFFBF360C, 0xFFF9A825, 0xFF558B2F};
    private static final int[] BRIGHT  = {0xFF69F0AE, 0xFF80DEEA, 0xFFFF6E40, 0xFFFFEA00, 0xFFCCFF90};
    private static final int[] TEXT_C  = {0xFF00E676, 0xFF00E5FF, 0xFFFF3D00, 0xFFFFD600, 0xFF76FF03};

    private int hovered = -1;

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final int CARD_W = 110;
    private static final int CARD_H = 162;
    private static final int GAP    = 8;

    private int startX() { return (width  - (5 * CARD_W + 4 * GAP)) / 2; }
    private int startY() { return (height - CARD_H) / 2 - 12; }

    public WeaponSelectScreen() { super(Component.literal("Choose Your Element")); }

    @Override public boolean isPauseScreen()    { return false; }
    @Override public boolean shouldCloseOnEsc() { return false; }

    // Fix 4: Override renderBackground to prevent Minecraft drawing dirt/blur over us
    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float pt) {
        // Draw our own solid background — skip super to prevent default blur
        g.fill(0, 0, width, height, 0xFF08080E);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g, mx, my, pt);

        // Title bar
        g.fill(0, 0, width, 28, 0xFF0F0F1A);
        g.fill(0, 27, width, 28, 0xFF444488);
        g.drawCenteredString(font, "§l§6* NINJAGO - CHOOSE YOUR ELEMENT *", width/2, 9, 0xFFD700);

        // Subtitle
        g.drawCenteredString(font, "§7Your element is permanent. Choose wisely, ninja.", width/2, 34, 0x888888);

        // Detect hover
        hovered = -1;
        int sx = startX(), sy = startY();
        for (int i = 0; i < 5; i++) {
            int cx = sx + i*(CARD_W+GAP);
            if (mx >= cx && mx <= cx+CARD_W && my >= sy && my <= sy+CARD_H) hovered = i;
        }

        // Draw all 5 cards
        for (int i = 0; i < 5; i++) drawCard(g, sx + i*(CARD_W+GAP), sy, i, hovered == i);

        // Bottom tip
        g.drawCenteredString(font, "§8[RC] Ab.1  [Shift+RC] Ab.2  [R] Ab.3  [T] Ab.4  [Y] Spinjitzu",
            width/2, startY()+CARD_H+10, 0x444466);

        // Render buttons on top (super skips bg)
        super.render(g, mx, my, pt);
    }

    private void drawCard(GuiGraphics g, int x, int y, int idx, boolean hot) {
        int accent = ACCENT[idx];
        int bright = BRIGHT[idx];
        int textC  = TEXT_C[idx];
        int bg     = hot ? blend(BG_DARK[idx], accent, 50) : BG_DARK[idx];

        // Drop shadow
        g.fill(x+3, y+3, x+CARD_W+3, y+CARD_H+3, 0xAA000000);

        // Card body
        g.fill(x, y, x+CARD_W, y+CARD_H, bg);

        // Top coloured strip (3px)
        g.fill(x, y, x+CARD_W, y+3, accent);

        // Border
        drawBorder(g, x, y, CARD_W, CARD_H, hot ? bright : accent);

        // -- Icon (ASCII, guaranteed to render) --
        int iy = y + 6;
        g.drawCenteredString(font, "§l" + ICONS[idx], x+CARD_W/2, iy, bright);

        // -- Weapon name (shortened to avoid overflow) --
        // Fix 3: use scaled-down name that fits 110px
        g.drawCenteredString(font, "§l" + NAMES[idx], x+CARD_W/2, iy+12, textC);

        // -- Ninja name --
        g.drawCenteredString(font, "§o" + NINJA[idx], x+CARD_W/2, iy+22, 0x999999);

        // Divider
        line(g, x+5, x+CARD_W-5, iy+31, accent);

        // -- 4 Abilities (2 columns, 2 rows) --
        int ay = iy + 36;
        // Fix 5: clip text to card width using drawStringNoShadow
        drawAbility(g, ABILITY1[idx], x+4,          ay,      bright);
        drawAbility(g, ABILITY2[idx], x+4,          ay+10,   bright);
        drawAbility(g, ABILITY3[idx], x+4,          ay+20,   bright);
        drawAbility(g, ABILITY4[idx], x+4,          ay+30,   bright);

        // Divider
        line(g, x+5, x+CARD_W-5, ay+41, accent);

        // -- Ultimate --
        g.drawCenteredString(font, "§6* " + ULTIMATE[idx], x+CARD_W/2, ay+45, 0xFFD700);

        // -- Select button --
        int btnY = y+CARD_H-22;
        g.fill(x+5, btnY, x+CARD_W-5, btnY+17, hot ? accent : 0xFF111122);
        drawBorder(g, x+5, btnY, CARD_W-10, 17, hot ? bright : 0xFF2A2A44);
        g.drawCenteredString(font, hot ? "§l> SELECT <" : "§8[ Select ]",
            x+CARD_W/2, btnY+5, hot ? 0xFFFFFF : 0x555566);
    }

    /** Draw a small ability label clipped to card width */
    private void drawAbility(GuiGraphics g, String name, int x, int y, int col) {
        // Fix 5: truncate if too wide for card
        String label = "§8+ §f" + name;
        int maxPx = CARD_W - 10;
        while (font.width(label) > maxPx && label.length() > 5)
            label = label.substring(0, label.length()-1);
        g.drawString(font, label, x, y, col, false);
    }

    /** 1px border */
    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int col) {
        g.fill(x,     y,     x+w, y+1,   col);
        g.fill(x,     y+h-1, x+w, y+h,   col);
        g.fill(x,     y,     x+1, y+h,   col);
        g.fill(x+w-1, y,     x+w, y+h,   col);
    }

    /** 1px horizontal line */
    private void line(GuiGraphics g, int x1, int x2, int y, int col) {
        g.fill(x1, y, x2, y+1, col);
    }

    /** Blend colour a toward b by pct% */
    private int blend(int a, int b, int pct) {
        int ar=(a>>16&0xFF),ag=(a>>8&0xFF),ab=(a&0xFF);
        int br=(b>>16&0xFF),bg=(b>>8&0xFF),bb=(b&0xFF);
        return 0xFF000000|((ar+(br-ar)*pct/100)<<16)|((ag+(bg-ag)*pct/100)<<8)|(ab+(bb-ab)*pct/100);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0 && hovered >= 0) {
            PacketDistributor.sendToServer(new WeaponSelectPacket(hovered));
            minecraft.setScreen(null);
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override public void onClose() { /* Must choose — cannot escape */ }
}
