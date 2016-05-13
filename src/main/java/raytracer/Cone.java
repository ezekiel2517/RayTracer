package raytracer;

import math.*;

/**
 * Created by piotr on 08.05.16.
 */
public class Cone extends Object {
    public double radius;
    public double radius2;
    public double radius0;
    public double height;
    public Vec3D normal;
    public double h, ymax, ymin;

    public Cone (Matrix44D objectToWorld, double radius0, double radius, double height, Vec3D albedo) {
        super(objectToWorld);
        this.radius = radius;
        radius2 = radius * radius;
        this.radius0 = radius0;
        h = height * radius0 / (radius - radius0);
        this.height = height + h;
        ymax = radius0 == 0 ? 0 : -h;
        System.out.println(ymax);
        ymin = ymax - height;
        this.albedo = albedo;
        this.materialType = MaterialType.PHONG;
        type = "cone";
    }

    @Override
    public Double intersect(Ray ray) {
        Vec3D o = worldToObject.multiplyPoint(ray.origin);
        o.setY(o.getY() + ymax + (ymin - ymax) * 0.5);
        Vec3D d = worldToObject.multiplyDirection(ray.direction);
        double sin2 = radius2 / (radius2 + height * height);
        double cos2 = 1 - sin2;
        double a = d.getX() * d.getX() * cos2 + d.getZ() * d.getZ() * cos2 - d.getY() * d.getY() * sin2;
        double b = 2 * (o.getX() * d.getX() * cos2 + o.getZ() * d.getZ() * cos2 - o.getY() * d.getY() * sin2);
        double c = o.getX() * o.getX() * cos2 + o.getZ() * o.getZ() * cos2 - o.getY() * o.getY() * sin2;
        QuadraticRoots roots = Utils.solveQuadratic(a, b, c);
        boolean isect = false;
        double t0 = 0, t1 = 0, t = Double.POSITIVE_INFINITY;
        Vec3D n = new Vec3D();
        if (roots != null) {
            if (roots.x0 > roots.x1) {
                t0 = roots.x1;
                t1 = roots.x0;
            } else {
                t0 = roots.x0;
                t1 = roots.x1;
            }
            if (t0 < 0) {
                t0 = t1;
            }
            double y = o.getY() + t0 * d.getY();
            if (y > ymax || y < ymin) {
                t0 = t1;
                y = o.getY() + t0 * d.getY();
            }
            if (y >= ymin && y <= ymax && t0 >= 0) {
                isect = true;
                t = t0;
                Vec3D p = o.add(d.multiply(t0));
                Vec3D tangent = new Vec3D(0, 1, 0).crossProduct(new Vec3D(p.getX(), 0, p.getZ()));
                Vec3D bitangent = p.multiply(-1);
                n = objectToWorld.multiplyDirection(tangent.crossProduct(bitangent)).normalize();
            }
            t0 = (ymin - o.getY()) / d.getY();
            double xx = o.getX() + t0 * d.getX();
            double zz = o.getZ() + t0 * d.getZ();
            if (xx * xx + zz * zz <= radius2 && t0 < t && t0 >= 0) {
                isect = true;
                t = t0;
                n = objectToWorld.multiplyDirection(new Vec3D(0, -1, 0));
            }
            if (radius0 != 0) {
                t0 = (ymax - o.getY()) / d.getY();
                xx = o.getX() + t0 * d.getX();
                zz = o.getZ() + t0 * d.getZ();
                if (xx * xx + zz * zz <= radius0 * radius0 && t0 < t && t0 >= 0) {
                    isect = true;
                    t = t0;
                    n = objectToWorld.multiplyDirection(new Vec3D(0, 1, 0));
                }
            }
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
