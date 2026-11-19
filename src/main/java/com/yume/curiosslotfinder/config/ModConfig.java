package com.yume.curiosslotfinder.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public final class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final BooleanValue AUTO_FILTER;
    public static final BooleanValue CLEAR_ON_CLOSE;
    public static final BooleanValue SHOW_RESULTS;
    public static final IntValue VALIDATOR_CACHE_TTL;
    public static final IntValue RESULT_COLUMNS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        AUTO_FILTER = builder
                .comment("Automatically filter when an empty Curios slot is clicked.")
                .define("autoFilter", true);
        CLEAR_ON_CLOSE = builder
                .comment("Restore the previous JEI filter when the Curios screen closes.")
                .define("clearOnClose", true);
        SHOW_RESULTS = builder
                .comment("Show the Curios Slot Filter status and validator result panel.")
                .define("showResults", true);
        builder.pop();

        builder.push("performance");
        VALIDATOR_CACHE_TTL = builder
                .comment("Lifetime of player-dependent validator results. Zero disables caching.")
                .defineInRange("validatorCacheTtl", 20, 0, 1200);
        builder.pop();

        builder.push("display");
        RESULT_COLUMNS = builder
                .comment("Number of columns used by the validator result panel.")
                .defineInRange("resultColumns", 6, 3, 10);
        builder.pop();

        SPEC = builder.build();
    }

    private ModConfig() {
    }
}
