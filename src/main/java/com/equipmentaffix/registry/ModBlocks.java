package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.block.AffixTableBlock;
import com.equipmentaffix.block.TableKind;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<AffixTableBlock> UPGRADE_TABLE = BLOCKS.register("upgrade_table",
            () -> new AffixTableBlock(TableKind.UPGRADE,
                    BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE)
                            .strength(2.5F)
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<AffixTableBlock> RESET_TABLE = BLOCKS.register("reset_table",
            () -> new AffixTableBlock(TableKind.RESET,
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)
                            .strength(3.5F)
                            .requiresCorrectToolForDrops()));

    private ModBlocks() {
    }
}
