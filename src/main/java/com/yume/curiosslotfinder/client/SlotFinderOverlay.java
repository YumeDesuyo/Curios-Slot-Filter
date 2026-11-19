package com.yume.curiosslotfinder.client;

import com.yume.curiosslotfinder.config.ModConfig;
import com.yume.curiosslotfinder.curios.RuleMode;
import com.yume.curiosslotfinder.curios.SlotDescriptor;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;

public final class SlotFinderOverlay {
    private static final int BACKGROUND = 0xE0101018;
    private static final int BORDER = 0xFF8D7BFF;
    private static final int TEXT = 0xFFF4F1FF;
    private static final int MUTED = 0xFFBEB8D8;
    private static final int PADDING = 7;
    private static final int CELL = 18;

    public static void render(ScreenEvent.Render.Post event) {
        SlotDescriptor slot = SlotFinderController.activeSlot();
        if (slot == null) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        Screen screen = event.getScreen();
        Font font = Minecraft.getInstance().font;
        List<ItemStack> results = SlotFinderController.validatorResults();
        int columns = slot.mode() == RuleMode.VALIDATORS ? ModConfig.RESULT_COLUMNS.get() : 1;
        int maxRows = Math.max(1, Math.min(7, (screen.height - 48) / CELL));
        int visibleResults = slot.mode() == RuleMode.VALIDATORS
                ? Math.min(results.size(), columns * maxRows)
                : 0;
        int rows = (visibleResults + columns - 1) / columns;
        int headerWidth = Math.max(
                font.width(Component.translatable("curios_slot_finder.results.title")),
                Math.max(font.width(modeLine(slot)), font.width(slot.displayName()))
        );
        int gridWidth = columns * CELL;
        int panelWidth = Math.max(174, Math.max(headerWidth, gridWidth) + PADDING * 2);
        int panelHeight = PADDING * 2 + font.lineHeight * 3 + 4
                + (rows > 0 ? rows * CELL + 4 : 0);
        int x = 8;
        int y = Math.max(8, (screen.height - panelHeight) / 2);

        graphics.fill(x, y, x + panelWidth, y + panelHeight, BACKGROUND);
        graphics.renderOutline(x, y, panelWidth, panelHeight, BORDER);
        graphics.drawString(font, Component.translatable("curios_slot_finder.results.title"),
                x + PADDING, y + PADDING, TEXT, false);
        int lineY = y + PADDING + font.lineHeight + 2;
        graphics.drawString(font, slot.displayName(), x + PADDING, lineY, MUTED, false);
        lineY += font.lineHeight;
        graphics.drawString(font, modeLine(slot), x + PADDING, lineY, MUTED, false);

        if (visibleResults > 0) {
            int gridX = x + PADDING;
            int gridY = lineY + font.lineHeight + 4;

            for (int i = 0; i < visibleResults; i++) {
                int column = i % columns;
                int row = i / columns;
                int itemX = gridX + column * CELL;
                int itemY = gridY + row * CELL;
                ItemStack stack = results.get(i);

                graphics.renderItem(stack, itemX, itemY);
                graphics.renderItemDecorations(font, stack, itemX, itemY);

                if (event.getMouseX() >= itemX && event.getMouseX() < itemX + 16
                        && event.getMouseY() >= itemY && event.getMouseY() < itemY + 16) {
                    graphics.renderTooltip(font, stack, event.getMouseX(), event.getMouseY());
                }
            }
        }

        if (slot.mode() == RuleMode.VALIDATORS && results.size() > visibleResults) {
            Component more = Component.translatable(
                    "curios_slot_finder.results.more",
                    results.size() - visibleResults
            ).withStyle(ChatFormatting.DARK_GRAY);
            graphics.drawString(font, more, x + PADDING,
                    y + panelHeight - PADDING - font.lineHeight, MUTED, false);
        }
    }

    private static Component modeLine(SlotDescriptor slot) {
        if (!SlotFinderController.jeiAvailable()) {
            return Component.translatable("curios_slot_finder.status.no_jei")
                    .withStyle(ChatFormatting.RED);
        }

        return switch (slot.mode()) {
            case TAG -> Component.translatable(
                    "curios_slot_finder.status.tag",
                    "#" + slot.identifier()
            );
            case VALIDATORS -> Component.translatable(
                    "curios_slot_finder.status.validators",
                    SlotFinderController.validatorResults().size()
            );
            case ALL -> Component.translatable("curios_slot_finder.status.all");
        };
    }

    private SlotFinderOverlay() {
    }
}
