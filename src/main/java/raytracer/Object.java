package raytracer;

import math.Matrix44D;
import math.Vec3D;

public abstract class Object {

    public enum MaterialType {PHONG, REFLECTIVE, REFLECTIVE_AND_REFRACTIVE}

    public Matrix44D objectToWorld, worldToObject;
    public Vec3D albedo;
    public Texture texture;
    public MaterialType materialType;
    public double ior = 1.5; // index of refraction
    public double kd = 0.7; // Phong model diffuse weight
    public double ks = 0.3; // Phong model specular weight
    public double n = 10; // Phong model specular exponent

    public Object(Matrix44D objectToWorld) {
        this.objectToWorld = objectToWorld;
        worldToObject = objectToWorld.inverse();
    }

    public abstract Double intersect(Ray ray);
    public abstract SurfaceProperties getSurfaceProperties(Vec3D hitPoint);
}
