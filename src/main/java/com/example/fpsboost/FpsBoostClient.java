package com.example.fpsboost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.lwjgl.glfw.GLFW;

public class FpsBoostClient implements ClientModInitializer {
    private static KeyBinding lungeKey;
    private int stage = 0;
    private int prevSlot = -1;

    @Override
    public void onInitializeClient() {
        lungeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fpsboost.lunge", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, "category.fpsboost"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen != null) {
                stage = 0;
                return;
            }
            var inv = client.player.getInventory();

            switch (stage) {
                case 0 -> {
                    if (lungeKey.wasPressed()) {
                        int spear = findSpear(client);
                        if (spear == -1) return;
                        prevSlot = inv.getSelectedSlot();
                        inv.setSelectedSlot(spear);
                        stage = 1;
                    }
                }
                case 1 -> {
                    KeyBinding.onKeyPressed(client.options.attackKey.getDefaultKey());
                    stage = 2;
                }
                case 2 -> {
                    inv.setSelectedSlot(prevSlot);
                    stage = 0;
                }
            }
        });
    }

    private int findSpear(MinecraftClient client) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = client.player.getInventory().getStack(i);
            if (s.isIn(ItemTags.SPEARS)) return i;
        }
        return -1;
    }
}
