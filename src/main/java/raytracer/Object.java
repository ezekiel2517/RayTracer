package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Object {
    public enum MaterialType {PHONG, REFLECTIVE, REFLECTIVE_AND_REFRACTIVE}
    public Matrix44D objectToWorld, worldToObject;
    public Vec3D albedo;
    public MaterialType materialType;
    public double kd = 0.7, ks = 0.3, n = 10;
    public double ior = 1.5;
    public Texture texture;

    public Object(Matrix44D objectToWorld) {
        this.objectToWorld = objectToWorld;
        this.worldToObject = objectToWorld.inverse();
    }

    public abstract Double intersect(Ray ray);
    public abstract SurfaceProperties getSurfaceProperties(Vec3D hitPoint);
}
