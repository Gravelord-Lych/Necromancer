package lych.necromancer.item;

import lych.necromancer.Necromancer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ModCommonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Necromancer.MODID);
    public static final RegistryObject<Item> NECRODUST = register(ModItemNames.NECRODUST, ModCommonItems::createDefault);
    public static final RegistryObject<Item> NECROITE_INGOT = register(ModItemNames.NECROITE_INGOT, ModCommonItems::createDefault);
    public static final RegistryObject<Item> NECROITE_NUGGET = register(ModItemNames.NECROITE_NUGGET, ModCommonItems::createDefault);

    static {
        ModSpecialItems.init();
    }

    private ModCommonItems() {}

    public static Item createDefault() {
        return create(UnaryOperator.identity());
    }

    public static Supplier<Item> using(UnaryOperator<Properties> op) {
        return () -> create(op);
    }

    public static Item create(UnaryOperator<Properties> op) {
        return new Item(op.apply(new Properties()));
    }

    public static BlockItem createDefaultBlockItem(Block block) {
        return createBlockItem(block, UnaryOperator.identity());
    }

    public static BlockItem createBlockItem(Block block, UnaryOperator<Properties> op) {
        return new BlockItem(block, op.apply(new Properties()));
    }

    public static <T extends Item> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
        return ITEMS.register(name, sup);
    }
}
