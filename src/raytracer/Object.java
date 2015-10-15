package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Object {
    protected Matrix44D objectToWorld, worldToObject;
    protected Vec3D albedo;

    public abstract Double intersect(Ray ray);
    public abstract Vec3D getSurfaceProperties(Vec3D hitPoint);
}
