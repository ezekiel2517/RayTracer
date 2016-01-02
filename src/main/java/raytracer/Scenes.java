package raytracer;

import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 15.11.15.
 */
public class Scenes {
    public static Scene stones(Camera camera) {
        Matrix44D sphereo2w = new Matrix44D();
        sphereo2w.set(3, 2, -5);
        sphereo2w.set(3, 1, -0.5);
        Sphere sphere = new Sphere(sphereo2w, 0.5, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere.texture = Textures.gradient(0, new Vec2D(10, 10));
        Matrix44D plane02w = new Matrix44D();
        plane02w.set(3, 1, -1);
        Plane plane = new Plane(plane02w, new Vec3D(0, 1, 0), Object.MaterialType.PHONG);

        Matrix44D box2w = new Matrix44D();
        box2w = box2w.rotatedY(30);
        box2w.set(3, 0, 2);
        box2w.set(3, 1, 0.5);
        box2w.set(3, 2, -7);
        TriangleMesh box = TriangleMesh.createCuboid(1, 4, 1, box2w, true);
        box.materialType = Object.MaterialType.PHONG;
        box.albedo = new Vec3D(1, 0.5, 0);
        box.smoothShading = false;

        Matrix44D box22w = new Matrix44D();
        box22w = box22w.rotatedY(5);
        box22w.set(3, 0, -1.5);
        box22w.set(3, 1, 0.5);
        box22w.set(3, 2, -7.5);
        TriangleMesh box2 = TriangleMesh.createCuboid(1, 4, 1, box22w, true);
        box2.materialType = Object.MaterialType.PHONG;
        box2.albedo = new Vec3D(1, 0.5, 0);
        box2.smoothShading = false;

        Matrix44D box32w = new Matrix44D();
        box32w = box32w.rotatedZ(90);
        box32w = box32w.rotatedY(-10);
        box32w.set(3, 0, 0.5);
        box32w.set(3, 1, 3);
        box32w.set(3, 2, -7);
        TriangleMesh box3 = TriangleMesh.createCuboid(1, 6, 1, box32w, true);
        box3.materialType = Object.MaterialType.PHONG;
        box3.albedo = new Vec3D(1, 0.5, 0);
        box3.smoothShading = false;

        Matrix44D lighto2w = new Matrix44D();
        lighto2w = lighto2w.rotatedX(-20).rotatedY(-60);
        DistantLight light = new DistantLight(lighto2w, new Vec3D(1, 0.5, 0.75), 0.25);

        PointLight pointLight = new PointLight(new Matrix44D().translated(new Vec3D(0, 1, -8)), new Vec3D(1, 1, 0), 50);

        camera.translate(new Vec3D(0, -1, 0), 0.5);
        camera.rotateX(10);
        camera.rotateY(-5);
        return new ScalaScene(new Object[] {sphere, plane, box, box2, box3}, new Light[] {light, pointLight}, camera);
    }

    public static Scene reflections(Camera camera) {
        Matrix44D sphere1o2w = new Matrix44D();
        sphere1o2w.set(3, 1, -2);
        sphere1o2w.set(3, 2, -0.5);
        Sphere sphere1 = new Sphere(sphere1o2w, 1, new Vec3D(1, 0, 0), Object.MaterialType.REFLECTIVE);
        Matrix44D sphere2o2w = new Matrix44D();
        sphere2o2w.set(3, 0, 2.5);
        sphere2o2w.set(3, 1, -1.5);
        sphere2o2w.set(3, 2, -2);
        Sphere sphere2 = new Sphere(sphere2o2w, 1.5, new Vec3D(1, 0, 0), Object.MaterialType.REFLECTIVE);
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
        box.materialType = Object.MaterialType.PHONG;
        box.albedo = new Vec3D(0, 1, 0);
        box.smoothShading = false;

        Matrix44D box22w = new Matrix44D();
        box22w = box22w.rotatedY(-20);
        box22w.set(3, 0, -1.5);
        box22w.set(3, 1, 0);
        box22w.set(3, 2, 1.5);
        TriangleMesh box2 = TriangleMesh.createCuboid(2, 2, 2, box22w, true);
        box2.materialType = Object.MaterialType.PHONG;
        box2.albedo = new Vec3D(1, 0, 0);
        box2.smoothShading = false;

        Matrix44D l2w1 = new Matrix44D();
        l2w1.set(3, 0, -4);
        l2w1.set(3, 1, 0);
        l2w1.set(3, 2, -4);
        PointLight light1 = new PointLight(l2w1, new Vec3D(0, 0, 1), 100);

        Matrix44D l2w3 = new Matrix44D();
        l2w3.set(3, 0, -4);
        l2w3.set(3, 1, 0);
        l2w3.set(3, 2, 4);
        PointLight light3 = new PointLight(l2w3, new Vec3D(0, 0, 1), 100);

        Matrix44D l2w2 = new Matrix44D();
        l2w2.set(3, 0, 4);
        l2w2.set(3, 1, -1.5);
        l2w2.set(3, 2, 4);
        PointLight light2 = new PointLight(l2w2, new Vec3D(1, 1, 1), 250);

        camera.translate(new Vec3D(0, -1, 0), 1);
        camera.rotate(-10, 10);
        camera.translate(new Vec3D(0, 0, 1), 7.5);
        return new ScalaScene(new Object[] {sphere1, sphere2, room, box, box2}, new Light[] {light1, light2, light3}, camera);
    }

    public static Scene mirrorFloor(Camera camera) {
        Plane bottomPlane = new Plane(new Matrix44D(), new Vec3D(), Object.MaterialType.PHONG);
        bottomPlane.texture = Textures.checkerboard(20, new Vec2D(1, 1), new Vec3D(), new Vec3D(1, 1, 1));
        Plane topPlane = new Plane(new Matrix44D().translated(new Vec3D(0, 0.01, 0)), new Vec3D(), Object.MaterialType.REFLECTIVE_AND_REFRACTIVE);
        topPlane.ior = 10;
        Sphere sphere = new Sphere(new Matrix44D().translated(new Vec3D(0, 1.01, -8)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere.texture = Textures.stripes(45, new Vec2D(10, 10), new Vec3D(0.7, 0.1, 0.9), new Vec3D(0.2, 0.7, 0.1));
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-45).rotatedY(45), new Vec3D(1, 1, 1), 1);

        camera.translate(new Vec3D(0, 1, 0), 1);
        camera.rotateX(-10);
        return new ScalaScene(new Object[] {bottomPlane, topPlane, sphere}, new Light[] {distLight1}, camera);
    }

    public static Scene refractions(Camera camera) {
        Plane plane = new Plane(new Matrix44D(), new Vec3D(), Object.MaterialType.PHONG);
        plane.texture = Textures.checkerboard(0, new Vec2D(1, 1), new Vec3D(0, 0.5, 0.25), new Vec3D(1, 1, 0));
        Sphere sphere = new Sphere(new Matrix44D().translated(new Vec3D(0, 1, -4)), 1, new Vec3D(1, 0, 0), Object.MaterialType.REFLECTIVE_AND_REFRACTIVE);
        Sphere sphere2 = new Sphere(new Matrix44D().translated(new Vec3D(-1.1, 1, -6)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere2.texture = Textures.checkerboard(0, new Vec2D(10, 10), new Vec3D(0.7, 0.1, 0.9), new Vec3D(0.2, 0.7, 0.1));
        Sphere sphere3 = new Sphere(new Matrix44D().translated(new Vec3D(1.4, 1, -5)), 1, new Vec3D(1, 0, 0), Object.MaterialType.PHONG);
        sphere3.texture = Textures.gradient(0, new Vec2D(10, 10));
        sphere.ior = 5;
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-45).rotatedY(45), new Vec3D(1, 1, 1), 1);
        camera.translate(new Vec3D(0, 1, 0), 1.5);
        camera.rotateX(-12);
        return new ScalaScene(new Object[] {plane, sphere, sphere2, sphere3}, new Light[] {distLight1}, camera);
    }

    public static Scene polySphere(Camera camera) {
        TriangleMesh sphere = TriangleMesh.createPolySphere(1, 10);
        sphere.materialType = Object.MaterialType.PHONG;
        sphere.albedo = new Vec3D(0, 1, 0);
        sphere.smoothShading = true;
        DistantLight distLight1 = new DistantLight(new Matrix44D().rotatedX(-45).rotatedY(-45), new Vec3D(1, 1, 1), 1);
        camera.translate(new Vec3D(0, 0, 1), 3);
        return new ScalaScene(new Object[] {sphere}, new Light[] {distLight1}, camera);
    }
}
