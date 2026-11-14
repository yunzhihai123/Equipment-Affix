package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.block.AffixTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<AffixTableBlockEntity>> AFFIX_TABLE =
            BLOCK_ENTITIES.register("affix_table", () -> BlockEntityType.Builder
                    .of(AffixTableBlockEntity::new, ModBlocks.UPGRADE_TABLE.get(), ModBlocks.RESET_TABLE.get())
                    .build(null));

    private ModBlockEntities() {
    }
}
