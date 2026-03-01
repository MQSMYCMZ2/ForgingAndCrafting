package com.mqsmycmz.forging_and_crafting.api.pipe;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class PipeShapeBuilder {

    private final VoxelShape[] northShape;
    private final VoxelShape[] southShape;
    private final VoxelShape[] eastShape;
    private final VoxelShape[] westShape;
    private final VoxelShape[] upShape;
    private final VoxelShape[] downShape;
    private VoxelShape shape;
    private final List<VoxelShape> parts = new ArrayList<>();



    public PipeShapeBuilder(VoxelShape[] shape, VoxelShape[] northShape,VoxelShape[] southShape,
                            VoxelShape[] eastShape, VoxelShape[] westShape,
                            VoxelShape[] upShape, VoxelShape[] downShape)  {
        this.shape = mergeShapes(shape);
        this.northShape = northShape;
        this.southShape = southShape;
        this.eastShape = eastShape;
        this.westShape = westShape;
        this.upShape = upShape;
        this.downShape = downShape;
    }

    private VoxelShape mergeShapes(VoxelShape[] shapes) {
        if (shapes == null || shapes.length == 0) {
            return Shapes.empty();
        }
        VoxelShape result = shapes[0];
        for (int i = 1; i < shapes.length; i++) {
            result = Shapes.or(result, shapes[i]);
        }
        return result;
    }

    public PipeShapeBuilder addNorth() {
        addParts(northShape);
        return this;
    }

    public PipeShapeBuilder addSouth() {
        addParts(southShape);
        return this;
    }

    public PipeShapeBuilder addEast() {
        addParts(eastShape);
        return this;
    }

    public PipeShapeBuilder addWest() {
        addParts(westShape);
        return this;
    }

    public PipeShapeBuilder addUp() {
        addParts(upShape);
        return this;
    }

    public PipeShapeBuilder addDown() {
        addParts(downShape);
        return this;
    }

    private PipeShapeBuilder addParts(VoxelShape[] voxelShapes) {
        for (VoxelShape part : voxelShapes) {
            shape = Shapes.join(shape, part, BooleanOp.OR);
        }
        return this;
    }

    public VoxelShape build() {
        return shape;
    }
}
