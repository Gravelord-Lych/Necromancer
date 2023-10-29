package lych.necromancer.data.loot;

import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    private static final Set<Item> EXPLOSION_RESISTANT = Set.of();
    private final Set<Block> knownBlocks = new HashSet<>();

    public ModBlockLootTables() {
        super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.NECROITE_BLOCK.get());
        dropSelf(ModBlockGroups.NECROCK.base().get());
        dropSelf(ModBlockGroups.NECROCK.slab().get());
        dropSelf(ModBlockGroups.NECROCK.stairs().get());
        dropSelf(ModBlockGroups.NECROCK.wall().get());
    }

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }

    @Override
    public Set<Block> getKnownBlocks() {
        return knownBlocks;
    }
}
