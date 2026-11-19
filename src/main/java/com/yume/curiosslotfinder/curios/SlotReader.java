package com.yume.curiosslotfinder.curios;

import java.util.Optional;
import java.util.Set;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public final class SlotReader {
    private static final ResourceLocation TAG_VALIDATOR = new ResourceLocation(CuriosApi.MODID, "tag");
    private static final ResourceLocation ALL_VALIDATOR = new ResourceLocation(CuriosApi.MODID, "all");

    public static Optional<CurioSlot> findEmptySlot(AbstractContainerScreen<?> screen,
                                                    double mouseX,
                                                    double mouseY) {
        for (Slot slot : screen.getMenu().slots) {
            if (!(slot instanceof CurioSlot curioSlot)
                    || !curioSlot.isActive()
                    || curioSlot.hasItem()) {
                continue;
            }

            int left = screen.getGuiLeft() + curioSlot.x;
            int top = screen.getGuiTop() + curioSlot.y;

            if (mouseX >= left && mouseX < left + 16 && mouseY >= top && mouseY < top + 16) {
                return Optional.of(curioSlot);
            }
        }
        return Optional.empty();
    }

    public static Optional<SlotDescriptor> describe(CurioSlot slot, LocalPlayer player) {
        ISlotType slotType = CuriosApi.getSlot(slot.getIdentifier(), player.level()).orElse(null);

        if (slotType == null) {
            return Optional.empty();
        }

        Set<ResourceLocation> validators = Set.copyOf(slotType.getValidators());
        RuleMode mode = classify(validators);
        SlotContext context = new SlotContext(
                slot.getIdentifier(),
                player,
                slot.getSlotIndex(),
                slot.isCosmetic(),
                slot.getRenderStatus()
        );
        Component name = Component.translatable("curios.identifier." + slot.getIdentifier());

        if (slot.isCosmetic()) {
            name = Component.translatable("curios.cosmetic").append(" ").append(name);
        }

        return Optional.of(new SlotDescriptor(
                slot.getIdentifier(),
                slot.getSlotIndex(),
                slot.isCosmetic(),
                name,
                validators,
                mode,
                context
        ));
    }

    private static RuleMode classify(Set<ResourceLocation> validators) {
        if (validators.contains(ALL_VALIDATOR)) {
            return RuleMode.ALL;
        }
        if (validators.size() == 1 && validators.contains(TAG_VALIDATOR)) {
            return RuleMode.TAG;
        }
        return RuleMode.VALIDATORS;
    }

    private SlotReader() {
    }
}
