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
        if (v == null) throw new IllegalArgumentException();
        return new Vec3D(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public Vec3D subtract(Vec3D v) {
        if (v == null) throw new IllegalArgumentException();
        return new Vec3D(x - v.getX(), y - v.getY(), z - v.getZ());
    }

    public double dotProduct(Vec3D v) {
        if (v == null) throw new IllegalArgumentException();
        return x * v.getX() + y * v.getY() + z * v.getZ();
    }

    public Vec3D crossProduct(Vec3D v) {
        if (v == null) throw new IllegalArgumentException();
        return new Vec3D(y * v.getZ() - z * v.getY(), z * v.getX() - x * v.getZ(), x * v.getY() - y * v.getX());
    }

    public double length() {
        return Math.sqrt(dotProduct(this));
    }

    public void normalize() {
        double lengthSquared = dotProduct(this);
        if (lengthSquared == 0) throw new ZeroVectorNormalizationException();
        double factor = 1.0 / Math.sqrt(lengthSquared);
        x *= factor;
        y *= factor;
        z *= factor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3D vec3D = (Vec3D) o;

        if (Double.compare(vec3D.x, x) != 0) return false;
        if (Double.compare(vec3D.y, y) != 0) return false;
        return Double.compare(vec3D.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
