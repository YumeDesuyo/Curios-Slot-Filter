package com.yume.curiosslotfinder.cache;

import com.yume.curiosslotfinder.config.ModConfig;
import com.yume.curiosslotfinder.curios.SlotDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class CompatibilityCache {
    private static final Map<CacheKey, CacheEntry> CACHE = new HashMap<>();
    private static long clientTicks;

    public static void tick() {
        clientTicks++;

        if ((clientTicks & 0x3F) == 0) {
            CACHE.entrySet().removeIf(entry -> entry.getValue().expiresAt < clientTicks);
        }
    }

    public static List<ItemStack> getOrCompute(SlotDescriptor descriptor,
                                               LocalPlayer player,
                                               Supplier<List<ItemStack>> computation) {
        int ttl = ModConfig.VALIDATOR_CACHE_TTL.get();
        if (ttl <= 0) {
            return List.copyOf(computation.get());
        }

        CacheKey key = new CacheKey(
                descriptor.identifier(),
                descriptor.index(),
                descriptor.cosmetic(),
                descriptor.validatorSummary(),
                playerFingerprint(player)
        );
        CacheEntry cached = CACHE.get(key);

        if (cached != null && cached.expiresAt >= clientTicks) {
            return cached.stacks;
        }

        List<ItemStack> computed = List.copyOf(computation.get());
        CACHE.put(key, new CacheEntry(computed, clientTicks + ttl));
        return computed;
    }

    public static void invalidateAll() {
        CACHE.clear();
    }

    private static int playerFingerprint(LocalPlayer player) {
        List<Object> state = new ArrayList<>(48);
        state.add(player.getUUID());
        state.add(player.level().dimension().location());
        state.add(player.getHealth());
        state.add(player.getFoodData().getFoodLevel());
        state.add(player.experienceLevel);
        state.add(player.getMainHandItem());
        state.add(player.getOffhandItem());
        player.getActiveEffects().forEach(effect -> state.add(effect.toString()));

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            state.add(player.getInventory().getItem(i));
        }
        return Objects.hash(state.toArray());
    }

    private record CacheKey(
            String identifier,
            int index,
            boolean cosmetic,
            String validators,
            int playerState
    ) {
    }

    private static final class CacheEntry {
        private final List<ItemStack> stacks;
        private final long expiresAt;

        private CacheEntry(List<ItemStack> stacks, long expiresAt) {
            this.stacks = stacks;
            this.expiresAt = expiresAt;
        }
    }

    private CompatibilityCache() {
    }
}
