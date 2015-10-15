package raytracer;

import math.Vec3D;

public class Illumination {
    public Vec3D lightDirection;
    public Vec3D lightIntensity;
    public double distance;

    public Illumination(Vec3D lightDirection, Vec3D lightIntensity, double distance) {
        this.lightDirection = lightDirection;
        this.lightIntensity = lightIntensity;
        this.distance = distance;
    }
}
