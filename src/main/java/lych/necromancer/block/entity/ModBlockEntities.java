package lych.necromancer.block.entity;

import lych.necromancer.Necromancer;
import lych.necromancer.block.BlockEntry;
import lych.necromancer.block.ModBlockNames;
import lych.necromancer.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Necromancer.MODID);
    public static final RegistryObject<BlockEntityType<NecrockItemCarrierBlockEntity>> NECROCK_ITEM_CARRIER =
            BLOCK_ENTITIES.register(ModBlockNames.NECROCK_ITEM_CARRIER, make(NecrockItemCarrierBlockEntity::new, ModBlocks.NECROCK_ITEM_CARRIER));

    @NotNull
    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> make(BlockEntityType.BlockEntitySupplier<? extends T> sup, BlockEntry<?, ?>... blocks) {
        return () -> BlockEntityType.Builder.<T>of(sup, Arrays.stream(blocks).map(BlockEntry::get).toArray(Block[]::new)).build(null);
    }
}
