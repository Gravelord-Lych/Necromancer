package lych.necromancer.util.tab;

import lych.necromancer.Necromancer;
import lych.necromancer.block.BlockEntry;
import lych.necromancer.block.BlockGroup;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.item.NecrowandItem;
import lych.necromancer.util.ReflectionUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeModeTabs {
    private static final String ITEM_GROUP = "itemGroup";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Necromancer.MODID);
    public static final RegistryObject<CreativeModeTab> COMMON = CREATIVE_MODE_TABS.register(ModCreativeModeTabNames.COMMON, () -> CreativeModeTab.builder()
            .title(Component.translatable(prefixItemGroup(ModCreativeModeTabNames.COMMON)))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(ModCreativeModeTabs::makeCommonIcon)
            .displayItems(ModCreativeModeTabs::addCommonItems)
            .build());
    public static final RegistryObject<CreativeModeTab> BLOCK_ITEMS = CREATIVE_MODE_TABS.register(ModCreativeModeTabNames.BLOCK_ITEMS, () -> CreativeModeTab.builder()
            .title(Component.translatable(prefixItemGroup(ModCreativeModeTabNames.BLOCK_ITEMS)))
            .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS,
                    CreativeModeTabs.COLORED_BLOCKS,
                    CreativeModeTabs.NATURAL_BLOCKS,
                    CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    CreativeModeTabs.REDSTONE_BLOCKS,
                    CreativeModeTabs.OP_BLOCKS,
                    ModCreativeModeTabKeys.COMMON)
            .icon(ModCreativeModeTabs::makeBlocksIcon)
            .displayItems(ModCreativeModeTabs::addBlockItems)
            .build());
    public static final RegistryObject<CreativeModeTab> SPECIAL = CREATIVE_MODE_TABS.register(ModCreativeModeTabNames.SPECIAL, () -> CreativeModeTab.builder()
            .title(Component.translatable(prefixItemGroup(ModCreativeModeTabNames.SPECIAL)))
            .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS,
                    CreativeModeTabs.COLORED_BLOCKS,
                    CreativeModeTabs.NATURAL_BLOCKS,
                    CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    CreativeModeTabs.REDSTONE_BLOCKS,
                    CreativeModeTabs.OP_BLOCKS,
                    ModCreativeModeTabKeys.COMMON,
                    ModCreativeModeTabKeys.BLOCK_ITEM)
            .icon(ModCreativeModeTabs::makeSpecialIcon)
            .displayItems(ModCreativeModeTabs::addSpecialItems)
            .build());

    private ModCreativeModeTabs() {}

    private static void addCommonItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        iterateItems(ModCommonItems.class, output);
    }

    private static void addBlockItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        iterateBlockGroups(ModBlockGroups.class, output);
        iterateBlockItems(ModBlocks.class, output);
    }

    private static void addSpecialItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (int i = 1; i <= NecrowandItem.MAX_LEVEL; i++) {
            output.accept(NecrowandItem.leveledNecrowand(i));
        }
    }

    private static void iterateItems(Class<?> src, CreativeModeTab.Output output) {
        ReflectionUtils.iterateFields(src, RegistryObject.class, field -> {
            @SuppressWarnings("unchecked") // All of them are item registry objects
            RegistryObject<Item> itemRegistryObject = (RegistryObject<Item>) field.get(null);
            output.accept(itemRegistryObject.get().getDefaultInstance());
        });
    }

    private static void iterateBlockItems(Class<?> src, CreativeModeTab.Output output) {
        ReflectionUtils.iterateFields(src, BlockEntry.class, field -> {
            BlockEntry<?, ?> entry = (BlockEntry<?, ?>) field.get(null);
            acceptOneEntry(output, entry);
        });
    }

    private static void iterateBlockGroups(Class<?> src, CreativeModeTab.Output output) {
        ReflectionUtils.iterateFields(src, BlockGroup.class, field -> {
            BlockGroup group = (BlockGroup) field.get(null);
            group.forAllEntries(entry -> acceptOneEntry(output, entry));
        });
    }

    private static void acceptOneEntry(CreativeModeTab.Output output, BlockEntry<?, ?> entry) {
        if (entry.hasItemInTab()) {
            output.accept(entry.item());
        }
    }

    public static String prefixItemGroup(String name) {
        return Necromancer.prefixMsg(ITEM_GROUP, name);
    }

    private static ItemStack makeCommonIcon() {
        return ModCommonItems.NECRODUST.get().getDefaultInstance();
    }

    private static ItemStack makeBlocksIcon() {
        return ModBlocks.NECROITE_BLOCK.item().getDefaultInstance();
    }

    private static ItemStack makeSpecialIcon() {
        ItemStack icon = NecrowandItem.leveledNecrowand(NecrowandItem.MAX_LEVEL);
        NecrowandItem.setIcon(icon, true);
        return icon;
    }
}
