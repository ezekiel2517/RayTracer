package raytracer;

import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 15.11.15.
 */
public class Scenes {

    static double earthRadius = 6360e3;

    public static ScalaScene stones(Camera camera) {
        Matrix44D sphereo2w = new Matrix44D();
        sphereo2w.set(3, 2, -5);
        sphereo2w.set(3, 1, -0.5 + earthRadius);
        Sphere sphere = new Sphere(sphereo2w, 0.5, new Vec3D(1, 1, 0), Object.MaterialType.PHONG);
        sphere.ior = 0.37;
        sphere.ior2 = 2.82;
        sphere.texture = Textures.gradient(0, new Vec2D(10, 10));
        Matrix44D plane02w = new Matrix44D();
        plane02w.set(3, 1, -1 + earthRadius);
        Plane plane = new Plane(plane02w, new Vec3D(0.25, 1, 0.25), Object.MaterialType.PHONG);

        Matrix44D box2w = new Matrix44D();
        box2w = box2w.rotatedY(30);
        box2w.set(3, 0, 2);
        box2w.set(3, 1, 0.5 + earthRadius);
        box2w.set(3, 2, -7);
        TriangleMesh box = TriangleMesh.createCuboid(1, 4, 1, box2w, true);
        box.materialType = Object.MaterialType.PHONG;
        box.albedo = new Vec3D(1, 0.5, 0);
        box.smoothShading = false;

        Matrix44D box22w = new Matrix44D();
        box22w = box22w.rotatedY(5);
        box22w.set(3, 0, -1.5);
        box22w.set(3, 1, 0.5 + earthRadius);
        box22w.set(3, 2, -7.5);
        TriangleMesh box2 = TriangleMesh.createCuboid(1, 4, 1, box22w, true);
        box2.materialType = Object.MaterialType.PHONG;
        box2.albedo = new Vec3D(1, 0.5, 0);
        box2.smoothShading = false;

        Matrix44D box32w = new Matrix44D();
        box32w = box32w.rotatedZ(90);
        box32w = box32w.rotatedY(-10);
        box32w.set(3, 0, 0.5);
        box32w.set(3, 1, 3 + earthRadius);
        box32w.set(3, 2, -7);
        TriangleMesh box3 = TriangleMesh.createCuboid(1, 6, 1, box32w, true);
        box3.materialType = Object.MaterialType.PHONG;
        box3.albedo = new Vec3D(1, 0.5, 0);
        box3.ior = 1.5;
        box3.smoothShading = false;

        Matrix44D lighto2w = new Matrix44D();
        lighto2w = lighto2w.rotatedX(-20).rotatedY(-60);
        DistantLight light = new DistantLight(lighto2w, new Vec3D(1, 1, 1), Math.PI);

        PointLight pointLight = new PointLight(new Matrix44D().translated(new Vec3D(0, 1, -8)), new Vec3D(1, 1, 0), 50);

        camera.translate(new Vec3D(0, -1, 0), 0.5 - earthRadius);
        camera.translate(new Vec3D(0, 0, -1), 2);
        camera.rotateX(10);
        camera.rotateY(-5);
        camera.fov = 90;
        return new ScalaScene(new Object[] {sphere, plane, box, box2, box3}, new Light[] {light}, camera);
    }

