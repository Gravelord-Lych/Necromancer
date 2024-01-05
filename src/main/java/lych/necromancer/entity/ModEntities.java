package lych.necromancer.entity;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.monster.NecroGolem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Necromancer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Necromancer.MODID);
    public static final RegistryObject<EntityType<NecroGolem>> NECRO_GOLEM = register(ModEntityNames.NECRO_GOLEM,
            () -> EntityType.Builder.of(NecroGolem::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));

    private ModEntities() {}

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(NECRO_GOLEM.get(), NecroGolem.createAttributes().build());
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<? extends EntityType.Builder<T>> sup) {
        return ENTITIES.register(name, () -> sup.get().build(name));
    }
}
