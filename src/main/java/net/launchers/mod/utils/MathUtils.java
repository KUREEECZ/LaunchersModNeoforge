package net.launchers.mod.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class MathUtils
{
    public static Vec3 fromDirection(Direction direction)
    {
        return switch (direction) {
            case UP -> new Vec3(0, 1, 0);
            case DOWN -> new Vec3(0, -1, 0);
            case EAST -> new Vec3(1, 0, 0);
            case WEST -> new Vec3(-1, 0, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
            default -> new Vec3(0, 0, 0);
        };
    }
    
    public static AABB getExpansionBlock(BlockPos pos, Direction facing)
    {
        AABB res = new AABB(pos);
        switch(facing)
        {
            case UP:
                res.inflate(0.2D, 1.2D, 0.2D);
            case DOWN:
                res.inflate(0.2D, 1.2D, 0.2D);
            case EAST:
                res.inflate(1.2D, 0.2D, 0.2D);
            case WEST:
                res.inflate(1.2D, 0.2D, 0.2D);
            case NORTH:
                res.inflate(0.2D, 0.2D, 1.2D);
            case SOUTH:
                res.inflate(0.2D, 0.2D, 1.2D);
            default:
                res.inflate(0, 0, 0);
        }
        System.out.println(res);
        return res;
    }
}
