package raytracer;

import math.Matrix44D;
import math.Vec3D;

public class Camera {
    public double fov = 45;
    public Matrix44D cameraToWorld = new Matrix44D();
    public Matrix44D rotX = new Matrix44D();
    public Matrix44D rotY = new Matrix44D();

    @Override
    public String toString() {
        Vec3D pos = cameraToWorld.multiplyPoint(new Vec3D());
        Vec3D dir = cameraToWorld.multiplyDirection(new Vec3D(0, 0, -1));
        return "cam{pos = " + pos + " dir = " + dir + "}";
    }
}
