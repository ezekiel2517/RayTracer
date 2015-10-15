package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Object {
    public enum MaterialType {PHONG}
    protected Matrix44D objectToWorld, worldToObject;
    protected Vec3D albedo;
    protected MaterialType materialType;
    protected double kd = 0.8, ks = 0.2, n = 10;

    public abstract Double intersect(Ray ray);
    public abstract Vec3D getSurfaceProperties(Vec3D hitPoint);
}
