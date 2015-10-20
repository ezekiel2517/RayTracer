package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Object {
    public enum MaterialType {PHONG, REFLECTIVE, REFLECTIVE_AND_REFRACTIVE}
    protected Matrix44D objectToWorld, worldToObject;
    protected Vec3D albedo;
    protected MaterialType materialType;
    protected double kd = 0.7, ks = 0.3, n = 10;
    protected double ior = 5;
    protected Texture texture;

    public abstract Double intersect(Ray ray);
    public abstract SurfaceProperties getSurfaceProperties(Vec3D hitPoint);
}
