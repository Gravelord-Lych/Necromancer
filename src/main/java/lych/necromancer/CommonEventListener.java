package lych.necromancer;

import lych.necromancer.block.entity.ItemCarrier;
import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.world.event.FirstStrikeManager;
import lych.necromancer.capability.world.event.IFirstStrikeDataStorage;
import lych.necromancer.capability.world.event.OnslaughtManager;
import lych.necromancer.util.mixin.IEnderDragonMixin;
import lych.necromancer.world.level.EnderHighlighter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.end.EndDragonFight;
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
            if (level.dimension() == Level.END) {
                EndDragonFight fight = level.getDragonFight();
                if (fight != null && fight.getDragonUUID() != null && level.getEntity(fight.getDragonUUID()) instanceof EnderDragon dragon) {
                    ((IEnderDragonMixin) dragon).traverseNodes();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && player.level().dimension() == Level.END) {
            EnderHighlighter.draw(player.serverLevel(), player);
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
