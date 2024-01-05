package lych.necromancer.world.event.round;

import lych.necromancer.world.event.AbstractWorldEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public record HintProvider<T extends AbstractWorldEvent<?, ?>>(String name, BiFunction<? super T, ? super ServerPlayer, ? extends Component> provider) {
    public Component getHint(T event, ServerPlayer player) {
        return provider.apply(event, player);
    }
}
