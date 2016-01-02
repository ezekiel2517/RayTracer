package raytracer;

import math.Utils;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 15.11.15.
 */
public class Textures {
    public static Texture checkerboard(double angleInDegrees, Vec2D scale, Vec3D col1, Vec3D col2) {
        return new Texture(angleInDegrees, scale) {

            @Override
            public Vec3D getColor(Vec2D texCoords) {
                rotateTexCoords(texCoords);
                double pattern = (Utils.modulo(texCoords.x * scale.x) < 0.5) ^ (Utils.modulo(texCoords.y * scale.y) < 0.5) ? 1 : 0;
                return pattern == 1 ? col1 : col2;
            }
        };
    }

    public static Texture stripes(double angleInDegrees, Vec2D scale, Vec3D col1, Vec3D col2) {
        return new Texture(angleInDegrees, scale) {

            @Override
            public Vec3D getColor(Vec2D texCoords) {
                rotateTexCoords(texCoords);
                double pattern = Utils.modulo(texCoords.x * scale.x) < 0.5 ? 1 : 0;
                return pattern == 1 ? col1 : col2;
            }
        };
    }

    public static Texture blurredCheckerboard(double angleInDegrees, Vec2D scale, Vec3D col1, Vec3D col2) {
        return new Texture(angleInDegrees, scale) {

            @Override
            public Vec3D getColor(Vec2D texCoords) {
                rotateTexCoords(texCoords);
                double pattern = (Math.cos(texCoords.y * 2 * Math.PI * scale.y) * Math.sin(texCoords.x * 2 * Math.PI * scale.x) + 1) * 0.5;
                return col1.multiply(pattern).add(col2.multiply(1 - pattern));
            }
        };
    }

    public static Texture gradient(double angleInDegrees, Vec2D scale) {
        return new Texture(angleInDegrees, scale) {

            @Override
            public Vec3D getColor(Vec2D texCoords) {
                rotateTexCoords(texCoords);
                return new Vec3D(texCoords.x, texCoords.y, 0);
            }
        };
    }
}
