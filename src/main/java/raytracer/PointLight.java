package raytracer;

import math.Matrix44D;
import math.Vec3D;

public class PointLight extends Light {
    private Vec3D position;

    public PointLight(Matrix44D lightToWorld, Vec3D color, double intensity) {
        super(lightToWorld, color, intensity);
        position = lightToWorld.multiplyPoint(new Vec3D());
    }

    @Override
    public Illumination illuminate(Vec3D point) {
        Vec3D lightDirection = point.subtract(position);
        double r2 = lightDirection.dotProduct(lightDirection);
        double distance = Math.sqrt(r2);
        lightDirection = lightDirection.multiply(1 / distance);
        Vec3D lightIntensity = color.multiply(intensity / (4 * Math.PI * r2));
        return new Illumination(lightDirection, lightIntensity, distance);
    }
}
