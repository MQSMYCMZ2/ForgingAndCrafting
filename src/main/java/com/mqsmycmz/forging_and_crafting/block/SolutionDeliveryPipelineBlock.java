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

public class SolutionDeliveryPipelineBlock extends AbstractPipeBlock {
    public SolutionDeliveryPipelineBlock(Properties pProperties, VoxelShape[] shape) {
        super(pProperties, shape);
    }

    public static final VoxelShape[] shape = { Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5) };

    private static final VoxelShape[] northShape =
            { Block.box(6.45, 6.875, 0, 6.95, 9.625, 0.75),
                    Block.box(6.45, 6.375, 0, 9.2, 6.875, 0.75),
                    Block.box(9.2, 6.375, 0, 9.7, 9.125, 0.75),
                    Block.box(7.45, 6.875, 0.75, 9.2, 7.375, 1.25),
                    Block.box(6.95, 8.625, 0.75, 8.7, 9.125, 1.25),
                    Block.box(8.7, 7.375, 0.75, 9.2, 9.125, 1.25),
                    Block.box(6.95, 6.875, 0.75, 7.45, 8.625, 1.25),
                    Block.box(6.95, 9.125, 0, 9.7, 9.625, 0.75),
                    Block.box(9.1, 7.125, 1.25, 9.35, 8.875, 8),
                    Block.box(6.85, 7.125, 1.25, 7.1, 8.875, 8),
                    Block.box(6.85, 6.875, 1.25, 9.35, 7.125, 8),
                    Block.box(6.85, 8.875, 1.25, 9.35, 9.125, 8) };

    private static final VoxelShape[] southShape =
            { Block.box(6.45, 6.875, 15.25, 6.95, 9.625, 16),
                    Block.box(6.45, 6.375, 15.25, 9.2, 6.875, 16),
                    Block.box(9.2, 6.375, 15.25, 9.7, 9.125, 16),
                    Block.box(7.45, 6.875, 14.75, 9.2, 7.375, 15.25),
                    Block.box(6.95, 8.625, 14.75, 8.7, 9.125, 15.25),
                    Block.box(8.7, 7.375, 14.75, 9.2, 9.125, 15.25),
                    Block.box(6.95, 6.875, 14.75, 7.45, 8.625, 15.25),
                    Block.box(6.95, 9.125, 15.25, 9.7, 9.625, 16),
                    Block.box(9.1, 7.125, 8, 9.35, 8.875, 14.75),
                    Block.box(6.85, 7.125, 8, 7.1, 8.875, 14.75),
                    Block.box(6.85, 6.875, 8, 9.35, 7.125, 14.75),
                    Block.box(6.85, 8.875, 8, 9.35, 9.125, 14.75) };

    private static final VoxelShape[] eastShape =
            { Block.box(15.325, 6.875, 6.375, 16.075, 9.625, 6.875),
                    Block.box(15.325, 6.375, 6.375, 16.075, 6.875, 9.125),
                    Block.box(15.325, 6.375, 9.125, 16.075, 9.125, 9.625),
                    Block.box(14.825, 6.875, 7.375, 15.325, 7.375, 9.125),
                    Block.box(14.825, 8.625, 6.875, 15.325, 9.125, 8.625),
                    Block.box(14.825, 7.375, 8.625, 15.325, 9.125, 9.125),
                    Block.box(14.825, 6.875, 6.875, 15.325, 8.625, 7.375),
                    Block.box(15.325, 9.125, 6.875, 16.075, 9.625, 9.625),
                    Block.box(8.075, 7.125, 9.025, 14.825, 8.875, 9.275),
                    Block.box(8.075, 7.125, 6.775, 14.825, 8.875, 7.025),
                    Block.box(8.075, 6.875, 6.775, 14.825, 7.125, 9.275),
                    Block.box(8.075, 8.875, 6.775, 14.825, 9.125, 9.275) };

    private static final VoxelShape[] westShape =
            { Block.box(0.075, 6.875, 6.375, 0.825, 9.625, 6.875),
                    Block.box(0.075, 6.375, 6.375, 0.825, 6.875, 9.125),
                    Block.box(0.075, 6.375, 9.125, 0.825, 9.125, 9.625),
                    Block.box(0.825, 6.875, 7.375, 1.325, 7.375, 9.125),
                    Block.box(0.825, 8.625, 6.875, 1.325, 9.125, 8.625),
                    Block.box(0.825, 7.375, 8.625, 1.325, 9.125, 9.125),
                    Block.box(0.825, 6.875, 6.875, 1.325, 8.625, 7.375),
                    Block.box(0.075, 9.125, 6.875, 0.825, 9.625, 9.625),
                    Block.box(1.325, 7.125, 9.025, 8.075, 8.875, 9.275),
                    Block.box(1.325, 7.125, 6.775, 8.075, 8.875, 7.025),
                    Block.box(1.325, 6.875, 6.775, 8.075, 7.125, 9.275),
                    Block.box(1.325, 8.875, 6.775, 8.075, 9.125, 9.275) };

    private static final VoxelShape[] upShape =
            { Block.box(9.2, 15.25, 6.375, 9.7, 16, 9.125),
                    Block.box(6.95, 15.25, 9.125, 9.7, 16, 9.625),
                    Block.box(6.45, 15.25, 6.875, 6.95, 16, 9.625),
                    Block.box(6.45, 15.25, 6.375, 9.2, 16, 6.875),
                    Block.box(7.45, 14.75, 6.875, 9.2, 15.25, 7.375),
                    Block.box(6.95, 14.75, 8.625, 8.7, 15.25, 9.125),
                    Block.box(6.95, 14.75, 6.875, 7.45, 15.25, 8.625),
                    Block.box(8.7, 14.75, 7.375, 9.2, 15.25, 9.125),
                    Block.box(6.95, 8, 6.775, 7.2, 14.75, 9.275),
                    Block.box(7.2, 8, 9.025, 8.95, 14.75, 9.275),
                    Block.box(8.95, 8, 6.775, 9.2, 14.75, 9.275),
                    Block.box(7.2, 8, 6.775, 8.95, 14.75, 7.025) };

    private static final VoxelShape[] downShape =
            { Block.box(6.45, 0, 6.375, 9.2, 0.75, 6.875),
                    Block.box(9.2, 0, 6.375, 9.7, 0.75, 9.125),
                    Block.box(6.95, 0, 9.125, 9.7, 0.75, 9.625),
                    Block.box(8.7, 0.75, 7.375, 9.2, 1.25, 9.125),
                    Block.box(6.95, 0.75, 6.875, 7.45, 1.25, 8.625),
                    Block.box(6.95, 0.75, 8.625, 8.7, 1.25, 9.125),
                    Block.box(7.45, 0.75, 6.875, 9.2, 1.25, 7.375),
                    Block.box(6.45, 0, 6.875, 6.95, 0.75, 9.625),
                    Block.box(7.2, 1.25, 9.025, 8.95, 8, 9.275),
                    Block.box(7.2, 1.25, 6.775, 8.95, 8, 7.025),
                    Block.box(8.95, 1.25, 6.775, 9.2, 8, 9.275),
                    Block.box(6.95, 1.25, 6.775, 7.2, 8, 9.275) };

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
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }
}