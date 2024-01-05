package lych.necromancer.sound;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockNames;
import lych.necromancer.entity.ModEntityNames;
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
    public static final RegistryObject<SoundEvent> NECRO_GOLEM_AMBIENT = registerAmbient(ModEntityNames.NECRO_GOLEM);
    public static final RegistryObject<SoundEvent> NECRO_GOLEM_DEATH = registerDeath(ModEntityNames.NECRO_GOLEM);
    public static final RegistryObject<SoundEvent> NECRO_GOLEM_HURT = registerHurt(ModEntityNames.NECRO_GOLEM);
    public static final RegistryObject<SoundEvent> NECRO_GOLEM_STEP = registerStep(ModEntityNames.NECRO_GOLEM);
    public static final RegistryObject<SoundEvent> NECROCRAFT_FINISHED = register("necrocraft.finished");

    private static RegistryObject<SoundEvent> registerAmbient(String name) {
        return register("%s.ambient".formatted(name));
    }

    private static RegistryObject<SoundEvent> registerDeath(String name) {
        return register("%s.death".formatted(name));
    }

    private static RegistryObject<SoundEvent> registerHurt(String name) {
        return register("%s.hurt".formatted(name));
    }

    private static RegistryObject<SoundEvent> registerStep(String name) {
        return register("%s.step".formatted(name));
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Necromancer.prefix(name)));
    }

    private ModSoundEvents() {}
}
