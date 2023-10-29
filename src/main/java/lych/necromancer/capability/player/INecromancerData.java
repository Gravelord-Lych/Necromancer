package lych.necromancer.capability.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface INecromancerData extends INBTSerializable<CompoundTag> {
    int getKeepInventoryTimes();

    void setKeepInventoryTimes(int keepInventoryTimes);
}
