package raytracer;

import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 10.05.16.
 */
public class Disk extends Object {
    public Vec3D center;
    public Vec3D normal;
    public double radius;
    public double radius2;

    public Disk(Matrix44D objectToWorld, Vec3D albedo, double radius) {
        super(objectToWorld);
        this.albedo = albedo;
        center = objectToWorld.multiplyPoint(new Vec3D());
        normal = objectToWorld.multiplyDirection(new Vec3D(0, 1, 0));
        this.materialType = MaterialType.PHONG;
        type = "disk";
        this.radius = radius;
        radius2 = radius * radius;
    }

    @Override
    public Double intersect(Ray ray) {
        Double t = Plane.intersect(ray, center, normal);
        if (t != null) {
            Vec3D p = ray.origin.add(ray.direction.multiply(t));
            Vec3D v = p.subtract(center);
            double d2 = v.dotProduct(v);
            return d2 <= radius2 ? t : null;
        }
        return null;
    }

    @Override
    public SurfaceProperties getSurfaceProperties(Vec3D hitPoint) {
        Vec2D tex = new Vec2D(0,0);
        //tex.x = hitPoint.getX();
        //tex.y = hitPoint.getZ();
        Vec3D v = worldToObject.multiplyPoint(hitPoint);
        tex.x = v.getX();
        tex.y = v.getZ();
        SurfaceProperties surfaceProperties = new SurfaceProperties();
        surfaceProperties.hitNormal = new Vec3D(normal);
        surfaceProperties.hitTextureCoordinates = tex;
        return surfaceProperties;
    }
}
