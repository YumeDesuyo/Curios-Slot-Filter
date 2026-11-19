package com.yume.curiosslotfinder.jei;

import com.yume.curiosslotfinder.CuriosSlotFinder;
import java.util.Optional;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@JeiPlugin
public final class JeiIntegration implements IModPlugin {
    private static volatile IJeiRuntime runtime;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CuriosSlotFinder.MOD_ID, "jei");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        int count = 0;

        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack stack = item.getDefaultInstance();
            boolean hasCuriosTag = stack.getTags()
                    .anyMatch(tag -> tag.location().getNamespace().equals(CuriosApi.MODID));
            boolean hasCurioCapability = CuriosApi.getCurio(stack).isPresent();

            if (hasCuriosTag || hasCurioCapability) {
                registration.addAlias(item, "curio");
                count++;
            }
        }

        CuriosSlotFinder.LOGGER.info(
                "Registered the 'curio' JEI alias for {} Curios items",
                count
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        CuriosSlotFinder.LOGGER.info("Curios Slot Finder connected to JEI");
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
        JeiFilterController.forgetRuntime();
    }

    public static Optional<IJeiRuntime> getRuntime() {
        return Optional.ofNullable(runtime);
    }
}
