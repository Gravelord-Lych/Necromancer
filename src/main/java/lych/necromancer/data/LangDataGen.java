package lych.necromancer.data;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.command.FirstStrikeCommand;
import lych.necromancer.entity.ModEntities;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.item.ModSpawnEggs;
import lych.necromancer.item.ModSpecialItems;
import lych.necromancer.sound.ModSoundEvents;
import lych.necromancer.util.DarkPowerHelper;
import lych.necromancer.util.KeepInventoryHelper;
import lych.necromancer.util.tab.ModCreativeModeTabNames;
import lych.necromancer.util.tab.ModCreativeModeTabs;
import lych.necromancer.world.event.FirstStrike;
import lych.necromancer.world.event.WorldEvent;
import lych.necromancer.world.level.ModGameRules;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class LangDataGen extends LanguageProvider {
    public LangDataGen(PackOutput output, String locale) {
        super(output, Necromancer.MODID, locale);
    }

    protected abstract String getSpawnEggSuffix();

    public void addEntityAndSpawnEgg(Supplier<? extends EntityType<?>> type, Supplier<? extends Item> spawnEgg, String name) {
        addEntityType(type, name);
        addItem(spawnEgg, name + getSpawnEggSuffix());
    }

    public static final class EnUs extends LangDataGen {
        public EnUs(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            addItems();
            addBlocks();
            addCommands();
            addEntities();
            addGameRules();
            addItemGroups();
            addMisc();
            addSubtitles();
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
            addBlock(ModBlocks.NECROCK_ITEM_BASE, "Necroite Item Base");
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

        private void addCommands() {
            addCommand(FirstStrikeCommand.COMMAND_NAME, FirstStrikeCommand.SUCCESSFULLY_REFRESH, "Successfully reset First Strike for %s");
            addCommand(FirstStrikeCommand.COMMAND_NAME, FirstStrikeCommand.SUCCESSFULLY_STRUCK, "Successfully started First Strike for %s");
            addCommand(FirstStrikeCommand.COMMAND_NAME, FirstStrikeCommand.ALREADY_STRUCK_ID, "%s has already been struck");
            addCommand(FirstStrikeCommand.COMMAND_NAME, FirstStrikeCommand.NOT_STRUCK_ID, "%s has not been struck");
            addCommand(FirstStrikeCommand.NO_DATA_STORAGE, "First Strikes are disabled in this world");
            addCommand(FirstStrikeCommand.UNKNOWN_ERROR, "Unable to create First Strike");
        }

        private void addEntities() {
            addEntityAndSpawnEgg(ModEntities.NECRO_GOLEM, ModSpawnEggs.NECRO_GOLEM, "Necro-Golem");
        }

        private void addGameRules() {
            addGameRule(ModGameRules.NCMRULE_DISABLE_FIRST_STRIKES, "Disable First Strikes",
                    "Disables First Strikes in this world. Players will receive bonus Necrodusts as compensation.");
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
            add(DarkPowerHelper.DP, "%d DP / %d DP");
            add(WorldEvent.Status.ONGOING.getTranslationKey().getString(), "Ongoing");
            add(WorldEvent.Status.VICTORY.getTranslationKey().getString(), "Victory");
            add(WorldEvent.Status.LOSS.getTranslationKey().getString(), "Defeat");
            add(WorldEvent.Status.STOPPED.getTranslationKey().getString(), "Stopped");
            add(FirstStrike.ID, "First Strike");
            add(FirstStrike.HINT_START_1.name(), "%1$s's first night is coming... Let's make an onslaught!");
            add(FirstStrike.HINT_START_2.name(), "Have you prepared enough, %1$s?");
            add(FirstStrike.HINT_WARNING.name(), "Survive the first strike of the necro-mobs!");
            add(FirstStrike.HINT_R1.name(), "The first batch of the necro-mobs is coming!");
            add(FirstStrike.HINT_R2.name(), "More necro-mobs are coming!");
            add(FirstStrike.HINT_R3.name(), "More necro-mobs are coming!");
            add(FirstStrike.HINT_R4.name(), "The last batch of the necro-mobs is coming!");
        }

        private void addSubtitles() {
            addSound(ModSoundEvents.NECROCK_ITEM_BASE_PLACE, "Necrock Item Base fills");
            addSound(ModSoundEvents.NECROCK_ITEM_BASE_REMOVE, "Necrock Item Base empties");
            addSound(ModSoundEvents.NECROCK_ITEM_CARRIER_PLACE, "Necrock Item Carrier fills");
            addSound(ModSoundEvents.NECROCK_ITEM_CARRIER_REMOVE, "Necrock Item Carrier empties");
            addSound(ModSoundEvents.NECROCRAFT_FINISHED, "Necrocraft finished");
            addSound(ModSoundEvents.NECRO_GOLEM_AMBIENT, "Necro Golem groans");
            addSound(ModSoundEvents.NECRO_GOLEM_DEATH, "Necro Golem dies");
            addSound(ModSoundEvents.NECRO_GOLEM_HURT, "Necro Golem hurts");
        }

        private void addSound(Supplier<SoundEvent> soundSup, String name) {
            add(SoundDataGen.makeSubtitle(soundSup), name);
        }

        private void addCommand(String commandName, String messageId, String name) {
            add(Necromancer.prefixCmd(commandName, messageId), name);
        }

        private void addCommand(SimpleCommandExceptionType type, String name) {
            add(type.toString(), name);
        }

        private void addGameRule(GameRules.Key<?> key, String name) {
            addGameRule(key, name, null);
        }

        private void addGameRule(GameRules.Key<?> key, String name, @Nullable String description) {
            add("gamerule." + key.getId(), name);
            if (description != null) {
                add("gamerule." + key.getId() + ".description", description);
            }
        }

        @Override
        protected String getSpawnEggSuffix() {
            return " Spawn Egg";
        }
    }
}
