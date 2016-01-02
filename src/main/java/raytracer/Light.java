package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Light {
    protected Vec3D color;
    protected double intensity;
    protected Matrix44D lightToWorld;

    public Light(Matrix44D lightToWorld, Vec3D color, double intensity) {
        this.lightToWorld = lightToWorld;
        this.color = color;
        this.intensity = intensity;
    }

    public abstract Illumination illuminate(Vec3D point);
}
