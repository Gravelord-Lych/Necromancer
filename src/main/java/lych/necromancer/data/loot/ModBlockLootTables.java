package lych.necromancer.data.loot;

import lych.necromancer.block.BlockEntry;
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
        dropSelf(ModBlocks.NECROITE_BLOCK);
        dropSelf(ModBlocks.NECROCK_ITEM_CARRIER);
        ModBlockGroups.NECROCK.forAllEntries(this::dropSelf);
        ModBlockGroups.POLISHED_NECROCK.forAllEntries(this::dropSelf);
        ModBlockGroups.NECROCK_BRICKS.forAllEntries(this::dropSelf);
        ModBlockGroups.CRACKED_NECROCK_BRICKS.forAllEntries(this::dropSelf);
    }

    protected void dropSelf(BlockEntry<?, ?> entry) {
        dropSelf(entry.get());
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
