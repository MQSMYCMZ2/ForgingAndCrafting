package com.mqsmycmz.forging_and_crafting.api.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface IPipeBlock {
    BooleanProperty NORTH = BlockStateProperties.NORTH;
    BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    BooleanProperty EAST = BlockStateProperties.EAST;
    BooleanProperty WEST = BlockStateProperties.WEST;
    BooleanProperty UP = BlockStateProperties.UP;
    BooleanProperty DOWN = BlockStateProperties.DOWN;
    BooleanProperty CONNECTED = BooleanProperty.create("boolean");

    static BooleanProperty getConnection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    static boolean hasAnyConnection(BlockState state) {
        return state.getValue(NORTH) || state.getValue(SOUTH) ||
                state.getValue(EAST) || state.getValue(WEST) ||
                state.getValue(UP) || state.getValue(DOWN);
    }

    boolean canPipeConnect(BlockState state, BlockPos pos, Level level, Direction direction);
}
