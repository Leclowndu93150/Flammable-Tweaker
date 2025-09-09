package com.leclowndu93150.flamabletweaker.mixin;

import com.leclowndu93150.flamabletweaker.compat.DomumOrnamentumCompat;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Shadow protected abstract BlockState getStateWithAge(LevelAccessor p_53438_, BlockPos p_53439_, int p_53440_);

    @Inject(
            method = "checkBurnOut",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void checkDomumOrnamentumFlammability(Level level, BlockPos pos, int p_53434_, RandomSource randomSource, int p_53436_, Direction face, CallbackInfo ci) {
        if (!DomumOrnamentumCompat.isModLoaded()) {
            return;
        }

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof IMateriallyTexturedBlock materiallyTexturedBlock) {
            Block materialBlock = DomumOrnamentumCompat.getMaterialBlock(level, pos, materiallyTexturedBlock);

            if (materialBlock != null && DomumOrnamentumCompat.isMaterialFlammable(materialBlock)) {
                int customFlammability = DomumOrnamentumCompat.getFlammabilityValue(materialBlock);

                if (randomSource.nextInt(p_53434_) < customFlammability) {
                    blockState.onCaughtFire(level, pos, face, null);
                    if (randomSource.nextInt(p_53434_ + 10) < 5 && !level.isRainingAt(pos)) {
                        int newAge = Math.min(p_53434_ + randomSource.nextInt(5) / 4, 15);
                        level.setBlock(pos, this.getStateWithAge(level, pos, newAge), 3);
                    } else {
                        level.removeBlock(pos, false);
                    }
                }
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "canCatchFire",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void checkDomumOrnamentumCanCatchFire(BlockGetter world, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (!DomumOrnamentumCompat.isModLoaded()) {
            return;
        }

        if (DomumOrnamentumCompat.isFlammableDOBlock(world, pos)) {
            cir.setReturnValue(true);
        }
    }
}