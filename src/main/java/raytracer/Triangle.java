package raytracer;

import math.Matrix44D;
import math.Vec3D;

/**
 * Created by piotr on 05.11.15.
 */
public class Triangle extends Object {
    public Vec3D v0, v1, v2, normal;
    public double u, v;

    public Triangle(Matrix44D objectToWorld) {
        super(objectToWorld);
    }

    @Override
    public Double intersect(Ray ray) {
        // Moller-Trumbore algorithm
        Vec3D v0v1 = v1.subtract(v0);
        Vec3D v0v2 = v2.subtract(v0);
        Vec3D pvec = ray.direction.crossProduct(v0v2);

        normal = v0v1.crossProduct(v0v2).normalize();

        double det = v0v1.dotProduct(pvec);
        if (Options.culling)
            if (det < Options.kEpsilon)
                return null;
        else
            if (Math.abs(det) < Options.kEpsilon)
                return null;
        double invDet = 1 / det;
        Vec3D tvec = ray.origin.subtract(v0);
        u = tvec.dotProduct(pvec) * invDet;
        if (u < 0 || u > 1)
            return null;
        Vec3D qvec = tvec.crossProduct(v0v1);
        v = ray.direction.dotProduct(qvec) * invDet;
        if (v < 0 || u + v > 1)
            return null;
        return v0v2.dotProduct(qvec) * invDet;
    }

    @Override
    public SurfaceProperties getSurfaceProperties(Vec3D hitPoint) {
        SurfaceProperties props = new SurfaceProperties();
        props.hitNormal = normal;
        return props;
    }
}
