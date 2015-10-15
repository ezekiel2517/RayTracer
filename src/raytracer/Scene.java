package raytracer;

import math.Vec3D;

public class Scene {
    public Object[] objects;
    public Light[] lights;
    public Camera camera;
    public Vec3D backgroundColor = new Vec3D(0.03, 0.07, 0.16);
    public double ambientLight = 0.0;
    public double bias = 1e-9;
    public int maxDepth = 10;

    public Scene(Object[] objects, Light[] lights, Camera camera) {
        this.objects = objects;
        this.lights = lights;
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
                pixels[j][i] = castRay(new Ray(origin, direction), 0);
            }
        }
        return pixels;
    }

    private Vec3D castRay(Ray ray, int depth) {
        if (depth > maxDepth) return backgroundColor;
        Intersection isect = new Intersection();
        trace(ray, isect);
        if (isect.hitObject != null) {
            Vec3D hitPoint = ray.origin.add(ray.direction.multiply(isect.tNear));
            Vec3D hitNormal = isect.hitObject.getSurfaceProperties(hitPoint);
            Vec3D hitColor = new Vec3D();
            switch (isect.hitObject.materialType) {
                case PHONG:
                    Vec3D diffuse = new Vec3D(), specular = new Vec3D();
                    for (Light light : lights) {
                        Illumination illumination = light.illuminate(hitPoint);
                        Ray shadowRay = new Ray(hitPoint.add(hitNormal.multiply(bias)), illumination.lightDirection.multiply(-1));
                        Intersection shadowIsect = new Intersection();
                        shadowIsect.tNear = illumination.distance;
                        trace(shadowRay, shadowIsect);
                        boolean visible = shadowIsect.hitObject == null;
                        if (visible) {
                            diffuse = diffuse.add(illumination.lightIntensity.multiply(
                                    Math.max(0, hitNormal.dotProduct(illumination.lightDirection.multiply(-1))))
                                    .multiply(isect.hitObject.albedo));
                            Vec3D r = reflect(illumination.lightDirection, hitNormal);
                            specular = specular.add(illumination.lightIntensity.multiply(
                                    Math.pow(Math.max(0, r.dotProduct(ray.direction.multiply(-1))), isect.hitObject.n)));
                        }
                    }
                    hitColor = diffuse.multiply(isect.hitObject.kd).add(specular.multiply(isect.hitObject.ks));
                    break;
                case REFLECTIVE:
                    boolean outside = ray.direction.dotProduct(hitNormal) < 0;
                    Vec3D bias = hitNormal.multiply(this.bias);
                    Vec3D r = reflect(ray.direction, hitNormal).normalize();
                    Vec3D orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    hitColor = castRay(new Ray(orig, r), depth + 1).multiply(0.75);
                    break;
            }
            //hitColor = hitColor.add(isect.hitObject.albedo.multiply(ambientLight));
            if (hitColor.getX() > 1) hitColor.setX(1);
            if (hitColor.getY() > 1) hitColor.setY(1);
            if (hitColor.getZ() > 1) hitColor.setZ(1);
            return hitColor;
        } else return backgroundColor;
    }

    private void trace(Ray ray, Intersection isect) {
        for (Object object : objects) {
            Double tNear = object.intersect(ray);
            if (tNear != null && tNear < isect.tNear) {
                isect.tNear = tNear;
                isect.hitObject = object;
            }
        }
    }

    private Vec3D reflect(Vec3D incident, Vec3D normal) {
        return incident.subtract(normal.multiply(2 * incident.dotProduct(normal)));
    }
}
