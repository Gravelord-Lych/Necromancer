package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.item.ModSpecialItems;
import lych.necromancer.util.KeepInventoryHelper;
import lych.necromancer.util.tab.ModCreativeModeTabNames;
import lych.necromancer.util.tab.ModCreativeModeTabs;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class LangDataGen extends LanguageProvider {
    public LangDataGen(PackOutput output, String locale) {
        super(output, Necromancer.MODID, locale);
    }

    public static final class EnUs extends LangDataGen {
        public EnUs(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            addItems();
            addBlocks();
            addItemGroups();
            addMisc();
        }

        private void addItems() {
            addItem(ModCommonItems.NECRODUST, "Necrodust");
            addItem(ModCommonItems.NECROITE_INGOT, "Necroite Ingot");
            addItem(ModCommonItems.NECROITE_NUGGET, "Necroite Nugget");
            addItem(ModSpecialItems.NECROWAND, "Necrowand");
        }

        private void addBlocks() {
            addBlock(ModBlockGroups.CRACKED_NECROCK_BRICKS.base(), "Cracked Necrock Bricks");
            addBlock(ModBlockGroups.CRACKED_NECROCK_BRICKS.slab(), "Cracked Necrock Brick Slab");
            addBlock(ModBlockGroups.CRACKED_NECROCK_BRICKS.stairs(), "Cracked Necrock Brick Stairs");
            addBlock(ModBlockGroups.CRACKED_NECROCK_BRICKS.wall(), "Cracked Necrock Brick Wall");
            addBlock(ModBlocks.NECROITE_BLOCK, "Necroite Block");
            addBlock(ModBlocks.NECROCK_ITEM_CARRIER, "Necroite Item Carrier");
            addBlock(ModBlockGroups.NECROCK.base(), "Necrock");
            addBlock(ModBlockGroups.NECROCK.slab(), "Necrock Slab");
            addBlock(ModBlockGroups.NECROCK.stairs(), "Necrock Stairs");
            addBlock(ModBlockGroups.NECROCK.wall(), "Necrock Wall");
            addBlock(ModBlockGroups.POLISHED_NECROCK.base(), "Polished Necrock");
            addBlock(ModBlockGroups.POLISHED_NECROCK.slab(), "Polished Necrock Slab");
            addBlock(ModBlockGroups.POLISHED_NECROCK.stairs(), "Polished Necrock Stairs");
            addBlock(ModBlockGroups.POLISHED_NECROCK.wall(), "Polished Necrock Wall");
            addBlock(ModBlockGroups.NECROCK_BRICKS.base(), "Necrock Bricks");
            addBlock(ModBlockGroups.NECROCK_BRICKS.slab(), "Necrock Brick Slab");
            addBlock(ModBlockGroups.NECROCK_BRICKS.stairs(), "Necrock Brick Stairs");
            addBlock(ModBlockGroups.NECROCK_BRICKS.wall(), "Necrock Brick Wall");
        }

        private void addItemGroups() {
            add(ModCreativeModeTabs.prefixItemGroup(ModCreativeModeTabNames.COMMON), "Necromancer Items");
            add(ModCreativeModeTabs.prefixItemGroup(ModCreativeModeTabNames.BLOCK_ITEMS), "Necromancer Blocks");
            add(ModCreativeModeTabs.prefixItemGroup(ModCreativeModeTabNames.SPECIAL), "Necromancer Special Items");
        }

        private void addMisc() {
            add(KeepInventoryHelper.MESSAGE_ID, "Keep-Inventory times remaining: %s");
            add(KeepInventoryHelper.PlayerState.READY.getMessageId(), "%d");
            add(KeepInventoryHelper.PlayerState.INFINITY.getMessageId(), "Infinity");
            add(KeepInventoryHelper.PlayerState.DEPLETED.getMessageId(), "Keep-Inventory times have been used up");
            add(KeepInventoryHelper.PlayerState.RULE_ENABLED.getMessageId(), "Gamerule keepInventory is true");
        }
    }
}
