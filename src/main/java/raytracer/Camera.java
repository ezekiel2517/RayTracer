package raytracer;

//import com.google.gson.annotations.Expose;
import math.Matrix44D;
import math.Vec3D;

public class Camera {
    //@Expose
    public Matrix44D cameraToWorld = new Matrix44D();
    //@Expose
    public double fov = 67.5;
    //@Expose
    public Vec3D pos = new Vec3D();
    //@Expose
    public double rotationX;
    //@Expose
    public Matrix44D rotX = new Matrix44D();
    //@Expose
    public Matrix44D rotY = new Matrix44D();

    public Camera() {}

    public Camera(double fovdeg) {
        fov = fovdeg;
    }

    public void reset() {
        cameraToWorld = new Matrix44D();
        pos = new Vec3D();
        rotationX = 0;
        rotX = new Matrix44D();
        rotY = new Matrix44D();
    }

    public void translate(Vec3D dir, double d) {
        Vec3D v = cameraToWorld.multiplyDirection(dir).multiply(d);
        pos = pos.add(v);
        cameraToWorld = cameraToWorld.translated(v);
    }

    public void rotateX(double degrees) {
        if (rotationX + degrees > 90) {
            degrees = 90 - rotationX;
            rotationX = 90;
        } else if (rotationX + degrees < -90) {
            degrees = -90 - rotationX;
            rotationX = -90;
        } else {
            rotationX += degrees;
        }
        rotX = rotX.rotatedX(degrees);
        cameraToWorld = rotX.multiply(rotY).translated(pos);
    }

    public void rotateY(double degrees) {
        rotY = rotY.rotatedY(degrees);
        cameraToWorld = rotX.multiply(rotY).translated(pos);
    }

    public void rotate(double degreesX, double degreesY) {
        if (rotationX + degreesX > 90) {
            degreesX = 90 - rotationX;
            rotationX = 90;
        } else if (rotationX + degreesX < -90) {
            degreesX = -90 - rotationX;
            rotationX = -90;
        } else {
            rotationX += degreesX;
        }
        rotX = rotX.rotatedX(degreesX);
        rotY = rotY.rotatedY(degreesY);
        cameraToWorld = rotX.multiply(rotY).translated(pos);
    }

    @Override
    public String toString() {
        Vec3D dir = cameraToWorld.multiplyDirection(new Vec3D(0, 0, -1));
        return "cam{pos = " + pos + " dir = " + dir + "}";
    }
}
