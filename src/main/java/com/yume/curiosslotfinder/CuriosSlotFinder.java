package com.yume.curiosslotfinder;

import com.mojang.logging.LogUtils;
import com.yume.curiosslotfinder.config.ModConfig;
import com.yume.curiosslotfinder.event.ClientEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CuriosSlotFinder.MOD_ID)
public final class CuriosSlotFinder {
    public static final String MOD_ID = "curios_slot_finder";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CuriosSlotFinder(FMLJavaModLoadingContext context) {
        LOGGER.info("Curios Slot Filter initialized");
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(ClientEvents::registerKeyMappings);
        modBus.addListener(ClientEvents::registerReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onMousePressed);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onScreenClosing);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onScreenRender);
        context.registerConfig(Type.CLIENT, ModConfig.SPEC);
    }
}
