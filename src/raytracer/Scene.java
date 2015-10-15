package raytracer;

import math.Vec3D;

public class Scene {
    public Object object;
    public Light light;
    public Camera camera;
    public Vec3D backgroundColor = new Vec3D(0.03, 0.07, 0.16);
    public double ambientLight = 0.05;

    public Scene(Object object, Light light, Camera camera) {
        this.object = object;
        this.light = light;
        this.camera = camera;
    }

    public Vec3D[][] render(int width, int height) {
        Vec3D[][] pixels = new Vec3D[height][width];
        double scale = Math.tan(Math.toRadians(camera.fov * 0.5));
        double imageAspectRatio = width / (double) height;
        Vec3D origin = camera.cameraToWorld.multiplyPoint(new Vec3D());
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double x = (2 * (i + 0.5) / width - 1) * imageAspectRatio * scale;
                double y = (1 - 2 * (j + 0.5) / height) * scale;
                Vec3D direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1));
                pixels[j][i] = castRay(new Ray(origin, direction));
            }
        }
        return pixels;
    }

    private Vec3D castRay(Ray ray) {
        Double tNear = trace(ray);
        if (tNear != null) {
            Vec3D hitPoint = ray.origin.add(ray.direction.multiply(tNear));
            Vec3D hitNormal = object.getSurfaceProperties(hitPoint);
            Illumination illumination = light.illuminate(hitPoint);
            Vec3D hitColor =  illumination.lightIntensity.multiply(
                    Math.max(0, hitNormal.dotProduct(illumination.lightDirection.multiply(-1)))).multiply(object.albedo);
            hitColor = hitColor.add(object.albedo.multiply(ambientLight));
            if (hitColor.getX() > 1) hitColor.setX(1);
            if (hitColor.getY() > 1) hitColor.setY(1);
            if (hitColor.getZ() > 1) hitColor.setZ(1);
            return hitColor;
        } else return backgroundColor;
    }

    private Double trace(Ray ray) {
        return object.intersect(ray);
    }
}
