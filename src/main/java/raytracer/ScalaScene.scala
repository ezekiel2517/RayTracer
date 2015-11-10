package raytracer

import math.Vec3D
import scala.collection.par._
import Scheduler.Implicits.global

class ScalaScene(objects: Array[Object], lights: Array[Light], camera: Camera) extends Scene(objects, lights, camera) {

  def renderQuickly(width: Int, height: Int) = {
    val pixels = Array.ofDim[Vec3D](height, width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
    (0 until height).par.foreach { j =>
      (0 until width).par.foreach { i =>
        val x = (2 * (i + 0.5) / width - 1) * imageAspectRatio * scale
        val y = (1 - 2 * (j + 0.5) / height) * scale
        val direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1))
        pixels(j)(i) = castRayQuickly(new Ray(origin, direction, Ray.RayType.PRIMARY_RAY));
      }
    }
    pixels
  }

  protected def castRayQuickly(ray: Ray): Vec3D = {
    val isect: Intersection = new Intersection
    trace(ray, isect)
    if (isect.hitObject != null) {
      val hitPoint: Vec3D = ray.origin.add(ray.direction.multiply(isect.tNear))
      val props: SurfaceProperties = isect.hitObject.getSurfaceProperties(hitPoint)
      val hitColor =
      if (isect.hitObject.texture != null) {
        isect.hitObject.texture.getPattern(props.hitTextureCoordinates)
      }
      else {
        isect.hitObject.albedo
      }

      if (hitColor.getX > 1) hitColor.setX(1)
      if (hitColor.getY > 1) hitColor.setY(1)
      if (hitColor.getZ > 1) hitColor.setZ(1)
      if (hitColor.getX < 0) hitColor.setX(0)
      if (hitColor.getY < 0) hitColor.setY(0)
      if (hitColor.getZ < 0) hitColor.setZ(0)
      hitColor
    }
    else backgroundColor
  }

  override def render(width: Int, height: Int, realTimeMode: Boolean): Array[Array[Vec3D]] = {
    if (realTimeMode) return renderQuickly(width, height)
    val pixels = Array.ofDim[Vec3D](height, width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
    val b = 1
    val c = 1
    val factor = 1.0 / (aa * aa)
    (0 until height).foreach { j =>
      (0 until width).foreach { i =>
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
