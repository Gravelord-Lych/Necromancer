package lych.necromancer.command;

import lych.necromancer.Necromancer;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Necromancer.MODID)
public final class ModCommands {
    private ModCommands() {}

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Necromancer.MODID)
                .then(FirstStrikeCommand.register()));
    }
}
