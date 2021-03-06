package raytracer;

//import com.google.gson.annotations.Expose;
import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

public class Plane extends Object {
    //@Expose
    public Vec3D point;
    //@Expose
    public Vec3D normal;

    public Plane(Matrix44D objectToWorld, Vec3D albedo, MaterialType materialType) {
        super(objectToWorld);
        this.albedo = albedo;
        point = objectToWorld.multiplyPoint(new Vec3D());
        normal = objectToWorld.multiplyDirection(new Vec3D(0, 1, 0));
        this.materialType = materialType;
        type = "plane";
    }

    public static Double intersect(Ray ray, Vec3D point, Vec3D normal) {
        double denom = normal.dotProduct(ray.direction);
        if (Math.abs(denom) > Options.kEpsilon) {
            Vec3D pointOrigin = point.subtract(ray.origin);
            double t = pointOrigin.dotProduct(normal) / denom;
            return t >= 0 ? t : null;
        }
        return null;
    }

    @Override
    public Double intersect(Ray ray) {
        return intersect(ray, point, normal);
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
