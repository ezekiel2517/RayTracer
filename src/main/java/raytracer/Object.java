package raytracer;

//import com.google.gson.annotations.Expose;
import math.Matrix44D;
import math.Vec3D;

import java.lang.reflect.Type;

public abstract class Object {

    public double ior2 = 0;

    public enum MaterialType {PHONG, REFLECTIVE, REFLECTIVE_AND_REFRACTIVE, CONDUCTOR}

    //@Expose
    public Matrix44D objectToWorld;
    public Matrix44D worldToObject;
    //@Expose
    public Vec3D albedo;
    //@Expose
    public Texture texture;
    //@Expose
    public MaterialType materialType;
    //@Expose
    public double ior = 1.5; // index of refraction
    //@Expose
    public double kd = 1.0; // Phong model diffuse weight
    //@Expose
    public double ks = 0.0; // Phong model specular weight
    //@Expose
    public double n = 10; // Phong model specular exponent

    //@Expose
    public String type = "";

    public Object() {}

    public Object(Matrix44D objectToWorld) {
        this.objectToWorld = objectToWorld;
        worldToObject = objectToWorld.inverse();
    }

    public abstract Double intersect(Ray ray);

    public abstract SurfaceProperties getSurfaceProperties(Vec3D hitPoint);
}
