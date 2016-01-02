package raytracer;

import math.Vec3D;

public class Ray {
    public enum RayType {PRIMARY_RAY, SHADOW_RAY, OTHER}

    public Vec3D origin, direction;
    public RayType type;

    public Ray(Vec3D origin, Vec3D direction, RayType type) {
        this.origin = origin;
        this.direction = direction.normalize();
        this.type = type;
    }
}
