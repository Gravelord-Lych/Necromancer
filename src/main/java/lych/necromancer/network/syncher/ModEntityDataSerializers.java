package lych.necromancer.network.syncher;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.monster.NecromancyGenerated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

import static net.minecraft.network.syncher.EntityDataSerializer.simpleEnum;

public final class ModEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.ENTITY_DATA_SERIALIZERS, Necromancer.MODID);
    public static final RegistryObject<EntityDataSerializer<Optional<NecromancyGenerated.Property>>> NECROMANCY_GENERATED_MOB_PROPERTY =
            DATA_SERIALIZERS.register(ModEntityDataSerializerNames.NECROMANCY_GENERATED_MOB_PROPERTY, () -> optionalEnum(NecromancyGenerated.Property.class));
    public static final RegistryObject<EntityDataSerializer<NecromancyGenerated.Size>> NECROMANCY_GENERATED_MOB_SIZE =
            DATA_SERIALIZERS.register(ModEntityDataSerializerNames.NECROMANCY_GENERATED_MOB_SIZE, () -> simpleEnum(NecromancyGenerated.Size.class));

    private static <T extends Enum<T>> EntityDataSerializer<Optional<T>> optionalEnum(Class<T> enumClass) {
        return EntityDataSerializer.optional(FriendlyByteBuf::writeEnum, enumReader(enumClass));
    }

    private static <T extends Enum<T>> FriendlyByteBuf.Reader<T> enumReader(Class<T> enumClass) {
        return buf -> buf.readEnum(enumClass);
    }

    private ModEntityDataSerializers() {}
}
