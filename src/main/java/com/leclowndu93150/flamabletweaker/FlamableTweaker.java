package com.leclowndu93150.flamabletweaker;

import com.leclowndu93150.flamabletweaker.mixin.FireBlockAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(FlamableTweaker.MODID)
public class FlamableTweaker {
    public static final String MODID = "flamabletweaker";
    private static final Logger LOGGER = LogManager.getLogger();

    public FlamableTweaker() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    @SubscribeEvent
    public void setup(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            FlammabilityConfig.loadConfig();
            updateFlammability();
        });
    }

    private void updateFlammability() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        FireBlockAccessor accessor = (FireBlockAccessor) fireBlock;

        List<Block> blocksToRemove = new ArrayList<>();
        accessor.getIgniteOdds().object2IntEntrySet().forEach(entry -> {
            Block block = entry.getKey();
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            if (blockId != null) {
                String blockName = blockId.getPath().toLowerCase();
                if (FlammabilityConfig.excludedKeywords.stream().anyMatch(blockName::contains)) {
                    blocksToRemove.add(block);
                    LOGGER.info("Removing vanilla flammability from: {}", blockId);
                }
            }
        });

        blocksToRemove.forEach(block -> {
            accessor.getIgniteOdds().removeInt(block);
            accessor.getBurnOdds().removeInt(block);
        });

        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            if (blockId == null) continue;

            String blockName = blockId.getPath().toLowerCase();

            if (FlammabilityConfig.excludedKeywords.stream().anyMatch(blockName::contains)) {
                continue;
            }

            if (FlammabilityConfig.flammableKeywords.stream().anyMatch(blockName::contains)) {
                if (!accessor.getIgniteOdds().containsKey(block)) {
                    accessor.invokeSetFlammable(block, 5, 20);
                    LOGGER.info("Adding flammability to: {}", blockId);
                }
            }
        }
    }
}