package com.ninjago.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

/** Base elemental weapon — melee only. Abilities come from the matching armor set. */
public abstract class ElementalWeaponItem extends SwordItem {

    private final String element;
    private final ChatFormatting color;

    protected ElementalWeaponItem(String element, ChatFormatting color, int dmg, float spd) {
        super(Tiers.NETHERITE, new Item.Properties()
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, dmg, spd))
            .stacksTo(1));
        this.element = element;
        this.color   = color;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack,ctx,tip,flag);
        tip.add(Component.literal(" "));
        tip.add(Component.literal("§l" + element.toUpperCase() + " WEAPON").withStyle(color));
        tip.add(Component.literal("§7Abilities are granted by your §l" + element + " armor§7."));
        tip.add(Component.literal("§7Equip the full set for maximum power!"));
    }

    public String getElement() { return element; }
}
