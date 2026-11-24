package net.launchers.mod.entity;

import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.initializer.LMEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public class LauncherBlockEntity extends AbstractLauncherBlockEntity {
    public LauncherBlockEntity(BlockPos pos, BlockState state) {
        super(LMEntities.LAUNCHER_BLOCK_ENTITY.get(), pos, state);
    }
}
