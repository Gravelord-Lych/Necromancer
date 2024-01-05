package lych.necromancer;

import lych.necromancer.block.entity.ItemCarrier;
import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.world.event.FirstStrikeManager;
import lych.necromancer.capability.world.event.IFirstStrikeDataStorage;
import lych.necromancer.capability.world.event.OnslaughtManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Necromancer.MODID)
public final class CommonEventListener {
    private CommonEventListener() {}

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockEntity entity = level.getBlockEntity(pos);

        if (entity instanceof ItemCarrier carrier && !carrier.getItemInside().isEmpty()) {
            event.setCanceled(true);
            ItemCarrier.handleAttack(level, pos, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level instanceof ServerLevel level) {
            level.getCapability(ModCapabilities.FIRST_STRIKE_MANAGER).ifPresent(FirstStrikeManager::tick);
            level.getCapability(ModCapabilities.FIRST_STRIKE_DATA_STORAGE).ifPresent(IFirstStrikeDataStorage::tick);
            level.getCapability(ModCapabilities.ONSLAUGHT_MANAGER).ifPresent(OnslaughtManager::tick);
        }
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.level().getCapability(ModCapabilities.FIRST_STRIKE_MANAGER)
                    .filter(manager -> manager.hasActiveFirstStrikeFor(player))
                    .ifPresent(manager -> event.setResult(Player.BedSleepingProblem.NOT_SAFE));
        }
    }
}
