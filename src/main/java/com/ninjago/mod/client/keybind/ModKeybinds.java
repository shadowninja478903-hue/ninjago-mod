package com.ninjago.mod.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static final KeyMapping ABILITY_2 = new KeyMapping(
        "key.ninjago.ability2", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.ninjago");

    public static final KeyMapping ABILITY_3 = new KeyMapping(
        "key.ninjago.ability3", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, "key.categories.ninjago");

    public static final KeyMapping SPINJITZU = new KeyMapping(
        "key.ninjago.spinjitzu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, "key.categories.ninjago");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ABILITY_2);
        event.register(ABILITY_3);
        event.register(SPINJITZU);
    }
}
