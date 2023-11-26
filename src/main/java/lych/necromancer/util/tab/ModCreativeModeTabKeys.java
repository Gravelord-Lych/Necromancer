package lych.necromancer.util.tab;

import lych.necromancer.Necromancer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public final class ModCreativeModeTabKeys {
    public static final ResourceKey<CreativeModeTab> COMMON = createKey(ModCreativeModeTabNames.COMMON);
    public static final ResourceKey<CreativeModeTab> BLOCK_ITEM = createKey(ModCreativeModeTabNames.BLOCK_ITEMS);
    public static final ResourceKey<CreativeModeTab> SPECIAL = createKey(ModCreativeModeTabNames.SPECIAL);

    private ModCreativeModeTabKeys() {}

    private static ResourceKey<CreativeModeTab> createKey(String key) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Necromancer.prefix(key));
    }
}
