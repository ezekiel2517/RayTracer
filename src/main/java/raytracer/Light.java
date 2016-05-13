package raytracer;

//import com.google.gson.annotations.Expose;
import math.Matrix44D;
import math.Vec3D;

public abstract class Light {
    //@Expose
    protected Vec3D color;
    //@Expose
    protected double intensity;
    //@Expose
    protected
    Matrix44D lightToWorld;
    //@Expose
    public String type = "";

    public Light(Matrix44D lightToWorld, Vec3D color, double intensity) {
        this.lightToWorld = lightToWorld;
        this.color = color;
        this.intensity = intensity;
    }

    public abstract Illumination illuminate(Vec3D point);
}