    public static ScalaScene reflections(Camera camera) {
        Matrix44D sphere1o2w = new Matrix44D();
        sphere1o2w.set(3, 0, 1.6);
        sphere1o2w.set(3, 1, -2);
        sphere1o2w.set(3, 2, 0);
        Sphere sphere1 = new Sphere(sphere1o2w, 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        Matrix44D sphere2o2w = new Matrix44D();
        sphere2o2w.set(3, 0, 2.5);
        sphere2o2w.set(3, 1, -1.5);
        sphere2o2w.set(3, 2, -2);
        Sphere sphere2 = new Sphere(sphere2o2w, 1.5, new Vec3D(1, 0, 1), Object.MaterialType.REFLECTIVE_AND_REFRACTIVE);
        sphere2.kd = 1;
        sphere2.ks = 0;
        sphere2.ior = 1.5;
        sphere2.ior2 = 2.82;
        Matrix44D r2w = new Matrix44D();
        r2w = r2w.rotatedY(90);
        TriangleMesh room = TriangleMesh.createCuboid(16, 6, 16, r2w, false);
        room.materialType = Object.MaterialType.PHONG;
        room.texture = Textures.checkerboard(0, new Vec2D(4, 2), new Vec3D(), new Vec3D(1, 1, 1));
        room.albedo = new Vec3D(0.75, 0.25, 0.25);
        room.smoothShading = false;
        room.kd = 1;
        room.ks = 0;
        Matrix44D box2w = new Matrix44D();
        box2w = box2w.rotatedY(40);
        box2w.set(3, 0, -1.4);
        box2w.set(3, 1, -2);
        box2w.set(3, 2, 1.4);
        TriangleMesh box = TriangleMesh.createCuboid(2, 2, 2, box2w, true);
        box.materialType = Object.MaterialType.PHONG;
        box.ior = 0.37;
        box.ior2 = 2.82;
        box.albedo = new Vec3D(0, 1, 0);
        box.smoothShading = false;

        Matrix44D box22w = new Matrix44D();
        box22w = box22w.rotatedY(-20);
        box22w.set(3, 0, -1.5);
        box22w.set(3, 1, 0);
        box22w.set(3, 2, 1.5);
        TriangleMesh box2 = TriangleMesh.createCuboid(2, 2, 2, box22w, true);
        box2.materialType = Object.MaterialType.PHONG;
        box2.ior = 0.37;
        box2.ior2 = 2.82;
        box2.albedo = new Vec3D(0.75, 0.5, 0.25);
        box2.smoothShading = false;

        Matrix44D l2w1 = new Matrix44D();
        l2w1.set(3, 0, -4);
        l2w1.set(3, 1, 0);
        l2w1.set(3, 2, -4);
        PointLight light1 = new PointLight(l2w1, new Vec3D(0, 0, 1), 200);

        Matrix44D l2w3 = new Matrix44D();
        l2w3.set(3, 0, -4);
        l2w3.set(3, 1, 0);
        l2w3.set(3, 2, 4);
        PointLight light3 = new PointLight(l2w3, new Vec3D(0, 0, 1), 200);

        Matrix44D l2w2 = new Matrix44D();
        l2w2.set(3, 0, 0);
        l2w2.set(3, 1, -2);
        l2w2.set(3, 2, 0);
        PointLight light2 = new PointLight(l2w2, new Vec3D(1, 1, 1), 25);

        camera.translate(new Vec3D(0, -1, 0), 1);
        camera.rotate(-10, 10);
        camera.translate(new Vec3D(0, 0, 1), 7.5);
        return new ScalaScene(new Object[] {sphere1, sphere2, room, box, box2}, new Light[] {light2}, camera);
    }

    public static ScalaScene globalIllum(Camera camera) {
        Matrix44D sphere1o2w = new Matrix44D();
        sphere1o2w.set(3, 1, -2);
        sphere1o2w.set(3, 2, -0.5);
        Sphere sphere1 = new Sphere(sphere1o2w, 1, new Vec3D(0, 1, 1), Object.MaterialType.PHONG);
        sphere1.kd = 1;
        sphere1.ks = 0;
        Matrix44D sphere2o2w = new Matrix44D();
        sphere2o2w.set(3, 0, 2.5);
        sphere2o2w.set(3, 1, -1.5);
        sphere2o2w.set(3, 2, -2);
        Sphere sphere2 = new Sphere(sphere2o2w, 1.5, new Vec3D(1, 1, 1), Object.MaterialType.PHONG);
        sphere2.kd = 1;
        sphere2.ks = 0;
        Matrix44D r2w = new Matrix44D();
        r2w = r2w.rotatedY(90);
        TriangleMesh room = TriangleMesh.createCuboid(16, 6, 16, r2w, false);
        room.materialType = Object.MaterialType.PHONG;
        room.texture = Textures.checkerboard(0, new Vec2D(4, 2), new Vec3D(), new Vec3D(1, 1, 1));
        room.smoothShading = false;
        room.kd = 1;
        room.ks = 0;
        Matrix44D box2w = new Matrix44D();
        box2w = box2w.rotatedY(40);
        box2w.set(3, 0, -1.4);
        box2w.set(3, 1, -2);
        box2w.set(3, 2, 1.4);
        TriangleMesh box = TriangleMesh.createCuboid(2, 2, 2, box2w, true);
        box.kd = 1;
        box.ks = 0;
        box.materialType = Object.MaterialType.PHONG;
        box.albedo = new Vec3D(0, 1, 0);
        box.smoothShading = false;

        Matrix44D box22w = new Matrix44D();
        box22w = box22w.rotatedY(-20);
        box22w.set(3, 0, -1.5);
        box22w.set(3, 1, 0);
        box22w.set(3, 2, 1.5);
        TriangleMesh box2 = TriangleMesh.createCuboid(2, 2, 2, box22w, true);
        box2.kd = 1;
        box2.ks = 0;
        box2.materialType = Object.MaterialType.PHONG;
        box2.albedo = new Vec3D(1, 0, 0);
        box2.smoothShading = false;

        Matrix44D l2w1 = new Matrix44D();
        l2w1.set(3, 0, -4);
        l2w1.set(3, 1, 0);
        l2w1.set(3, 2, -4);
        PointLight light1 = new PointLight(l2w1, new Vec3D(0, 0, 1), 200);

        Matrix44D l2w3 = new Matrix44D();
        l2w3.set(3, 0, -4);
        l2w3.set(3, 1, 0);
        l2w3.set(3, 2, 4);
        PointLight light3 = new PointLight(l2w3, new Vec3D(0, 0, 1), 200);

        Matrix44D l2w2 = new Matrix44D();
        l2w2.set(3, 0, 2);
        l2w2.set(3, 1, 0);
        l2w2.set(3, 2, 2);
        PointLight light2 = new PointLight(l2w2, new Vec3D(1, 1, 1), 100);

        Matrix44D plane2w = new Matrix44D();
        plane2w = plane2w.scaled(new Vec3D(4, 0, 4));
        plane2w.set(3, 0, 0);
        plane2w.set(3, 1, -3);
        plane2w.set(3, 2, 0);
        TriangleMesh plane = TriangleMesh.createPlane(plane2w);
        plane.kd = 1;
        plane.ks = 0;
        plane.materialType = Object.MaterialType.PHONG;
        plane.albedo = new Vec3D(1, 0, 1);
        plane.smoothShading = false;

        camera.translate(new Vec3D(0, -1, 0), 1);
        camera.rotate(-10, 10);
        camera.translate(new Vec3D(0, 0, 1), 7.5);
        return new ScalaScene(new Object[] {sphere1, room, sphere2, box, box2}, new Light[] {light2}, camera);
    }

    public static ScalaScene mirrorFloor(Camera camera) {
        Plane bottomPlane = new Plane(new Matrix44D(), new Vec3D(), Object.MaterialType.PHONG);
        bottomPlane.texture = Textures.checkerboard(20, new Vec2D(1, 1), new Vec3D(), new Vec3D(1, 1, 1));
        Plane topPlane = new Plane(new Matrix44D().translated(new Vec3D(0, 0.01, 0)), new Vec3D(), Object.MaterialType.REFLECTIVE_AND_REFRACTIVE);
        topPlane.ior = 1.3;
        Sphere sphere = new Sphere(new Matrix44D().translated(new Vec3D(0, 1.01, -8)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere.texture = Textures.stripes(45, new Vec2D(10, 10), new Vec3D(0.7, 0.1, 0.9), new Vec3D(0.2, 0.7, 0.1));
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-45).rotatedY(45), new Vec3D(1, 1, 1), 1);

        camera.translate(new Vec3D(0, 1, 0), 1);
        camera.rotateX(-10);
        return new ScalaScene(new Object[] {bottomPlane, topPlane, sphere}, new Light[] {distLight1}, camera);
    }

    public static ScalaScene refractions(Camera camera) {
        Plane plane = new Plane(new Matrix44D().translated(new Vec3D(0, 0, 0)), new Vec3D(1, 1, 1), Object.MaterialType.PHONG);
        plane.texture = Textures.checkerboard(0, new Vec2D(1, 1), new Vec3D(0, 0.5, 0.25), new Vec3D(1, 1, 0));
        Sphere sphere = new Sphere(new Matrix44D().translated(new Vec3D(0, 0 + 1, 0)), 1, new Vec3D(1, 0.77, 0.34), Object.MaterialType.REFLECTIVE);
        Sphere sphere2 = new Sphere(new Matrix44D().translated(new Vec3D(-1.1, 0 + 1, -6)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere2.texture = Textures.checkerboard(0, new Vec2D(10, 10), new Vec3D(0.7, 0.1, 0.9), new Vec3D(0.2, 0.7, 0.1));
        Sphere sphere3 = new Sphere(new Matrix44D().translated(new Vec3D(1.4, 0 + 1, -5)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere3.texture = Textures.gradient(0, new Vec2D(10, 10));
        sphere.ior = 1.5;
        sphere.ior2 = 2.82;
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-90).rotatedY(0), new Vec3D(1, 1, 1), 3);
        camera.translate(new Vec3D(0, 1, 0), 1.25);
        camera.translate(new Vec3D(0, 0, 1), 4);
        camera.rotateX(-5);
        return new ScalaScene(new Object[] {plane, sphere}, new Light[] {distLight1}, camera);
    }

    public static ScalaScene polySphere(Camera camera) {
        TriangleMesh sphere = TriangleMesh.createPolySphere(1, 32);
        sphere.materialType = Object.MaterialType.PHONG;
        sphere.albedo = new Vec3D(0, 1, 0);
        sphere.smoothShading = true;

//        TriangleMesh cylinder = TriangleMesh.createCylinder(1, 0.5, 1, 32, 1);
//        cylinder.materialType = Object.MaterialType.PHONG;
//        cylinder.albedo = new Vec3D(0, 1, 0);
//        cylinder.smoothShading = true;

//        TriangleMesh cone = TriangleMesh.createCone(1, 1, 8, 16);
//        cone.materialType = Object.MaterialType.PHONG;
//        cone.albedo = new Vec3D(0, 1, 0);
//        cone.smoothShading = true;

//        for (int i = 0; i < cylinder.n.length; i++) {
//            System.out.println(cylinder.n[i]);
//        }

        //sphere.boundingVolume = new Sphere(new Matrix44D(), 1, new Vec3D(), Object.MaterialType.PHONG);
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-20).rotatedY(-45), new Vec3D(1, 1, 1), 1);
        DistantLight distLight2 = new DistantLight(new Matrix44D().rotatedX(180).rotatedY(0), new Vec3D(1, 1, 1), 1);
        camera.translate(new Vec3D(0.5, 0.5, 1), 4);
        camera.rotate(-20, 17.5);
        ScalaScene scene = new ScalaScene(new Object[] {sphere}, new Light[] {distLight1}, camera);
        scene.useEnvLight = false;
        return scene;
    }

    public static ScalaScene furnanceTest(Camera camera) {
        Sphere sphere = new Sphere(new Matrix44D(), 1, new Vec3D(0.18, 0.18, 0.18), Object.MaterialType.PHONG);
        sphere.kd = 1;
        sphere.ks = 0;
        camera.translate(new Vec3D(0, 0, 1), 4);
        return new ScalaScene(new Object[] {sphere}, new Light[] {}, camera);
    }

    public static ScalaScene conductors(Camera camera) {
        Sphere sphere = new Sphere(new Matrix44D().translated(new Vec3D(0, 0.5, 0)), 1, new Vec3D(1, 1, 0), Object.MaterialType.PHONG);
        sphere.kd = 1;
        sphere.ks = 0;
        sphere.ior = 1.5;
        sphere.ior2 = 2.82;
        camera.translate(new Vec3D(0, 0, 1), 4);
        DistantLight light = new DistantLight(new Matrix44D().rotatedX(-90), new Vec3D(1, 1, 1), 3);
        Matrix44D m = new Matrix44D().translated(new Vec3D(0, -1, 0)).scaled(new Vec3D(4, 1, 4));
        TriangleMesh floor = TriangleMesh.createPlane(m);
        floor.texture = Textures.checkerboard(0, new Vec2D(2, 2), new Vec3D(0, 0.5, 0.5), new Vec3D(0.5, 1, 0.5));
        floor.materialType = Object.MaterialType.PHONG;
        Sphere sphere1 = new Sphere(new Matrix44D().translated(new Vec3D(-2.5, 0, 0.5)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere1.texture = Textures.stripes(45, new Vec2D(2, 2), new Vec3D(1, 0, 1), new Vec3D(0, 1, 1));
        camera.translate(new Vec3D(0, 1, 0), 1);
        camera.rotate(-20, 10);
        camera.translate(new Vec3D(0, 0, 1), 4);
        return new ScalaScene(new Object[] {sphere, floor, sphere1}, new Light[] {light}, camera);
    }

    public static ScalaScene atmosphere(Camera camera) {
        Sphere sphere = new Sphere(new Matrix44D(), earthRadius, new Vec3D(0, 1, 0.25), Object.MaterialType.PHONG);
        Sphere sphere1 = new Sphere(new Matrix44D().translated(new Vec3D(2, earthRadius, -10)), 8, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere.kd = 1;
        sphere.ks = 0;
        camera.translate(new Vec3D(0, 1, 0), earthRadius + 1);
        //camera.rotateX(90);
        DistantLight light = new DistantLight(new Matrix44D().rotatedX(-90), new Vec3D(1, 1, 1), Math.PI);
        ScalaScene scene = new ScalaScene(new Object[] {sphere, sphere1}, new Light[] {}, camera);
        scene.renderAtmosphere = false;
        scene.backgroundColor = new Vec3D();
        camera.fov = 120;
        return scene;
    }

    public static ScalaScene triangleTest(Camera camera) {
        Matrix44D triangleToWorld = new Matrix44D();
        int[] faceIndex = new int[] {3};
        int[] vertsIndex = new int[] {0, 1, 2};
        Vec3D[] verts = new Vec3D[] {new Vec3D(-1, 1, 0), new Vec3D(-1, -1, 0), new Vec3D(1, -1, 0)};
        Vec3D[] normals = new Vec3D[] {new Vec3D(0, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 1)};
        Vec2D[] st = new Vec2D[] {new Vec2D(0, 0), new Vec2D(0, 1), new Vec2D(1, 1)};
        TriangleMesh triangle = new TriangleMesh(triangleToWorld, faceIndex, vertsIndex, verts, normals, st);
        triangle.materialType = Object.MaterialType.PHONG;
        triangle.albedo = new Vec3D(0, 1, 0);
        triangle.smoothShading = false;

        Matrix44D lightToWorld = new Matrix44D().rotatedX(0);
        DistantLight light = new DistantLight(lightToWorld, new Vec3D(1, 1, 1), 0.5 * Math.PI);

        camera.translate(new Vec3D(0, 0, 1), 2);

        ScalaScene scene = new ScalaScene(new Object[] {triangle}, new Light[] {light}, camera);
        scene.backgroundColor = new Vec3D(0, 0, 0.25);
        scene.useEnvLight = false;
        scene.bias = 1e-12;

        Options.culling = true;

        return scene;
    }

    public static ScalaScene cylinderTest(Camera camera) {
        TriangleMesh cylinder = TriangleMesh.createCylinder(2, 0.5, 3, 4, 2);
        cylinder.materialType = Object.MaterialType.PHONG;
        cylinder.albedo = new Vec3D(0, 1, 0);
        cylinder.smoothShading = true;

        //for (Vec3D v : cylinder.p) System.out.println(v);

        Matrix44D lightToWorld = new Matrix44D().rotatedX(-90);
        DistantLight light = new DistantLight(lightToWorld, new Vec3D(1, 1, 1), 0.5 * Math.PI);

        camera.translate(new Vec3D(0, 0, 1), 5);

        ScalaScene scene = new ScalaScene(new Object[] {cylinder}, new Light[] {light}, camera);
        scene.backgroundColor = new Vec3D(0, 0, 0.25);
        scene.useEnvLight = false;
        //scene.bias = 1e-12;

        Options.culling = true;

        return scene;
    }

    public static ScalaScene idealCylinderTest(Camera camera) {
        Matrix44D cylinderToWorld = new Matrix44D().rotatedZ(90).translated(new Vec3D(1, 0, 0));
        Cylinder cylinder = new Cylinder(cylinderToWorld, 1, new Vec3D(0, 1, 0), 2);

        Matrix44D lightToWorld = new Matrix44D().rotatedX(-45).rotatedY(-45);
        DistantLight light = new DistantLight(lightToWorld, new Vec3D(1, 1, 1), 0.5 * Math.PI);

        camera.translate(new Vec3D(0, 0, 1), 5);
        camera.rotateY(0);

        ScalaScene scene = new ScalaScene(new Object[] {cylinder}, new Light[] {light}, camera);

        return scene;
    }

    public static ScalaScene idealConeTest(Camera camera) {
        Matrix44D coneToWorld = new Matrix44D().rotatedZ(90).translated(new Vec3D(-2, 0, 0));
        Cone cone = new Cone(coneToWorld, 0.5, 1, 0.5, new Vec3D(0, 1, 0));

        Matrix44D lightToWorld = new Matrix44D().rotatedX(-45).rotatedY(-45);
        DistantLight light = new DistantLight(lightToWorld, new Vec3D(1, 1, 1), 0.5 * Math.PI);

        camera.translate(new Vec3D(0, 0, 1), 4);
        //camera.rotateX(90);

        ScalaScene scene = new ScalaScene(new Object[] {cone}, new Light[] {light}, camera);

        return scene;
    }

    public static ScalaScene diskTest(Camera camera) {
        Matrix44D diskToWorld = new Matrix44D();//.rotatedZ(90).translated(new Vec3D(-2, 0, 0));
        Disk disk = new Disk(diskToWorld, new Vec3D(0, 1, 0), 1);

        Matrix44D lightToWorld = new Matrix44D().rotatedX(-45).rotatedY(-45);
        DistantLight light = new DistantLight(lightToWorld, new Vec3D(1, 1, 1), 0.5 * Math.PI);

        camera.translate(new Vec3D(0, 1, 1), 4);
        camera.rotateX(-45);

        ScalaScene scene = new ScalaScene(new Object[] {disk}, new Light[] {light}, camera);

        return scene;
    }
}
