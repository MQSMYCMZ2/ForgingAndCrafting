package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.api.pipe.AbstractPipeBlock;
import com.mqsmycmz.forging_and_crafting.api.pipe.IPipeBlock;
import com.mqsmycmz.forging_and_crafting.api.pipe.PipeShapeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectricEnergyTransmissionPipelineBlock extends AbstractPipeBlock {
    public ElectricEnergyTransmissionPipelineBlock(Properties properties, VoxelShape[] shape) {
        super(properties, shape);
    }

    public static final VoxelShape[] shape = { Block.box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5) };

    public final VoxelShape[] northShape =
            { Block.box(6.5, 6.5, 0, 9.5, 9.5, 8),
                    Block.box(6.25, 6.5, 0, 6.5, 9.75, 0.5),
                    Block.box(9.5, 6.25, 0, 9.75, 9.5, 0.5),
                    Block.box(6.5, 9.5, 0, 9.75, 9.75, 0.5),
                    Block.box(6.25, 6.25, 0, 9.5, 6.5, 0.5),
                    Block.box(6.25, 7.25, 0.5, 6.5, 8.75, 8),
                    Block.box(9.5, 7.25, 0.5, 9.75, 8.75, 8),
                    Block.box(7.25, 9.5, 0.5, 8.75, 9.75, 8),
                    Block.box(7.25, 6.25, 0.5, 8.75, 6.5, 8) };

    public final VoxelShape[] southShape =
            { Block.box(6.5, 6.5, 8, 9.5, 9.5, 16),
                    Block.box(6.25, 6.5, 15.5, 6.5, 9.75, 16),
                    Block.box(9.5, 6.25, 15.5, 9.75, 9.5, 16),
                    Block.box(6.5, 9.5, 15.5, 9.75, 9.75, 16),
                    Block.box(6.25, 6.25, 15.5, 9.5, 6.5, 16),
                    Block.box(6.25, 7.25, 8, 6.5, 8.75, 15.5),
                    Block.box(9.5, 7.25, 8, 9.75, 8.75, 15.5),
                    Block.box(7.25, 9.5, 8, 8.75, 9.75, 15.5),
                    Block.box(7.25, 6.25, 8, 8.75, 6.5, 15.5) };

    public final VoxelShape[] eastShape =
            { Block.box(8, 6.5, 6.5, 16, 9.5, 9.5),
                    Block.box(15.5, 6.5, 9.5, 16, 9.75, 9.75),
                    Block.box(15.5, 6.25, 6.25, 16, 9.5, 6.5),
                    Block.box(15.5, 9.5, 6.25, 16, 9.75, 9.5),
                    Block.box(15.5, 6.25, 6.5, 16, 6.5, 9.75),
                    Block.box(8, 7.25, 9.5, 15.5, 8.75, 9.75),
                    Block.box(8, 7.25, 6.25, 15.5, 8.75, 6.5),
                    Block.box(8, 9.5, 7.25, 15.5, 9.75, 8.75),
                    Block.box(8, 6.25, 7.25, 15.5, 6.5, 8.75) };

    public final VoxelShape[] westShape =
            { Block.box(0, 6.5, 6.5, 8, 9.5, 9.5),
                    Block.box(0, 6.5, 9.5, 0.5, 9.75, 9.75),
                    Block.box(0, 6.25, 6.25, 0.5, 9.5, 6.5),
                    Block.box(0, 9.5, 6.25, 0.5, 9.75, 9.5),
                    Block.box(0, 6.25, 6.5, 0.5, 6.5, 9.75),
                    Block.box(0.5, 7.25, 9.5, 8, 8.75, 9.75),
                    Block.box(0.5, 7.25, 6.25, 8, 8.75, 6.5),
                    Block.box(0.5, 9.5, 7.25, 8, 9.75, 8.75),
                    Block.box(0.5, 6.25, 7.25, 8, 6.5, 8.75) };

    public final VoxelShape[] upShape =
            { Block.box(6.5, 8, 6.5, 9.5, 16, 9.5),
                    Block.box(9.5, 15.5, 6.5, 9.75, 16, 9.75),
                    Block.box(6.25, 15.5, 6.25, 6.5, 16, 9.5),
                    Block.box(6.25, 15.5, 9.5, 9.5, 16, 9.75),
                    Block.box(6.5, 15.5, 6.25, 9.75, 16, 6.5),
                    Block.box(6.25, 8, 7.25, 6.5, 15.5, 8.75),
                    Block.box(9.5, 8, 7.25, 9.75, 15.5, 8.75),
                    Block.box(7.25, 8, 6.25, 8.75, 15.5, 6.5),
                    Block.box(7.25, 8, 9.5, 8.75, 15.5, 9.75) };

    public final VoxelShape[] downShape =
            { Block.box(6.5, 0, 6.5, 9.5, 8, 9.5),
                    Block.box(9.5, 0, 6.5, 9.75, 0.5, 9.75),
                    Block.box(6.25, 0, 6.25, 6.5, 0.5, 9.5),
                    Block.box(6.25, 0, 9.5, 9.5, 0.5, 9.75),
                    Block.box(6.5, 0, 6.25, 9.75, 0.5, 6.5),
                    Block.box(6.25, 0.5, 7.25, 6.5, 8, 8.75),
                    Block.box(9.5, 0.5, 7.25, 9.75, 8, 8.75),
                    Block.box(7.25, 0.5, 6.25, 8.75, 8, 6.5),
                    Block.box(7.25, 0.5, 9.5, 8.75, 8, 9.75) };

    @Override
    protected VoxelShape createConnectedShape(BlockState state) {
        var builder = new PipeShapeBuilder(shape, northShape, southShape, eastShape, westShape, upShape, downShape);

        if (state.getValue(NORTH)) builder.addNorth();
        if (state.getValue(SOUTH)) builder.addSouth();
        if (state.getValue(EAST))  builder.addEast();
        if (state.getValue(WEST))  builder.addWest();
        if (state.getValue(UP))    builder.addUp();
        if (state.getValue(DOWN))  builder.addDown();

        return builder.build();
    }

    @Override
    public boolean canPipeConnect(BlockState state, BlockPos pos, Level level, Direction direction) {
        if (state.getBlock() instanceof IPipeBlock) {
            return true;
        }

        return level.getBlockEntity(pos) != null;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }
}