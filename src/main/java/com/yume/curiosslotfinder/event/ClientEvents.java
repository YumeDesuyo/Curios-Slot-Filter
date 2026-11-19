package com.yume.curiosslotfinder.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.yume.curiosslotfinder.client.SlotFinderController;
import com.yume.curiosslotfinder.client.SlotFinderOverlay;
import com.yume.curiosslotfinder.config.ModConfig;
import com.yume.curiosslotfinder.curios.SlotReader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.client.ICuriosScreen;

public final class ClientEvents {
    private static final KeyMapping TOGGLE_AUTO_FILTER = new KeyMapping(
            "key.curios_slot_finder.toggle_auto_filter",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            "key.categories.curios_slot_finder"
    );

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_AUTO_FILTER);
    }

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new CacheReloadListener());
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        SlotFinderController.tick();

        while (TOGGLE_AUTO_FILTER.consumeClick()) {
            boolean enabled = !ModConfig.AUTO_FILTER.get();
            ModConfig.AUTO_FILTER.set(enabled);

            if (!enabled) {
                SlotFinderController.clear();
            }

            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setOverlayMessage(
                    Component.translatable(enabled
                            ? "curios_slot_finder.toggle.on"
                            : "curios_slot_finder.toggle.off"),
                    false
            );
        }
    }

    public static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT
                || !(event.getScreen() instanceof ICuriosScreen)
                || !(event.getScreen() instanceof AbstractContainerScreen<?> screen)
                || !SlotFinderController.canRun()
                || Screen.hasShiftDown()
                || Screen.hasControlDown()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || !screen.getMenu().getCarried().isEmpty()) {
            return;
        }

        SlotReader.findEmptySlot(screen, event.getMouseX(), event.getMouseY())
                .flatMap(slot -> SlotReader.describe(slot, player))
                .ifPresent(slot -> {
                    com.yume.curiosslotfinder.CuriosSlotFinder.LOGGER.info(
                            "Detected empty Curios slot {}#{} with rule {}",
                            slot.identifier(),
                            slot.index(),
                            slot.mode()
                    );
                    SlotFinderController.toggle(slot, player);
                    event.setCanceled(true);
                });
    }

    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE
                && event.getScreen() instanceof ICuriosScreen) {
            SlotFinderController.clear();
        }
    }

    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof ICuriosScreen
                && ModConfig.CLEAR_ON_CLOSE.get()) {
            SlotFinderController.clear();
        }
    }

    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof ICuriosScreen
                && ModConfig.SHOW_RESULTS.get()) {
            SlotFinderOverlay.render(event);
        }
    }

    private ClientEvents() {
    }
}
