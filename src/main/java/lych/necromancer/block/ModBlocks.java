package lych.necromancer.block;

import lych.necromancer.Necromancer;
import lych.necromancer.item.ModCommonItems;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Necromancer.MODID);
    private static final Map<BlockEntry<?, ?>, BlockGroup> GROUPS = new HashMap<>();
    public static final BlockEntry<Block, BlockItem> NECROITE_BLOCK = create(ModBlockNames.NECROITE_BLOCK, using(p -> p.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(5, 6).sound(SoundType.METAL)));

    private ModBlocks() {}

    public static Supplier<Block> using(UnaryOperator<Properties> op) {
        return () -> newBlock(op);
    }

    public static Block newBlock(UnaryOperator<Properties> op) {
        return new Block(op.apply(Properties.of()));
    }

    public static <B extends Block> BlockEntry<B, BlockItem> create(String name, Supplier<? extends B> sup) {
        return create(name, sup, blockSup -> ModCommonItems.createDefaultBlockItem(blockSup.get()));
    }

    public static <B extends Block, I extends BlockItem> BlockEntry<B, I> create(String name, Supplier<? extends B> sup, Function<? super RegistryObject<B>, ? extends I> itemMaker) {
        RegistryObject<B> blockSup = register(name, sup);
        RegistryObject<I> itemSup = ModCommonItems.register(name, () -> itemMaker.apply(blockSup));
        return BlockEntry.of(Necromancer.prefix(name), blockSup, itemSup);
    }

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
        return BLOCKS.register(name, sup);
    }

    public static <T extends BlockGroup> T group(BlockEntry<?, ?> block, T group) {
        BlockGroup oldGroup = GROUPS.put(block, group);
        if (oldGroup != null) {
            throw new IllegalStateException("Duplicate family definition for " + block.location());
        }
        return group;
    }

    public static Stream<BlockGroup> getAllGroups() {
        return GROUPS.values().stream();
    }

    public static Stream<BlockFamily> createFamiliesFromGroup() {
        return getAllGroups().filter(BlockGroup::supportsBlockFamily).map(BlockGroup::makeFamily);
    }
}
