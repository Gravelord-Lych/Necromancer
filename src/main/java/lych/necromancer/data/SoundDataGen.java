package lych.necromancer.data;

import lych.necromancer.Necromancer;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

import static lych.necromancer.sound.ModSoundEvents.*;

public class SoundDataGen extends SoundDefinitionsProvider {
    public SoundDataGen(PackOutput output, ExistingFileHelper helper) {
        super(output, Necromancer.MODID, helper);
    }

    @Override
    public void registerSounds() {
        redirect(NECROCK_ITEM_BASE_PLACE, NECROCK_ITEM_CARRIER_PLACE);
        redirect(NECROCK_ITEM_BASE_REMOVE, NECROCK_ITEM_CARRIER_REMOVE);
        redirect(NECROCK_ITEM_CARRIER_PLACE, SoundEvents.ITEM_FRAME_ADD_ITEM);
        redirect(NECROCK_ITEM_CARRIER_REMOVE, SoundEvents.ITEM_FRAME_REMOVE_ITEM);
    }

    private SoundDefinition.Sound event(Supplier<SoundEvent> event) {
        return event(event.get());
    }

    private SoundDefinition.Sound event(SoundEvent event) {
        return event(event.getLocation());
    }

    private SoundDefinition.Sound event(ResourceLocation location) {
        return sound(location, SoundDefinition.SoundType.EVENT);
    }

    private void redirect(Supplier<SoundEvent> soundSup, Supplier<SoundEvent> origin) {
        redirect(soundSup, origin.get());
    }

    private void redirect(Supplier<SoundEvent> soundSup, SoundEvent origin) {
        add(soundSup, SoundDefinition.definition()
                .subtitle(makeSubtitle(soundSup))
                .with(event(origin)));
    }

    public static String makeSubtitle(Supplier<SoundEvent> soundSup) {
        return makeSubtitle(soundSup.get().getLocation().getPath());
    }

    public static String makeSubtitle(String name) {
        return Necromancer.prefix("subtitle." + name).toString();
    }
}
