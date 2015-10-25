package raytracer;

import math.Utils;
import math.Vec3D;

import java.io.IOException;

public abstract  class Scene {
    public Object[] objects;
    public Light[] lights;
    public Camera camera;
    public Vec3D backgroundColor = new Vec3D(0.03, 0.07, 0.16);
    public double ambientLight = 0.0;
    public double bias = 1e-9;
    public int maxDepth = 10;
    public int aa = 1;

    public Scene(Object[] objects, Light[] lights, Camera camera) {
        this.objects = objects;
        this.lights = lights;
        this.camera = camera;
    }

    public abstract Vec3D[][] render(int width, int height);
    /*public Vec3D[][] render(int width, int height) {
        Vec3D[][] pixels = new Vec3D[height][width];
        double scale = Math.tan(Math.toRadians(camera.fov * 0.5));
        double imageAspectRatio = width / (double) height;
        Vec3D origin = camera.cameraToWorld.multiplyPoint(new Vec3D());
        double b = 1;
        double c = 1;
        double factor = 1.0 / (aa * aa);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                pixels[j][i] = new Vec3D();
                for (int k = 0; k < aa; k++) {
                    for (int l = 0; l < aa; l++) {
                        double x = (2 * (i + (double) l / aa + 1 / aa * 0.5) / width - 1) * imageAspectRatio * scale;
                        double y = (1 - 2 * (j + (double) k / aa + 1 / aa * 0.5) / height) * scale;
                        Vec3D direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1));
                        pixels[j][i] = pixels[j][i].add(castRay(new Ray(origin, direction, Ray.RayType.PRIMARY_RAY), 0).multiply(factor));
                    }
                }
            }
            double a = (j + 1) / (double) height * 100;
            if (a >= b) {
                //System.out.format("%.1f%%%n", a);
                try {
                    Main.print(a + "%");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b = 10 * c++;
            }
        }
        return pixels;
    }*/

    protected Vec3D castRay(Ray ray, int depth) {
        if (depth > maxDepth) return backgroundColor;
        Intersection isect = new Intersection();
        trace(ray, isect);
        if (isect.hitObject != null) {
            Vec3D hitPoint = ray.origin.add(ray.direction.multiply(isect.tNear));
            SurfaceProperties props = isect.hitObject.getSurfaceProperties(hitPoint);
            Vec3D hitNormal = props.hitNormal;
            Vec3D hitColor = new Vec3D();
            switch (isect.hitObject.materialType) {
                case PHONG:
                    Vec3D diffuse = new Vec3D(), specular = new Vec3D();
                    for (Light light : lights) {
                        Illumination illumination = light.illuminate(hitPoint);
                        Ray shadowRay = new Ray(hitPoint.add(hitNormal.multiply(bias)),
                                illumination.lightDirection.multiply(-1), Ray.RayType.PRIMARY_RAY);
                        Intersection shadowIsect = new Intersection();
                        shadowIsect.tNear = illumination.distance;
                        trace(shadowRay, shadowIsect);
                        boolean visible = shadowIsect.hitObject == null;
                        Vec3D col;
                        if (isect.hitObject.texture != null) {
                            col = isect.hitObject.texture.getPattern(props.hitTextureCoordinates);
                        } else {
                            col = isect.hitObject.albedo;
                        }
                        if (visible) {
                            diffuse = diffuse.add(illumination.lightIntensity.multiply(
                                    Math.max(0, hitNormal.dotProduct(illumination.lightDirection.multiply(-1))))
                                    .multiply(col));
                            Vec3D r = reflect(illumination.lightDirection, hitNormal);
                            specular = specular.add(illumination.lightIntensity.multiply(
                                    Math.pow(Math.max(0, r.dotProduct(ray.direction.multiply(-1))), isect.hitObject.n)));
                        }
                    }
                    hitColor = diffuse.multiply(isect.hitObject.kd).add(new Vec3D(0, 0.5, 1).multiply(hitPoint.getZ() * 0)).add(specular.multiply(isect.hitObject.ks));
                    break;
                case REFLECTIVE:
                    boolean outside = ray.direction.dotProduct(hitNormal) < 0;
                    Vec3D bias = hitNormal.multiply(this.bias);
                    Vec3D r = reflect(ray.direction, hitNormal).normalize();
                    Vec3D orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    hitColor = castRay(new Ray(orig, r, Ray.RayType.PRIMARY_RAY), depth + 1).multiply(0.75);
                    break;
                case REFLECTIVE_AND_REFRACTIVE:
                    Vec3D refractionColor = new Vec3D();
                    Vec3D reflectionColor = new Vec3D();
                    double kr = fresnel(ray.direction, hitNormal, isect.hitObject.ior);
                    outside = ray.direction.dotProduct(hitNormal) < 0;
                    bias = hitNormal.multiply(this.bias);
                    if (kr < 1) {
                        Vec3D refractionDirection = refract(ray.direction, hitNormal, isect.hitObject.ior).normalize();
                        Vec3D refractionRayOrig = outside ? hitPoint.subtract(bias) : hitPoint.add(bias);
                        refractionColor = castRay(new Ray(refractionRayOrig, refractionDirection, Ray.RayType.PRIMARY_RAY), depth + 1);
                    }
                    r = reflect(ray.direction, hitNormal).normalize();
                    orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    reflectionColor = castRay(new Ray(orig, r, Ray.RayType.PRIMARY_RAY), depth + 1);
                    hitColor = hitColor.add(reflectionColor.multiply(kr).add(refractionColor.multiply(1 - kr)));
                    break;
            }
            //hitColor = hitColor.add(isect.hitObject.albedo.multiply(ambientLight));
            if (hitColor.getX() > 1) hitColor.setX(1);
            if (hitColor.getY() > 1) hitColor.setY(1);
            if (hitColor.getZ() > 1) hitColor.setZ(1);
            if (hitColor.getX() < 0) hitColor.setX(0);
            if (hitColor.getY() < 0) hitColor.setY(0);
            if (hitColor.getZ() < 0) hitColor.setZ(0);
            return hitColor;
        } else return backgroundColor;
    }

