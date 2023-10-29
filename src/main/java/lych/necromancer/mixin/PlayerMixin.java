package lych.necromancer.mixin;

import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.util.KeepInventoryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"), cancellable = true)
    private void doNotDropEquipment(CallbackInfo ci) {
        getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(data -> {
            if (KeepInventoryHelper.effectiveOn((Player) (Object) this)) {
                ci.cancel();
            }
        });
    }
}
