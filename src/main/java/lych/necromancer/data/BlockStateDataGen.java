package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Necromancer.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.getAllGroups().forEach(group -> group.generateBlockStates(this));
        simpleBlock(ModBlocks.NECROITE_BLOCK.get());
    }
}
