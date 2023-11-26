package lych.necromancer.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public final class BlockGroups {
    private BlockGroups() {}

    public static StoneBlockGroup newStoneBlockGroup(String name, Supplier<? extends Block> sup) {
        BlockEntry<? extends Block, BlockItem> base = ModBlocks.create(name, sup);
        return ModBlocks.group(base, StoneBlockGroup.of(base));
    }

    public static StoneBlockGroup newStoneBlockGroupWithPolishedRef(String name, Supplier<? extends Block> sup, Supplier<? extends StoneBlockGroup> polished) {
        BlockEntry<? extends Block, BlockItem> base = ModBlocks.create(name, sup);
        return ModBlocks.group(base, StoneBlockGroup.of(base, Lazy.of(polished)));
    }
}
