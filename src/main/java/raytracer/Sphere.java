package raytracer;

import math.*;

public class Sphere extends Object {
    private double radius, radius2;
    private Vec3D center;

    public Sphere(Matrix44D objectToWorld, double radius, Vec3D albedo, MaterialType materialType) {
        super(objectToWorld);
        center = objectToWorld.multiplyPoint(new Vec3D());
        this.radius = radius;
        radius2 = radius * radius;
        this.albedo = albedo;
        this.materialType = materialType;
    }

    @Override
    public Double intersect(Ray ray) {
        Vec3D l = ray.origin.subtract(center);
        double a = ray.direction.dotProduct(ray.direction);
        double b = 2 * ray.direction.dotProduct(l);
        double c = l.dotProduct(l) - radius2;
        QuadraticRoots roots = Utils.solveQuadratic(a, b, c);
        if (roots == null) return null;
        double t0, t1;
        if (roots.x0 > roots.x1) {
            t0 = roots.x1;
            t1 = roots.x0;
        } else {
            t0 = roots.x0;
            t1 = roots.x1;
        }
        if (t0 < 0) {
            t0 = t1;
            if (t0 < 0) return null;
        }
        return t0;
    }

    @Override
    public SurfaceProperties getSurfaceProperties(Vec3D hitPoint) {
        Vec3D hitNormal = hitPoint.subtract(center).normalize();
        Vec2D hitTextureCoordinates = new Vec2D(0,0);
        hitTextureCoordinates.x = 1 + (Math.atan2(hitNormal.getZ(), hitNormal.getX()) / Math.PI) * 0.5;
        hitTextureCoordinates.y = Math.acos(hitNormal.getY()) / Math.PI;
        SurfaceProperties props = new SurfaceProperties();
        props.hitNormal = hitNormal;
        props.hitTextureCoordinates = hitTextureCoordinates;
        return props;
    }
}
