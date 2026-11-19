package com.yume.curiosslotfinder.client;

import com.yume.curiosslotfinder.cache.CompatibilityCache;
import com.yume.curiosslotfinder.config.ModConfig;
import com.yume.curiosslotfinder.curios.RuleMode;
import com.yume.curiosslotfinder.curios.SlotDescriptor;
import com.yume.curiosslotfinder.jei.JeiFilterController;
import com.yume.curiosslotfinder.CuriosSlotFinder;
import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class SlotFinderController {
    private static SlotDescriptor activeSlot;
    private static List<ItemStack> validatorResults = List.of();
    private static boolean jeiAvailable = true;

    public static boolean toggle(SlotDescriptor slot, LocalPlayer player) {
        if (activeSlot != null && activeSlot.sameLocation(slot)) {
            clear();
            return true;
        }

        resetState();
        activeSlot = slot;
        jeiAvailable = true;
        validatorResults = List.of();

        switch (slot.mode()) {
            case TAG -> jeiAvailable = JeiFilterController.applyTagFilter(slot.identifier());
            case VALIDATORS -> validatorResults = CompatibilityCache.getOrCompute(
                    slot,
                    player,
                    () -> JeiFilterController.findValidCurios(slot)
            );
            case ALL -> JeiFilterController.releaseFilter();
        }
        CuriosSlotFinder.LOGGER.info(
                "Slot filter active: identifier={}, rule={}, matches={}, jei={}",
                slot.identifier(),
                slot.mode(),
                validatorResults.size(),
                jeiAvailable
        );
        return true;
    }

    public static void clear() {
        resetState();
        activeSlot = null;
        validatorResults = List.of();
        jeiAvailable = true;
    }

    public static void tick() {
        CompatibilityCache.tick();

        if (activeSlot != null
                && activeSlot.mode() == RuleMode.TAG
                && !JeiFilterController.isApplied()) {
            activeSlot = null;
            validatorResults = List.of();
            jeiAvailable = true;
        }
    }

    public static SlotDescriptor activeSlot() {
        return activeSlot;
    }

    public static List<ItemStack> validatorResults() {
        return validatorResults;
    }

    public static boolean jeiAvailable() {
        return jeiAvailable;
    }

    public static boolean canRun() {
        return ModConfig.AUTO_FILTER.get();
    }

    private static void resetState() {
        JeiFilterController.releaseFilter();
    }

    private SlotFinderController() {
    }
}