    private void trace(Ray ray, Intersection isect) {
        for (Object object : objects) {
            Double tNear = object.intersect(ray);
            if (tNear != null && tNear < isect.tNear) {
                if (ray.type == Ray.RayType.SHADOW_RAY && object.materialType == Object.MaterialType.REFLECTIVE_AND_REFRACTIVE)
                    continue;
                isect.tNear = tNear;
                isect.hitObject = object;
            }
        }
    }

    private Vec3D reflect(Vec3D incident, Vec3D normal) {
        return incident.subtract(normal.multiply(2 * incident.dotProduct(normal)));
    }

    private Vec3D refract(Vec3D incident, Vec3D normal, double ior) {
        double cosi = Utils.clamp(-1, 1, incident.dotProduct(normal));
        double etai = 1, etat = ior;
        Vec3D n = normal;
        if (cosi < 0)
            cosi = -cosi;
        else {
            double tmp = etai;
            etai = etat;
            etat = tmp;
            n = n.multiply(-1);
        }
        double eta = etai / etat;
        double k = 1 - eta * eta * (1 - cosi * cosi);
        return k < 0 ? new Vec3D() : incident.multiply(eta).add(n.multiply(eta - cosi * Math.sqrt(k)));

    }

    private double fresnel(Vec3D incident, Vec3D normal, double ior) {
        double kr;
        double cosi = Utils.clamp(-1, 1, incident.dotProduct(normal));
        double etai = 1, etat = ior;
        if (cosi > 0) {
            double tmp = etai;
            etai = etat;
            etat = tmp;
        }
        double sint = etai / etat * Math.sqrt(Math.max(0, 1 - cosi * cosi));
        if (sint >= 1)
            kr = 1;
        else {
            double cost = Math.sqrt(Math.max(0, 1 - sint * sint));
            cosi = Math.abs(cosi);
            double Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
            double Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
            kr = (Rs * Rs + Rp * Rp) / 2;
        }
        return kr;
    }
}
