package com.leclowndu93150.flamabletweaker.mixin;

import com.leclowndu93150.flamabletweaker.FlammabilityConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(method = "bootStrap", at = @At("RETURN"))
    private static void onBootstrapComplete(CallbackInfo ci) {
        FlammabilityConfig.loadConfig();
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
                    System.out.println("Removing vanilla flammability from: " + blockId);
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
                    System.out.println("Adding flammability to: " + blockId);
                }
            }
        }
    }
}