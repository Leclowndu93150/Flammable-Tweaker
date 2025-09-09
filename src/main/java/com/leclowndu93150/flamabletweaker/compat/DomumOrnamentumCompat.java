package com.leclowndu93150.flamabletweaker.compat;

import com.leclowndu93150.flamabletweaker.FlammabilityConfig;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlock;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlockComponent;
import com.ldtteam.domumornamentum.entity.block.IMateriallyTexturedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

public class DomumOrnamentumCompat {

    private static final String DOMUM_ORNAMENTUM_MOD_ID = "domum_ornamentum";
    private static Boolean modLoaded = null;

    public static boolean isModLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded(DOMUM_ORNAMENTUM_MOD_ID);
        }
        return modLoaded;
    }

    /**
     * Check if a block at the given position is a flammable Domum Ornamentum block
     * @param world The world/level
     * @param pos The position to check
     * @return true if the block is a DO block with flammable material, false otherwise
     */
    public static boolean isFlammableDOBlock(BlockGetter world, BlockPos pos) {
        if (!isModLoaded()) {
            return false;
        }

        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (!(block instanceof IMateriallyTexturedBlock materiallyTexturedBlock)) {
            return false;
        }

        Block materialBlock = getMaterialBlock(world, pos, materiallyTexturedBlock);
        if (materialBlock == null) {
            return false;
        }

        return isMaterialFlammable(materialBlock);
    }

    /**
     * Get the material block from a Domum Ornamentum block
     * @param world The world/level
     * @param pos The position of the block
     * @param materiallyTexturedBlock The DO block
     * @return The material block or null if not found
     */
    public static Block getMaterialBlock(BlockGetter world, BlockPos pos, IMateriallyTexturedBlock materiallyTexturedBlock) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof IMateriallyTexturedBlockEntity texturedEntity)) {
            return null;
        }

        IMateriallyTexturedBlockComponent mainComponent = materiallyTexturedBlock.getMainComponent();
        if (mainComponent == null) {
            return null;
        }

        return texturedEntity.getTextureData()
                .getTexturedComponents()
                .get(mainComponent.getId());
    }

    /**
     * Check if a material block should be considered flammable
     * @param materialBlock The material block to check
     * @return true if the material is flammable according to configuration
     */
    public static boolean isMaterialFlammable(Block materialBlock) {
        ResourceLocation materialId = BuiltInRegistries.BLOCK.getKey(materialBlock);
        if (materialId == null) {
            return false;
        }

        String materialName = materialId.getPath().toLowerCase();

        return FlammabilityConfig.flammableKeywords.stream()
                .anyMatch(materialName::contains) &&
                FlammabilityConfig.excludedKeywords.stream()
                        .noneMatch(materialName::contains);
    }

    /**
     * Get flammability value for a material type
     * @param materialBlock The material block
     * @return Flammability value (5 for logs/wood, 20 for other flammable materials)
     */
    public static int getFlammabilityValue(Block materialBlock) {
        ResourceLocation materialId = BuiltInRegistries.BLOCK.getKey(materialBlock);
        if (materialId == null) {
            return 20;
        }

        String materialName = materialId.getPath().toLowerCase();
        return (materialName.contains("log") || materialName.contains("wood")) ? 5 : 20;
    }
}