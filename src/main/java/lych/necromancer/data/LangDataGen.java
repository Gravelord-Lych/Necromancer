package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.util.KeepInventoryHelper;
import lych.necromancer.util.tab.ModCreativeModeTabNames;
import lych.necromancer.util.tab.ModCreativeModeTabs;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class LangDataGen extends LanguageProvider {
    public LangDataGen(PackOutput output, String locale) {
        super(output, Necromancer.MODID, locale);
    }

    public static class EnUs extends LangDataGen {
        public EnUs(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(KeepInventoryHelper.MESSAGE_ID, "Keep-Inventory times remaining: %s");
            add(KeepInventoryHelper.PlayerState.READY.getMessageId(), "%d");
            add(KeepInventoryHelper.PlayerState.INFINITY.getMessageId(), "Infinity");
            add(KeepInventoryHelper.PlayerState.DEPLETED.getMessageId(), "Keep-Inventory times have been used up");
            add(KeepInventoryHelper.PlayerState.RULE_ENABLED.getMessageId(), "Gamerule keepInventory is true");
            addItem(ModCommonItems.NECRODUST, "Necrodust");
            addItem(ModCommonItems.NECROITE_INGOT, "Necroite Ingot");
            addBlock(ModBlocks.NECROITE_BLOCK, "Necroite Block");
            addBlock(ModBlockGroups.NECROCK.base(), "Necrock");
            addBlock(ModBlockGroups.NECROCK.slab(), "Necrock Slab");
            addBlock(ModBlockGroups.NECROCK.stairs(), "Necrock Stairs");
            addBlock(ModBlockGroups.NECROCK.wall(), "Necrock Wall");
            add(ModCreativeModeTabs.prefixItemGroup(ModCreativeModeTabNames.COMMON), "Necromancer Items");
            add(ModCreativeModeTabs.prefixItemGroup(ModCreativeModeTabNames.BLOCK_ITEMS), "Necromancer Blocks");
        }
    }
}
