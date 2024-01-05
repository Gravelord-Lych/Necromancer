package lych.necromancer.item;

import lych.necromancer.entity.ModEntities;
import lych.necromancer.entity.ModEntityNames;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static lych.necromancer.item.ModCommonItems.register;

public final class ModSpawnEggs {
    private static final String SPAWN_EGG_SUFFIX = "_spawn_egg";
    public static final RegistryObject<ForgeSpawnEggItem> NECRO_GOLEM =
            spawnEggItem(ModEntityNames.NECRO_GOLEM, ModEntities.NECRO_GOLEM, 0x1B0533, 0x9120C9);

    private static RegistryObject<ForgeSpawnEggItem> spawnEggItem(String name, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor) {
        return register(name + SPAWN_EGG_SUFFIX, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }

    private ModSpawnEggs() {}

    static void init() {}
}
