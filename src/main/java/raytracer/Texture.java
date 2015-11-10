package raytracer;

import math.Utils;
import math.Vec2D;
import math.Vec3D;

public class Texture {
    double angle = Math.toRadians(0);
    double scaleS = 10, scaleT = 10;

    public Vec3D getPattern(Vec2D textureCoordinates) {
        double s = textureCoordinates.x * Math.cos(angle) - textureCoordinates.y * Math.sin(angle);
        double t = textureCoordinates.y * Math.cos(angle) + textureCoordinates.x * Math.sin(angle);
        double pattern = (Math.cos(textureCoordinates.y * 2 * Math.PI * scaleT) * Math.sin(textureCoordinates.x * 2 * Math.PI * scaleS) + 1) * 0.5;
        //double pattern = (Utils.modulo(s * scaleS) < 0.5) ^ (Utils.modulo(t * scaleT) < 0.5) ? 1 : 0;
        //double pattern = Utils.modulo(s * scaleS) < 0.5 ? 1 : 0;
        Vec3D c = new Vec3D(pattern, pattern, pattern);
        //Vec3D c = pattern == 1 ? new Vec3D(1, 0, 1) : new Vec3D(1, 1, 0);
        //Vec3D c = new Vec3D(textureCoordinates.x, textureCoordinates.y, textureCoordinates.y);
        return c;
    }
}
