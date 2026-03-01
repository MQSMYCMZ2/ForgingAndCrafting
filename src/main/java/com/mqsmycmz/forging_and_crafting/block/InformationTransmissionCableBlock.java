package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.api.pipe.AbstractPipeBlock;
import com.mqsmycmz.forging_and_crafting.api.pipe.IPipeBlock;
import com.mqsmycmz.forging_and_crafting.api.pipe.PipeShapeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InformationTransmissionCableBlock extends AbstractPipeBlock {
    public InformationTransmissionCableBlock(Properties properties, VoxelShape[] shape) {
        super(properties, shape);
    }

    public static final VoxelShape[] shape = { Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5) };

    private static final VoxelShape[] northShape =
            { Block.box(6.25, 9.25, 0, 9.25, 9.75, 0.5),
                    Block.box(6.25, 6.25, 0, 6.75, 9.25, 0.5),
                    Block.box(6.75, 6.25, 0, 9.75, 6.75, 0.5),
                    Block.box(9.25, 6.75, 0, 9.75, 9.75, 0.5),
                    Block.box(6.75, 6.75, 0.5, 8.25, 7.75, 1),
                    Block.box(6.75, 7.75, 0.5, 7.75, 9.25, 1),
                    Block.box(7.75, 8.25, 0.5, 9.25, 9.25, 1),
                    Block.box(8.25, 6.75, 0.5, 9.25, 8.25, 1),
                    Block.box(6.25, 6.25, 0.5, 6.5, 6.75, 8),
                    Block.box(6.25, 8.975, 0.5, 6.5, 9.475, 8),
                    Block.box(6.25, 9.475, 0.5, 9.5, 9.725, 8),
                    Block.box(9.5, 9.225, 0.5, 9.75, 9.725, 8),
                    Block.box(6.5, 6.25, 0.5, 9.75, 6.5, 8),
                    Block.box(9.5, 6.5, 0.5, 9.75, 7, 8),
                    Block.box(9.5, 7.25, 0.5, 9.75, 7.5, 8),
                    Block.box(9.5, 7.75, 0.5, 9.75, 8, 8),
                    Block.box(9.5, 8.25, 0.5, 9.75, 8.5, 8),
                    Block.box(9.5, 8.75, 0.5, 9.75, 9, 8),
                    Block.box(6.25, 7, 0.5, 6.5, 7.25, 8),
                    Block.box(6.25, 7.5, 0.5, 6.5, 7.75, 8),
                    Block.box(6.25, 8, 0.5, 6.5, 8.25, 8),
                    Block.box(6.25, 8.5, 0.5, 6.5, 8.75, 8),
                    Block.box(8, 7, 1, 8.5, 7.5, 8),
                    Block.box(7, 7.5, 1, 7.5, 8, 8),
                    Block.box(8, 8.75, 1, 8.5, 9.25, 8),
                    Block.box(7.5, 8.25, 1, 8, 8.75, 8),
                    Block.box(8.5, 8, 1, 9, 8.5, 8) };
    
    private static final VoxelShape[] southShape =
            { Block.box(6.25, 9.25, 15.5, 9.25, 9.75, 16),
            Block.box(7.75, 8.25, 15, 9.25, 9.25, 15.5),
            Block.box(6.75, 6.75, 15, 8.25, 7.75, 15.5),
            Block.box(6.75, 7.75, 15, 7.75, 9.25, 15.5),
            Block.box(8.25, 6.75, 15, 9.25, 8.25, 15.5),
            Block.box(6.25, 6.25, 15.5, 6.75, 9.25, 16),
            Block.box(6.75, 6.25, 15.5, 9.75, 6.75, 16),
            Block.box(9.25, 6.75, 15.5, 9.75, 9.75, 16),
            Block.box(6.25, 6.25, 8, 6.5, 6.75, 15.5),
            Block.box(6.25, 8.975, 8, 6.5, 9.475, 15.5),
            Block.box(6.25, 9.475, 8, 9.5, 9.725, 15.5),
            Block.box(9.5, 9.225, 8, 9.75, 9.725, 15.5),
            Block.box(6.5, 6.25, 8, 9.75, 6.5, 15.5),
            Block.box(9.5, 6.5, 8, 9.75, 7, 15.5),
            Block.box(9.5, 7.25, 8, 9.75, 7.5, 15.5),
            Block.box(9.5, 7.75, 8, 9.75, 8, 15.5),
            Block.box(9.5, 8.25, 8, 9.75, 8.5, 15.5),
            Block.box(9.5, 8.75, 8, 9.75, 9, 15.5),
            Block.box(6.25, 7, 8, 6.5, 7.25, 15.5),
            Block.box(6.25, 7.5, 8, 6.5, 7.75, 15.5),
            Block.box(6.25, 8, 8, 6.5, 8.25, 15.5),
            Block.box(6.25, 8.5, 8, 6.5, 8.75, 15.5),
            Block.box(8, 7, 8, 8.5, 7.5, 15),
            Block.box(7, 7.5, 8, 7.5, 8, 15),
            Block.box(8, 7.75, 8, 8.5, 8.25, 15),
            Block.box(7.5, 8.25, 8, 8, 8.75, 15),
            Block.box(8.5, 8, 8, 9, 8.5, 15) };
    
    private static final VoxelShape[] eastShape =
            { Block.box(15.5, 9.25, 6.25, 16, 9.75, 9.25),
            Block.box(15.5, 6.25, 6.25, 16, 9.25, 6.75),
            Block.box(15.5, 6.25, 6.75, 16, 6.75, 9.75),
            Block.box(15.5, 6.75, 9.25, 16, 9.75, 9.75),
            Block.box(15, 6.75, 6.75, 15.5, 7.75, 8.25),
            Block.box(15, 7.75, 6.75, 15.5, 9.25, 7.75),
            Block.box(15, 8.25, 7.75, 15.5, 9.25, 9.25),
            Block.box(15, 6.75, 8.25, 15.5, 8.25, 9.25),
            Block.box(8, 6.25, 6.25, 15.5, 6.75, 6.5),
            Block.box(8, 8.975, 6.25, 15.5, 9.475, 6.5),
            Block.box(8, 9.475, 6.25, 15.5, 9.725, 9.5),
            Block.box(8, 9.225, 9.5, 15.5, 9.725, 9.75),
            Block.box(8, 6.25, 6.5, 15.5, 6.5, 9.75),
            Block.box(8, 6.5, 9.5, 15.5, 7, 9.75),
            Block.box(8, 7.25, 9.5, 15.5, 7.5, 9.75),
            Block.box(8, 7.75, 9.5, 15.5, 8, 9.75),
            Block.box(8, 8.25, 9.5, 15.5, 8.5, 9.75),
            Block.box(8, 8.75, 9.5, 15.5, 9, 9.75),
            Block.box(8, 7, 6.25, 15.5, 7.25, 6.5),
            Block.box(8, 7.5, 6.25, 15.5, 7.75, 6.5),
            Block.box(8, 8, 6.25, 15.5, 8.25, 6.5),
            Block.box(8, 8.5, 6.25, 15.5, 8.75, 6.5),
            Block.box(8, 7, 8, 15, 7.5, 8.5),
            Block.box(8, 7.5, 7, 15, 8, 7.5),
            Block.box(8, 7.75, 8, 15, 8.25, 8.5),
            Block.box(8, 8.25, 7.5, 15, 8.75, 8),
            Block.box(8, 8, 8.5, 15, 8.5, 9) };
    
    private static final VoxelShape[] westShape =
            { Block.box(0, 9.25, 6.75, 0.5, 9.75, 9.75),
            Block.box(0, 6.25, 9.25, 0.5, 9.25, 9.75),
            Block.box(0, 6.25, 6.25, 0.5, 6.75, 9.25),
            Block.box(0, 6.75, 6.25, 0.5, 9.75, 6.75),
            Block.box(0.5, 6.75, 7.75, 1, 7.75, 9.25),
            Block.box(0.5, 7.75, 8.25, 1, 9.25, 9.25),
            Block.box(0.5, 8.25, 6.75, 1, 9.25, 8.25),
            Block.box(0.5, 6.75, 6.75, 1, 8.25, 7.75),
            Block.box(0.5, 6.25, 9.5, 8, 6.75, 9.75),
            Block.box(0.5, 8.975, 9.5, 8, 9.475, 9.75),
            Block.box(0.5, 9.475, 6.5, 8, 9.725, 9.75),
            Block.box(0.5, 9.225, 6.25, 8, 9.725, 6.5),
            Block.box(0.5, 6.25, 6.25, 8, 6.5, 9.5),
            Block.box(0.5, 6.5, 6.25, 8, 7, 6.5),
            Block.box(0.5, 7.25, 6.25, 8, 7.5, 6.5),
            Block.box(0.5, 7.75, 6.25, 8, 8, 6.5),
            Block.box(0.5, 8.25, 6.25, 8, 8.5, 6.5),
            Block.box(0.5, 8.75, 6.25, 8, 9, 6.5),
            Block.box(0.5, 7, 9.5, 8, 7.25, 9.75),
            Block.box(0.5, 7.5, 9.5, 8, 7.75, 9.75),
            Block.box(0.5, 8, 9.5, 8, 8.25, 9.75),
            Block.box(0.5, 8.5, 9.5, 8, 8.75, 9.75),
            Block.box(1, 7, 7.5, 8, 7.5, 8),
            Block.box(1, 7.5, 8.5, 8, 8, 9),
            Block.box(1, 7.75, 7.5, 8, 8.25, 8),
            Block.box(1, 8.25, 8, 8, 8.75, 8.5),
            Block.box(1, 8, 7, 8, 8.5, 7.5) };
    
    private static final VoxelShape[] upShape =
            { Block.box(6.25, 15.4875, 9.2625, 9.25, 15.9875, 9.7625),
            Block.box(6.25, 15.4875, 6.2625, 6.75, 15.9875, 9.2625),
            Block.box(6.75, 15.4875, 6.2625, 9.75, 15.9875, 6.7625),
            Block.box(9.25, 15.4875, 6.7625, 9.75, 15.9875, 9.7625),
            Block.box(6.75, 14.9875, 6.7625, 8.25, 15.4875, 7.7625),
            Block.box(6.75, 14.9875, 7.7625, 7.75, 15.4875, 9.2625),
            Block.box(7.75, 14.9875, 8.2625, 9.25, 15.4875, 9.2625),
            Block.box(8.25, 14.9875, 6.7625, 9.25, 15.4875, 8.2625),
            Block.box(6.25, 7.9875, 6.2625, 6.5, 15.4875, 6.7625),
            Block.box(6.25, 7.9875, 8.9875, 6.5, 15.4875, 9.4875),
            Block.box(6.25, 7.9875, 9.4875, 9.5, 15.4875, 9.7375),
            Block.box(9.5, 7.9875, 9.2375, 9.75, 15.4875, 9.7375),
            Block.box(6.5, 7.9875, 6.2625, 9.75, 15.4875, 6.5125),
            Block.box(9.5, 7.9875, 6.5125, 9.75, 15.4875, 7.0125),
            Block.box(9.5, 7.9875, 7.2625, 9.75, 15.4875, 7.5125),
            Block.box(9.5, 7.9875, 7.7625, 9.75, 15.4875, 8.0125),
            Block.box(9.5, 7.9875, 8.2625, 9.75, 15.4875, 8.5125),
            Block.box(9.5, 7.9875, 8.7625, 9.75, 15.4875, 9.0125),
            Block.box(6.25, 7.9875, 7.0125, 6.5, 15.4875, 7.2625),
            Block.box(6.25, 7.9875, 7.5125, 6.5, 15.4875, 7.7625),
            Block.box(6.25, 7.9875, 8.0125, 6.5, 15.4875, 8.2625),
            Block.box(6.25, 7.9875, 8.5125, 6.5, 15.4875, 8.7625),
            Block.box(8, 7.9875, 7.0125, 8.5, 14.9875, 7.5125),
            Block.box(7, 7.9875, 7.5125, 7.5, 14.9875, 8.0125),
            Block.box(8, 7.9875, 7.7625, 8.5, 14.9875, 8.2625),
            Block.box(7.5, 7.9875, 8.2625, 8, 14.9875, 8.7625),
            Block.box(8.5, 7.9875, 8.0125, 9, 14.9875, 8.5125) };
    
    private static final VoxelShape[] downShape =
            { Block.box(6.25, -0.0125, 9.2625, 9.25, 0.4875, 9.7625),
            Block.box(7.75, 0.4875, 8.2625, 9.25, 0.9875, 9.2625),
            Block.box(6.75, 0.4875, 6.7625, 8.25, 0.9875, 7.7625),
            Block.box(6.75, 0.4875, 7.7625, 7.75, 0.9875, 9.2625),
            Block.box(8.25, 0.4875, 6.7625, 9.25, 0.9875, 8.2625),
            Block.box(6.25, -0.0125, 6.2625, 6.75, 0.4875, 9.2625),
            Block.box(6.75, -0.0125, 6.2625, 9.75, 0.4875, 6.7625),
            Block.box(9.25, -0.0125, 6.7625, 9.75, 0.4875, 9.7625),
            Block.box(6.25, 0.4875, 6.2625, 6.5, 7.9875, 6.7625),
            Block.box(6.25, 0.4875, 8.9875, 6.5, 7.9875, 9.4875),
            Block.box(6.25, 0.4875, 9.4875, 9.5, 7.9875, 9.7375),
            Block.box(9.5, 0.4875, 9.2375, 9.75, 7.9875, 9.7375),
            Block.box(6.5, 0.4875, 6.2625, 9.75, 7.9875, 6.5125),
            Block.box(9.5, 0.4875, 6.5125, 9.75, 7.9875, 7.0125),
            Block.box(9.5, 0.4875, 7.2625, 9.75, 7.9875, 7.5125),
            Block.box(9.5, 0.4875, 7.7625, 9.75, 7.9875, 8.0125),
            Block.box(9.5, 0.4875, 8.2625, 9.75, 7.9875, 8.5125),
            Block.box(9.5, 0.4875, 8.7625, 9.75, 7.9875, 9.0125),
            Block.box(6.25, 0.4875, 7.0125, 6.5, 7.9875, 7.2625),
            Block.box(6.25, 0.4875, 7.5125, 6.5, 7.9875, 7.7625),
            Block.box(6.25, 0.4875, 8.0125, 6.5, 7.9875, 8.2625),
            Block.box(6.25, 0.4875, 8.5125, 6.5, 7.9875, 8.7625),
            Block.box(8, 1.4875, 7.0125, 8.5, 7.9875, 7.5125),
            Block.box(7, 1.4875, 7.5125, 7.5, 7.9875, 8.0125),
            Block.box(8, 1.4875, 7.7625, 8.5, 7.9875, 8.2625),
            Block.box(7.5, 1.4875, 8.2625, 8, 7.9875, 8.7625),
            Block.box(8.5, 1.4875, 8.0125, 9, 7.9875, 8.5125) };

    @Override
    protected VoxelShape createConnectedShape(BlockState state) {
        var builder = new PipeShapeBuilder(shape, northShape, southShape, eastShape, westShape, upShape, downShape);

        if (state.getValue(NORTH)) builder.addNorth();
        if (state.getValue(SOUTH)) builder.addSouth();
        if (state.getValue(EAST)) builder.addEast();
        if (state.getValue(WEST)) builder.addWest();
        if (state.getValue(UP)) builder.addUp();
        if (state.getValue(DOWN)) builder.addDown();

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
