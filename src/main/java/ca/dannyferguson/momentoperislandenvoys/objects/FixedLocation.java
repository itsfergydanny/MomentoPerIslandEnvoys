package ca.dannyferguson.momentoperislandenvoys.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class FixedLocation {
    private World world;
    private int x;
    private int y;
    private int z;

    public FixedLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static FixedLocation fromLocation(Location location) {
        return new FixedLocation(location.getWorld(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
    }

    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    @Override
    public String toString() {
        return "FixedLocation{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
