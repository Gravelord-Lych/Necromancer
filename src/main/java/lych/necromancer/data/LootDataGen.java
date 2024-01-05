package lych.necromancer.data;

import lych.necromancer.data.loot.ModBlockLootTables;
import lych.necromancer.data.loot.ModEntityLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class LootDataGen extends LootTableProvider {
    public LootDataGen(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY)));
    }
}
