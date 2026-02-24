package com.mqsmycmz.forging_and_crafting.api.pipe;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PipeShapeCache {
    private final Map<BlockState, VoxelShape> cache = new ConcurrentHashMap<>();
    private final Function<BlockState, VoxelShape> shapeFactory;

    public PipeShapeCache(Function<BlockState, VoxelShape> shapeFactory) {
        this.shapeFactory = shapeFactory;
    }

    public VoxelShape get(BlockState state) {
        return cache.computeIfAbsent(state, shapeFactory);
    }
}
