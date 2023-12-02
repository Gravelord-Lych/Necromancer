package lych.necromancer;

import lych.necromancer.block.entity.ItemCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
}
