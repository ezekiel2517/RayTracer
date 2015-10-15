package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Light {
    protected Vec3D color;
    protected double intensity;
    protected Matrix44D lightToWorld;

    public abstract Illumination illuminate(Vec3D point);
}
