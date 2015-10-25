package raytracer

import math.Vec3D
import scala.collection.par._
import Scheduler.Implicits.global

class ScalaScene(objects: Array[Object], lights: Array[Light], camera: Camera) extends Scene(objects, lights, camera) {
  override def render(width: Int, height: Int): Array[Array[Vec3D]] = {
    val pixels = Array.ofDim[Vec3D](height, width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
    val b = 1
    val c = 1
    val factor = 1.0 / (aa * aa)
    (0 until height).par.foreach { j =>
      (0 until width).par.foreach { i =>
        pixels(j)(i) = new Vec3D()
        (0 until aa).foreach { k =>
          (0 until aa).foreach { l =>
            val x = (2 * (i +  l.toDouble / aa +1 / aa * 0.5) / width - 1) * imageAspectRatio * scale
            val y = (1 - 2 * (j + k.toDouble / aa +1 / aa * 0.5) / height) * scale
            val direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1))
            pixels(j)(i) = pixels(j)(i).add(castRay(new Ray(origin, direction, Ray.RayType.PRIMARY_RAY), 0).multiply(factor));
          }
        }
      }
    }
    pixels
    /*Vec3D[][] pixels = new Vec3D[height][width];
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
                    for (double l = 0; l < aa; l++) {
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
        return pixels;*/
  }
}
