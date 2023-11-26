package lych.necromancer.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static lych.necromancer.item.ModCommonItems.register;

public final class ModSpecialItems {
    public static final RegistryObject<NecrowandItem> NECROWAND = register(ModItemNames.NECROWAND, () -> new NecrowandItem(new Item.Properties()));

    private ModSpecialItems() {}

    static void init() {}
}
