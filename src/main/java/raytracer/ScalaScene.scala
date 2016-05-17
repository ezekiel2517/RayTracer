package raytracer

import java.io.IOException
import java.text.DecimalFormat

import math.{Vec2D, Vec3D}
import scala.collection.par._
import Scheduler.Implicits.global

class ScalaScene(objects: Array[Object], lights: Array[Light], camera: Camera) extends Scene(objects, lights, camera) {

  def renderQuickly(width: Int, height: Int, pixels: Array[Vec3D]) = {
    stats = new Stats()
    stats.setnPixels(width * height)
    stats.setnObjects(objects.length)
    stats.setnLights(lights.length)
    stats.setnPrimaryRays(width * height * aa * aa)
    stats.setnTris(nTris)
    //val pixels = Array.ofDim[Vec3D](height, width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
    (0 until height).foreach { j =>
      (0 until width).foreach { i =>
        val x = (2 * (i + 0.5) / width - 1) * imageAspectRatio * scale
        val y = (1 - 2 * (j + 0.5) / height) * scale
        val direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1))
        pixels(j * width + i) = castRayQuickly(new Ray(origin, direction, Ray.RayType.PRIMARY_RAY));
      }
    }
    //pixels
  }

  protected def castRayQuickly(ray: Ray): Vec3D = {
    val isect: Intersection = new Intersection
    trace(ray, isect)
    if (isect.hitObject != null) {
      val hitPoint: Vec3D = ray.origin.add(ray.direction.multiply(isect.tNear))
      val props: SurfaceProperties = isect.hitObject.getSurfaceProperties(hitPoint)
      val hitColor =
        if (isect.hitObject.texture != null) isect.hitObject.texture.getColor(props.hitTextureCoordinates)
        else isect.hitObject.albedo

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

  override def render(width: Int, height: Int, row: Int, pixels: Array[Vec3D] ) = {
    stats = new Stats()
    stats.setnPixels(width * height)
    stats.setnObjects(objects.length)
    stats.setnLights(lights.length)
    stats.setnPrimaryRays(width * height * aa * aa)
    stats.setnTris(nTris)
    //val pixels = Array.ofDim[Vec3D](width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
    val factor = 1.0 / (aa * aa)
    (0 until width).foreach { i =>
      pixels(row * width + i) = new Vec3D()
      (0 until aa).foreach { k =>
        (0 until aa).foreach { l =>
          val x = (2 * (i + l.toDouble / aa + 1 / aa * 0.5) / width - 1) * imageAspectRatio * scale
          val y = (1 - 2 * (row + k.toDouble / aa + 1 / aa * 0.5) / height) * scale
          val direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1))
          val ray = new Ray(origin, direction, Ray.RayType.PRIMARY_RAY)
          val color = castRay(ray, 0, new Vec2D())
          val atm = new Atmosphere()
          val atmosphereColor = new Vec3D()
          var transmittance = new Vec3D()
          val isect = new Intersection()
          if (renderAtmosphere) {
            trace(ray, isect)
            val t = Double.PositiveInfinity
            if (isect.hitObject == null) {
              //t = isect.tNear
              transmittance = atm.computeIncidentLight(ray, atmosphereColor, t)
              pixels(row * width + i) = pixels(row * width + i).add(atmosphereColor.multiply(factor))
            } else
              pixels(row * width + i) = pixels(row * width + i).add(color.multiply(factor))
          } else {
            pixels(row * width + i) = pixels(row * width + i).add(color.multiply(factor))
          }


//          if (atmosphereColor.getX < 1.413)
//            atmosphereColor.setX(Math.pow(atmosphereColor.getX * 0.38317, 1 / 2.2))
//          else
//            atmosphereColor.setX(1 - Math.exp(-atmosphereColor.getX))
//          if (atmosphereColor.getY < 1.413)
//            atmosphereColor.setY(Math.pow(atmosphereColor.getY * 0.38317, 1 / 2.2))
//          else
//            atmosphereColor.setY(1 - Math.exp(-atmosphereColor.getY))
//          if (atmosphereColor.getZ < 1.413)
//            atmosphereColor.setZ(Math.pow(atmosphereColor.getZ * 0.38317, 1 / 2.2))
//          else
//            atmosphereColor.setZ(1 - Math.exp(-atmosphereColor.getZ))

          //atmosphereColor = atmosphereColor.multiply(2000)
          //if (isect.tNear > 100)
          //System.out.println(isect.tNear)
          //pixels(i) = pixels(i).add(atmosphereColor.multiply(factor))
          //pixels(i) = pixels(i).add(color.multiply(transmittance.multiply(-1).add(new Vec3D(1, 1, 1))).add(atmosphereColor).multiply(factor))
          //pixels(i) = pixels(i).add(color.multiply(factor))
          //pixels(i) = pixels(i).add(color.multiply(transmittance).add(atmosphereColor).multiply(factor))
        }
      }
      pixels(row * width + i).setX(Math.max(0, Math.min(1, pixels(row * width + i).getX)))
      pixels(row * width + i).setY(Math.max(0, Math.min(1, pixels(row * width + i).getY)))
      pixels(row * width + i).setZ(Math.max(0, Math.min(1, pixels(row * width + i).getZ)))
    }
  }

  override def render(width: Int, height: Int, pixels: Array[Vec3D]) = {
    stats = new Stats()
    stats.setnPixels(width * height)
    stats.setnObjects(objects.length)
    stats.setnLights(lights.length)
    stats.setnPrimaryRays(width * height * aa * aa)
    stats.setnTris(nTris)

//    val f = new DecimalFormat()
//    f.setMaximumFractionDigits(1)

    //val pixels = Array.ofDim[Vec3D](height, width)
    val scale = Math.tan(Math.toRadians(camera.fov * 0.5))
    val imageAspectRatio: Double = width.toDouble / height.toDouble
    val origin = camera.cameraToWorld.multiplyPoint(new Vec3D())
//    var b = 1
//    var c = 1
    val factor = 1.0 / (aa * aa)
    (0 until height).foreach { j =>
      (0 until width).foreach { i =>
        if (!Options.renderProgressively || pixels(j * width + i) == null || samplesCounter == 0) {
          pixels(j * width + i) = new Vec3D()
        }
        (0 until aa).foreach { k =>
          (0 until aa).foreach { l =>
            val x = (2 * (i +  l.toDouble / aa +1 / aa * 0.5) / width - 1) * imageAspectRatio * scale
            val y = (1 - 2 * (j + k.toDouble / aa +1 / aa * 0.5) / height) * scale
            val direction = camera.cameraToWorld.multiplyDirection(new Vec3D(x, y, -1))
            pixels(j * width + i) = pixels(j * width + i).add(castRay(new Ray(origin, direction, Ray.RayType.PRIMARY_RAY), 0, new Vec2D()).multiply(factor));
          }
        }
      }
//      val a = (j + 1.0) / height * 100
//      if (a >= b) {
//        //System.out.format("%.1f%%%n", a);
//
//        Panel.print(f.format(a) + "%")
//        b = 10 * c
//        c += 1
//      }
    }
//    stats.printStats()

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
                    Panel.print(a + "%");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b = 10 * c++;
            }
        }
        return pixels;*/
  }
}
