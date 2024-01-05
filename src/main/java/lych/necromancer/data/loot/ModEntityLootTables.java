package lych.necromancer.data.loot;

import lych.necromancer.entity.ModEntities;
import lych.necromancer.item.ModCommonItems;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;

public class ModEntityLootTables extends EntityLootSubProvider {
    private final Set<EntityType<?>> knownEntities = new HashSet<>();

    public ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        add(ModEntities.NECRO_GOLEM.get(), singlePool(lootPool().setRolls(exactly(1))
                .add(lootTableItem(ModCommonItems.NECRODUST.get())
                        .apply(setCount(exactly(1))))
                .add(lootTableItem(ModCommonItems.NECROITE_NUGGET.get())
                        .apply(setCount(between(0, 2))))));
    }

    @NotNull
    private static LootTable.Builder singlePool(LootPool.Builder poolBuilder) {
        return LootTable.lootTable().withPool(poolBuilder);
    }

    @Override
    protected void add(EntityType<?> type, ResourceLocation location, LootTable.Builder builder) {
        super.add(type, location, builder);
        knownEntities.add(type);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return knownEntities.stream();
    }
}
