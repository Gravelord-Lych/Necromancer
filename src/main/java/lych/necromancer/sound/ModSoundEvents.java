package lych.necromancer.sound;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockNames;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Necromancer.MODID);
    public static final RegistryObject<SoundEvent> NECROCK_ITEM_BASE_PLACE = register("%s.place".formatted(ModBlockNames.NECROCK_ITEM_BASE));
    public static final RegistryObject<SoundEvent> NECROCK_ITEM_BASE_REMOVE = register("%s.remove".formatted(ModBlockNames.NECROCK_ITEM_BASE));
    public static final RegistryObject<SoundEvent> NECROCK_ITEM_CARRIER_PLACE = register("%s.place".formatted(ModBlockNames.NECROCK_ITEM_CARRIER));
    public static final RegistryObject<SoundEvent> NECROCK_ITEM_CARRIER_REMOVE = register("%s.remove".formatted(ModBlockNames.NECROCK_ITEM_CARRIER));

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Necromancer.prefix(name)));
    }

    private ModSoundEvents() {}
}
