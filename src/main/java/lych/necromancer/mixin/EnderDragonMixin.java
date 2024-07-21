package lych.necromancer.mixin;

import lych.necromancer.util.mixin.IEnderDragonMixin;
import lych.necromancer.world.level.DragonPathPainter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(EnderDragon.class)
public class EnderDragonMixin extends Mob implements IEnderDragonMixin {
    @Shadow @Final private Node[] nodes;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private EnderDragonPhaseManager phaseManager;
    @Unique
    private boolean traversed;
    @Unique
    private final Set<Node> nodesTraversed = new HashSet<>();

    private EnderDragonMixin(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    public void traverseNodes() {
        if (traversed) {
            return;
        }
        for (Node node : nodes) {
            if (node != null) {
                System.out.println(node);
                nodesTraversed.add(node);
                traversed = true;
            }
        }
    }

    @Inject(method = "findPath", at = @At("RETURN"))
    private void printFoundPath(int startId, int endId, @Nullable Node expectedToPass, CallbackInfoReturnable<Path> cir) {
        Path path = cir.getReturnValue();
        if (false && path != null) {
            DragonPathPainter.paint(path, nodes[startId], nodes[endId], expectedToPass);
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;getFlySpeed()F"))
    private void logFlyingAndTurningSpeed(CallbackInfo ci) {
        if (tickCount % 10 == 0) {
            LOGGER.info("Speeds: {} + {}", phaseManager.getCurrentPhase().getFlySpeed(), phaseManager.getCurrentPhase().getTurnSpeed());
        }
    }
}
