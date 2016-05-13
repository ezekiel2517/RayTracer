package raytracer;

import math.*;

/**
 * Created by piotr on 07.05.16.
 */
public class Cylinder extends Object {
    public double radius;
    public double radius2;
    public double height;
    public Vec3D normal;

    public Cylinder(Matrix44D objectToWorld, double radius, Vec3D albedo, double height) {
        super(objectToWorld);
        this.radius = radius;
        radius2 = radius * radius;
        this.height = height;
        this.albedo = albedo;
        this.materialType = MaterialType.PHONG;
        type = "cylinder";
    }

    @Override
    public Double intersect(Ray ray) {
        Vec3D o = worldToObject.multiplyPoint(ray.origin);
        Vec3D d = worldToObject.multiplyDirection(ray.direction);
        double a = d.getX() * d.getX() + d.getZ() * d.getZ();
        double b = 2 * (o.getX() * d.getX() + o.getZ() * d.getZ());
        double c = o.getX() * o.getX() + o.getZ() * o.getZ() - radius;
        QuadraticRoots roots = Utils.solveQuadratic(a, b, c);
        boolean isect = false;
        double y0 = 0, y1 = 0, t0 = 0, t1 = 0, t = Double.POSITIVE_INFINITY;
        Vec3D n = new Vec3D();
        if (roots != null) {
            if (roots.x0 > roots.x1) {
                t0 = roots.x1;
                t1 = roots.x0;
            } else {
                t0 = roots.x0;
                t1 = roots.x1;
            }
            //y0 = o.getY() + t0 * d.getY();
            //y1 = o.getY() + t1 * d.getY();
            if (t0 < 0) {
                t0 = t1;
            }
            double y = o.getY() + t0 * d.getY();
            if (Math.abs(y) > 0.5 * height) {
                t0 = t1;
                y = o.getY() + t0 * d.getY();
            }
            if (Math.abs(y) <= 0.5 * height && t0 >= 0) {
                isect = true;
                t = t0;
                n = objectToWorld.multiplyDirection(new Vec3D(o.getX() + t0 * d.getX(), 0, o.getZ() + t0 * d.getZ())).normalize();
            }
        }
        t0 = (-0.5 * height - o.getY()) / d.getY();
        double xx = o.getX() + t0 * d.getX();
        double zz = o.getZ() + t0 * d.getZ();
        if (xx * xx + zz * zz <= radius2 && t0 < t && t0 >= 0) {
            isect = true;
            t = t0;
            n = objectToWorld.multiplyDirection(new Vec3D(0, -1, 0));
        }
        t0 = (0.5 * height - o.getY()) / d.getY();
        xx = o.getX() + t0 * d.getX();
        zz = o.getZ() + t0 * d.getZ();
        if (xx * xx + zz * zz <= radius2 && t0 < t && t0 >= 0) {
            isect = true;
            t = t0;
            n = objectToWorld.multiplyDirection(new Vec3D(0, 1, 0));
        }
        if (isect) {
            normal = n;
            return t;
        }
        return null;
    }

    @Override
    public SurfaceProperties getSurfaceProperties(Vec3D hitPoint) {
        Vec2D hitTextureCoordinates = new Vec2D(0,0);
        //hitTextureCoordinates.x = 1 + (Math.atan2(hitNormal.getZ(), hitNormal.getX()) / Math.PI) * 0.5;
        //hitTextureCoordinates.y = Math.acos(hitNormal.getY()) / Math.PI;
        SurfaceProperties props = new SurfaceProperties();
        props.hitNormal = new Vec3D(normal);
        props.hitTextureCoordinates = hitTextureCoordinates;
        return props;
    }
}
