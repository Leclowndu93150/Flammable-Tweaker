package com.leclowndu93150.flamabletweaker.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Accessor("igniteOdds")
    Object2IntMap<Block> getIgniteOdds();

    @Accessor("burnOdds")
    Object2IntMap<Block> getBurnOdds();

    @Invoker("setFlammable")
    void invokeSetFlammable(Block block, int igniteOdds, int burnOdds);
}