package math;

public class Vec3D {
    private double x, y, z;

    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3D add(Vec3D v) {
        return new Vec3D(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public Vec3D subtract(Vec3D v) {
        return new Vec3D(x - v.getX(), y - v.getY(), z - v.getZ());
    }

    public double dotProduct(Vec3D v) {
        return x * v.getX() + y * v.getY() + z * v.getZ();
    }

    public Vec3D crossProduct(Vec3D v) {
        return new Vec3D(y * v.getZ() - z * v.getY(), z * v.getX() - x * v.getZ(), x * v.getY() - y * v.getX());
    }

    public double length() {
        return Math.sqrt(dotProduct(this));
    }

    public void normalize() {
        double factor = 1.0 / Math.sqrt(dotProduct(this));
        x *= factor;
        y *= factor;
        z *= factor;
    }
}
