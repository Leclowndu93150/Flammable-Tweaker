package com.leclowndu93150.flamabletweaker;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FlamableTweaker.MODID)
public class FlamableTweaker {
    public static final String MODID = "flamabletweaker";
    private static final Logger LOGGER = LogManager.getLogger();

    public FlamableTweaker(IEventBus eventBus, ModContainer modContainer) {
    }
}
