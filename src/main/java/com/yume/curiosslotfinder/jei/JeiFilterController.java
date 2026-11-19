package com.yume.curiosslotfinder.jei;

import com.yume.curiosslotfinder.curios.SlotDescriptor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.ISlotType;

public final class JeiFilterController {
    private static String previousFilter = "";
    private static String appliedFilter;
    private static boolean ownsFilter;

    public static boolean applyTagFilter(String identifier) {
        return JeiIntegration.getRuntime()
                .map(runtime -> {
                    var filter = runtime.getIngredientFilter();
                    if (!ownsFilter) {
                        previousFilter = filter.getFilterText();
                    }

                    appliedFilter = identifier.equals(CuriosApi.MODID)
                            ? "curio"
                            : "#" + identifier;
                    filter.setFilterText(appliedFilter);

                    if (!identifier.equals(CuriosApi.MODID)
                            && filter.getFilteredItemStacks().isEmpty()) {
                        appliedFilter = "#" + CuriosApi.MODID;
                        filter.setFilterText(appliedFilter);
                    }

                    ownsFilter = true;
                    return true;
                })
                .orElse(false);
    }

    public static void releaseFilter() {
        JeiIntegration.getRuntime().ifPresent(runtime -> {
            if (ownsFilter) {
                runtime.getIngredientFilter().setFilterText(previousFilter);
            }
        });
        ownsFilter = false;
        appliedFilter = null;
        previousFilter = "";
    }

    public static boolean isApplied() {
        if (!ownsFilter || appliedFilter == null) {
            return false;
        }
        return JeiIntegration.getRuntime()
                .map(runtime -> appliedFilter.equals(runtime.getIngredientFilter().getFilterText()))
                .orElse(false);
    }

    public static List<ItemStack> findValidCurios(SlotDescriptor descriptor) {
        return JeiIntegration.getRuntime()
                .map(runtime -> {
                    Map<String, ItemStack> matches = new LinkedHashMap<>();

                    for (ItemStack stack : runtime.getIngredientManager().getAllItemStacks()) {
                        if (stack.isEmpty() || !isValidForSlot(descriptor, stack)) {
                            continue;
                        }

                        ItemStack displayStack = stack.copy();
                        displayStack.setCount(1);
                        matches.putIfAbsent(stackKey(displayStack), displayStack);
                    }
                    return List.copyOf(matches.values());
                })
                .orElseGet(List::of);
    }

    public static boolean isValidForSlot(SlotDescriptor descriptor, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        SlotResult result = new SlotResult(descriptor.context(), stack);
        if (CuriosApi.testCurioPredicates(descriptor.validators(), result)) {
            return true;
        }

        if (descriptor.identifier().equals(CuriosApi.MODID)) {
            return false;
        }

        var player = descriptor.context().entity();
        ISlotType generic = CuriosApi.getSlot(CuriosApi.MODID, player.level()).orElse(null);
        if (generic == null) {
            return false;
        }

        SlotContext genericContext = new SlotContext(
                CuriosApi.MODID,
                player,
                0,
                false,
                true
        );
        return CuriosApi.testCurioPredicates(
                generic.getValidators(),
                new SlotResult(genericContext, stack)
        );
    }

    static void forgetRuntime() {
        ownsFilter = false;
        appliedFilter = null;
        previousFilter = "";
    }

    private static String stackKey(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String nbt = stack.getTag() == null ? "" : stack.getTag().toString();
        return itemId + "|" + stack.getDamageValue() + "|" + nbt;
    }

    private JeiFilterController() {
    }
}
