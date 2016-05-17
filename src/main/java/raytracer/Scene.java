package raytracer;

//import com.google.gson.Gson;
//import com.google.gson.annotations.Expose;
import math.Utils;
import math.Vec2D;
import math.Vec3D;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public class Scene {
    //@Expose
    public Object[] objects;
    //@Expose
    public Light[] lights;
    //@Expose
    public Camera camera;
    //@Expose
    public Vec3D backgroundColor = new Vec3D(0, 0.1, 0.2); //new Vec3D(0.03, 0.07, 0.16);
    //@Expose
    public double ambientLight = 0;
    //@Expose
    public Vec3D fogColor = new Vec3D(0.5, 0.5, 0.5);
    //@Expose
    public double fogDist = 1024;
    //@Expose
    public double bias = 1e-4;
    //@Expose
    public int maxDepth = 4;
    //@Expose
    public int aa = 4;
    protected Stats stats = new Stats();
    protected int nTris;
    protected
    Random generator = new Random();
    //@Expose
    protected boolean useGI = false;
    int N = 4;
    public boolean renderAtmosphere = false;
    boolean renderFog = false;
    public boolean useEnvLight = false;
    Vec3D envLightColor = new Vec3D(1, 1, 1);
    double envLightIntensity = 1;
    public int samplesCounter;

    public Scene(Object[] objects, Light[] lights, Camera camera) {
        this.objects = objects;
        this.lights = lights;
        this.camera = camera;
        for (Object obj : objects) {
            if (obj instanceof TriangleMesh) {
                nTris += ((TriangleMesh) obj).getNTris();
            }
        }
    }

//    protected static void writeScene(Scene scene, String path) {
//        Gson gson = new Gson();
//        String json = gson.toJson(scene);
//        try (FileWriter writer = new FileWriter(path)) {
//            writer.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected static Scene readScene(String path) {
//        Gson gson = new Gson();
//        return null;
//    }

    public void readWriteScene() {
        //Scene.writeScene(scene, "/home/piotr/scene.json");

//        GsonBuilder gb = new GsonBuilder();
//
//        gb.registerTypeAdapter(Object.class, (JsonSerializer<Object>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("sphere")){
//                return gson.toJsonTree(src, Sphere.class);
//            }
//            if(objectType.equals("plane")){
//                return gson.toJsonTree(src, Plane.class);
//            }
//            if(objectType.equals("triangle")){
//                return gson.toJsonTree(src, Triangle.class);
//            }
//            if(objectType.equals("triangleMesh")){
//                return gson.toJsonTree(src, TriangleMesh.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Texture.class, (JsonSerializer<Texture>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("checkerboard")){
//                return gson.toJsonTree(src, Checkerboard.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Light.class, (JsonSerializer<Light>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("distantLight")){
//                return gson.toJsonTree(src, DistantLight.class);
//            }
//            if(objectType.equals("pointLight")){
//                return gson.toJsonTree(src, PointLight.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Object.class, (JsonDeserializer<Object>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("sphere")){
//                //return gson.fromJson(jsonElement, Sphere.class);
//                //return new Sphere((Matrix44D)data.get("objectToWorld"), (double)data.get("radius"), (Vec3D)data.get("albedo"), (Object.MaterialType) data.get("materialType"));
//                Sphere sphere = gson.fromJson(jsonElement, Sphere.class);
//                sphere.radius2 = sphere.radius * sphere.radius;
//                sphere.center = sphere.objectToWorld.multiplyPoint(new Vec3D());
//                sphere.worldToObject = sphere.objectToWorld.inverse();
//                return sphere;
//            }
//            if(objectType.equals("plane")){
//                return gson.fromJson(jsonElement, Plane.class);
//            }
//            if(objectType.equals("triangle")){
//                return gson.fromJson(jsonElement, Triangle.class);
//            }
//            if(objectType.equals("triangleMesh")){
//                return gson.fromJson(jsonElement, TriangleMesh.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Light.class, (JsonDeserializer<Light>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("distantLight")){
//                return gson.fromJson(jsonElement, DistantLight.class);
//            }
//            if(objectType.equals("pointLight")){
//                return gson.fromJson(jsonElement, PointLight.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Texture.class, (JsonDeserializer<Texture>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("checkerboard")){
//                return gson.fromJson(jsonElement, Checkerboard.class);
//            }
//            return null;
//        });
//
//        Sphere sphere = new Sphere(new Matrix44D(), 1, new Vec3D(0.18, 0.18, 0.18), Object.MaterialType.PHONG);
//        //sphere.kd = 1;
//        //sphere.ks = 0;
//        sphere.texture = new Checkerboard(0, new Vec2D(1, 1), new Vec3D(1, 0, 1), new Vec3D(0, 1, 1));
//        Camera cam = new Camera();
//        cam.translate(new Vec3D(0, 0, 1), 4);
//        Scene scene1 = new Scene(new Object[] {sphere}, new Light[] {}, cam);
//        String json = gb.setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(scene1);
//        try (FileWriter writer = new FileWriter("/home/piotr/test.json")) {
//            writer.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Scene scene2 = null;
//        try (BufferedReader br = new BufferedReader(new FileReader("/home/piotr/test.json"))) {
//            scene2 = gb.create().fromJson(br, Scene.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assert scene2 != null;
//        this.scene = new ScalaScene(scene2.objects, scene2.lights, scene2.camera);
//        camera = scene2.camera;
        //System.out.println(camera.cameraToWorld);
        //System.out.println(((Sphere) this.scene.objects[0]).albedo);

//        String js = gb.setPrettyPrinting().create().toJson(new Scene(this.scene.objects, this.scene.lights, this.scene.camera));
//        try (FileWriter writer = new FileWriter("/home/piotr/test.json")) {
//            writer.write(js);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    protected void createCoordinateSystem(Vec3D n, Vec3D nt, Vec3D nb) {
        if (Math.abs(n.getX()) > Math.abs(n.getY())) {
            double denom = Math.sqrt(n.getX() * n.getX() + n.getZ() * n.getZ());
            nt.setX(n.getZ() / denom);
            nt.setY(0);
            nt.setZ(-n.getX() / denom);
        }
        else {
            double denom = Math.sqrt(n.getY() * n.getY() + n.getZ() * n.getZ());
            nt.setX(0);
            nt.setY(-n.getZ() / denom);
            nt.setZ(n.getY() / denom);
        }
        Vec3D tmp = n.crossProduct(nt);
        nb.setX(tmp.getX());
        nb.setY(tmp.getY());
        nb.setZ(tmp.getZ());
    }

    protected Vec3D uniformSampleHemisphere(double r1, double r2) {
        // cos(theta) = u1 = y
        // cos^2(theta) + sin^2(theta) = 1 -> sin(theta) = srtf(1 - cos^2(theta))
        double sinTheta = Math.sqrt(1 - r1 * r1);
        double phi = 2 * Math.PI * r2;
        double x = sinTheta * Math.cos(phi);
        double z = sinTheta * Math.sin(phi);
        return new Vec3D(x, r1, z);
    }

    public void render(int width, int height, int row, Vec3D[] pixels) {
    }

    public void render(int width, int height, Vec3D[] pixels){
    }
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
                    Panel.print(a + "%");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b = 10 * c++;
            }
        }
        return pixels;
    }*/

    protected Vec3D castRay(Ray ray, int depth, Vec2D length) {
        if (depth > maxDepth) return new Vec3D();
        Intersection isect = new Intersection();
        trace(ray, isect);
        if (isect.hitObject != null) {
            length.x = isect.tNear;
            if (length.y == 1) return new Vec3D();
            Vec3D hitPoint = ray.origin.add(ray.direction.multiply(isect.tNear));
            SurfaceProperties props = isect.hitObject.getSurfaceProperties(hitPoint);
            Vec3D hitNormal = props.hitNormal;
            //System.out.println(hitNormal);
            Vec3D hitColor = new Vec3D();
            switch (isect.hitObject.materialType) {
                case PHONG:
                    // DIRECT LIGHTING

                    Vec3D diffuse = new Vec3D(), specular = new Vec3D();

                    Vec3D col;
                    if (isect.hitObject.texture != null) {
                        col = isect.hitObject.texture.getColor(props.hitTextureCoordinates);
                    } else {
                        col = isect.hitObject.albedo;
                    }

                    for (Light light : lights) {
                        Illumination illumination = light.illuminate(hitPoint);
                        Ray shadowRay = new Ray(hitPoint.add(hitNormal.multiply(bias)),
                                illumination.lightDirection.multiply(-1), Ray.RayType.SHADOW_RAY);
                        stats.addShadowRay();
                        Intersection shadowIsect = new Intersection();
                        shadowIsect.tNear = illumination.distance;
                        trace(shadowRay, shadowIsect);
                        boolean visible = shadowIsect.hitObject == null;
                        //System.out.print(visible);

                        if (visible) {
                            diffuse = diffuse.add(illumination.lightIntensity.multiply(
                                    Math.max(0, hitNormal.dotProduct(illumination.lightDirection.multiply(-1)))));
                            Vec3D r = reflect(illumination.lightDirection, hitNormal);
                            specular = specular.add(illumination.lightIntensity.multiply(
                                    Math.pow(Math.max(0, r.dotProduct(ray.direction.multiply(-1))), isect.hitObject.n)));
                        }
                    }

                    // INDIRECT LIGHTING

                    Vec3D indirectLighting = new Vec3D();

                    if (useGI || useEnvLight) {
                        //int N = 16;// / (depth + 1);
                        Vec3D Nt = new Vec3D(), Nb = new Vec3D();
                        createCoordinateSystem(hitNormal, Nt, Nb);
                        double pdf = 1 / (2 * Math.PI);

                        int nsam = Options.renderProgressively ? 1 : N;

                        for (int n = 0; n < nsam; ++n) {
                            double r1 = generator.nextDouble();
                            double r2 = generator.nextDouble();
                            Vec3D sample = uniformSampleHemisphere(r1, r2);
                            Vec3D sampleWorld = new Vec3D(
                                    sample.getX() * Nb.getX() + sample.getY() * hitNormal.getX() + sample.getZ() * Nt.getX(),
                                    sample.getX() * Nb.getY() + sample.getY() * hitNormal.getY() + sample.getZ() * Nt.getY(),
                                    sample.getX() * Nb.getZ() + sample.getY() * hitNormal.getZ() + sample.getZ() * Nt.getZ());
                            // don't forget to divide by PDF and multiply by cos(theta)
                            int dep = useGI ? depth + 1 : maxDepth;
                            double useMonteCarlo = useGI ? 0 : 1;
                            Vec3D incomingLight = castRay(new Ray(hitPoint.add(sampleWorld.multiply(bias)), sampleWorld, Ray.RayType.OTHER), dep, new Vec2D(0, useMonteCarlo));
                            indirectLighting = indirectLighting.add(incomingLight.multiply(r1)); // tu byl blad!!!
                        }

                        // divide by N
                        indirectLighting = indirectLighting.multiply(1 / (double) nsam);
                    }



                    // correct for specular
//                    diffuse = depth != 0 ?
//                    diffuse.multiply(1 / Math.PI).add(indirectLighting.multiply(2)).multiply(col) :
//                    indirectLighting.multiply(2).multiply(col);
                    diffuse = diffuse.multiply(1 / Math.PI).add(indirectLighting.multiply(2)).multiply(col);


                    hitColor = diffuse.multiply(isect.hitObject.kd).add(specular.multiply(isect.hitObject.ks));

                    // ambient light
                    Vec3D colo;
                    if (isect.hitObject.texture != null) {
                        colo = isect.hitObject.texture.getColor(props.hitTextureCoordinates);
                    } else {
                        colo = isect.hitObject.albedo;
                    }
                    hitColor = hitColor.add(colo.multiply(ambientLight));

                    // fog
                    if (renderFog) {
                        double z = -camera.cameraToWorld.inverse().multiplyPoint(hitPoint).getZ();
                        if (z >= fogDist) {
                            hitColor = fogColor;
                        } else {
                            double factor = z / fogDist;
                            hitColor = hitColor.multiply(1 - factor).add(fogColor.multiply(factor));
                        }
                    }
                    break;
                case REFLECTIVE:
                    boolean outside = ray.direction.dotProduct(hitNormal) < 0;
                    Vec3D bias = hitNormal.multiply(this.bias);
                    Vec3D r = reflect(ray.direction, hitNormal).normalize();
                    Vec3D orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    hitColor = castRay(new Ray(orig, r, Ray.RayType.OTHER), depth + 1, new Vec2D());//.multiply(0.75);
                    stats.addReflectionRay();
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
                        Vec2D len = new Vec2D();
                        refractionColor = castRay(new Ray(refractionRayOrig, refractionDirection, Ray.RayType.PRIMARY_RAY), depth + 1, len);
                        stats.addTransmissionRay();

//                        if (outside) {
//                            System.out.println(len.x);
//                            len.x *= 0.5;
//                            refractionColor.setX(refractionColor.getX() * Math.exp(-isect.hitObject.albedo.getX() * len.x));
//                            refractionColor.setY(refractionColor.getY() * Math.exp(-isect.hitObject.albedo.getY() * len.x));
//                            refractionColor.setZ(refractionColor.getZ() * Math.exp(-isect.hitObject.albedo.getZ() * len.x));
//                        }
                    }
                    r = reflect(ray.direction, hitNormal).normalize();
                    orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    reflectionColor = castRay(new Ray(orig, r, Ray.RayType.PRIMARY_RAY), depth + 1, new Vec2D());
                    stats.addReflectionRay();
                    //hitColor = hitColor.add(reflectionColor.multiply(kr).add(refractionColor.multiply(1 - kr)));
                    hitColor = hitColor.add(reflectionColor.multiply(kr*1).add(refractionColor.multiply(1 - kr).multiply(1)).multiply(new Vec3D(1, 1, 1)));
                    double dupa = 0.05;
                    //hitColor = hitColor.add(reflectionColor.multiply(dupa).add(refractionColor.multiply(1 - dupa)));

                    //////////////////////
//                    Vec3D spec = new Vec3D();
//                    for (Light light : lights) {
//                        Illumination illumination = light.illuminate(hitPoint);
//                        Ray shadowRay = new Ray(hitPoint.add(hitNormal.multiply(bias)),
//                                illumination.lightDirection.multiply(-1), Ray.RayType.SHADOW_RAY);
//                        stats.addShadowRay();
//                        Intersection shadowIsect = new Intersection();
//                        shadowIsect.tNear = illumination.distance;
//                        trace(shadowRay, shadowIsect);
//                        boolean visible = shadowIsect.hitObject == null;
//
//                        if (visible) {
//                            Vec3D ref = reflect(illumination.lightDirection, hitNormal);
//                            spec = spec.add(illumination.lightIntensity.multiply(
//                                    Math.pow(Math.max(0, ref.dotProduct(ray.direction.multiply(-1))), isect.hitObject.n)));
//                        }
//                    }
//                    hitColor = hitColor./*multiply(col).*/multiply(isect.hitObject.kd).add(spec.multiply(isect.hitObject.ks));
                    //////////////////////

                    break;
                case CONDUCTOR:
                    Vec3D reflCol;
                    double Kr = fresnelConductor(ray.direction, hitNormal, isect.hitObject.ior, isect.hitObject.ior2);
                    //System.out.println(Kr);
                    outside = ray.direction.dotProduct(hitNormal) < 0;
                    bias = hitNormal.multiply(this.bias);
                    r = reflect(ray.direction, hitNormal).normalize();
                    orig = outside ? hitPoint.add(bias) : hitPoint.subtract(bias);
                    reflCol = castRay(new Ray(orig, r, Ray.RayType.PRIMARY_RAY), depth + 1, new Vec2D());
                    stats.addReflectionRay();
                    hitColor = reflCol.multiply(1/Kr).multiply(isect.hitObject.albedo);
                    break;
            }

            if (hitColor.getX() > 1) hitColor.setX(1);
            if (hitColor.getY() > 1) hitColor.setY(1);
            if (hitColor.getZ() > 1) hitColor.setZ(1);
            if (hitColor.getX() < 0) hitColor.setX(0);
            if (hitColor.getY() < 0) hitColor.setY(0);
            if (hitColor.getZ() < 0) hitColor.setZ(0);
            return hitColor;
        } else {
            if (useEnvLight) return envLightColor.multiply(envLightIntensity);
            if (useGI) return new Vec3D();
            return backgroundColor;
        }
    }

    protected void trace(Ray ray, Intersection isect) {
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
        Vec3D n = new Vec3D(normal);
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
        return k < 0 ? new Vec3D() : incident.multiply(eta).add(n.multiply(eta * cosi - Math.sqrt(k)));

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

    private double fresnelConductor(Vec3D incident, Vec3D normal, double ior, double ior2) {
        double cosi = Utils.clamp(-1, 1, incident.dotProduct(normal));
        return ((ior - 1) * (ior - 1) + 4 * ior * Math.pow(1 - cosi, 5) + ior2 * ior2) / ((ior + 1) * (ior + 1) + ior2 * ior2);
    }
}
