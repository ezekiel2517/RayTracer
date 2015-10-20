package raytracer;

import math.Matrix44D;

public class Camera {
    public double fov = 45;
    public Matrix44D cameraToWorld = new Matrix44D();
}
