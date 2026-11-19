package com.yume.curiosslotfinder.curios;

import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

public record SlotDescriptor(
        String identifier,
        int index,
        boolean cosmetic,
        Component displayName,
        Set<ResourceLocation> validators,
        RuleMode mode,
        SlotContext context
) {
    public boolean sameLocation(SlotDescriptor other) {
        return this.identifier.equals(other.identifier)
                && this.index == other.index
                && this.cosmetic == other.cosmetic;
    }

    public String validatorSummary() {
        return this.validators.stream()
                .map(ResourceLocation::toString)
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }
}
