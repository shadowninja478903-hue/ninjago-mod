package com.ninjago.mod.init;

import com.ninjago.mod.NinjagoMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, NinjagoMod.MOD_ID);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> make(String element) {
        return ARMOR_MATERIALS.register(element, () -> new ArmorMaterial(
            new EnumMap<>(Map.of(
                ArmorItem.Type.BOOTS,      5,
                ArmorItem.Type.LEGGINGS,   10,
                ArmorItem.Type.CHESTPLATE, 13,
                ArmorItem.Type.HELMET,     5
            )),
            40,  // enchantability
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(
                ResourceLocation.fromNamespaceAndPath(NinjagoMod.MOD_ID, element)
            )),
            4.0f,  // toughness (netherite = 3.0, we go higher)
            0.3f   // knockback resistance
        ));
    }

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> EARTH     = make("earth");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ICE       = make("ice");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FIRE      = make("fire");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LIGHTNING = make("lightning");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NATURE    = make("nature");
}
