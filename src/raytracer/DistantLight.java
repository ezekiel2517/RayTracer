package raytracer;

import math.Matrix44D;
import math.Vec3D;

public class DistantLight extends Light {
    private Vec3D direction;

    public DistantLight(Matrix44D lightToWorld, Vec3D color, double intensity) {
        this.lightToWorld = lightToWorld;
        this.color = color;
        this.intensity = intensity;
        direction = lightToWorld.multiplyDirection(new Vec3D(0, 0, -1)).normalize();
    }

    @Override
    public Illumination illuminate(Vec3D point) {
        return new Illumination(direction, color.multiply(intensity), Double.POSITIVE_INFINITY);
    }
}
