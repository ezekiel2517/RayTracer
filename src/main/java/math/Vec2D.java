package math;

public class Vec2D {
    public double x, y;

    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D multiply(double s) {
        return new Vec2D(x * s, y * s);
    }

    public Vec2D add(Vec2D v) {
        return new Vec2D(x + v.x, y + v.y);
    }
}
