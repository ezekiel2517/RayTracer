package raytracer;

import math.Vec3D;

public class Ray {
    public Vec3D origin, direction;

    public Ray(Vec3D origin, Vec3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }
}
