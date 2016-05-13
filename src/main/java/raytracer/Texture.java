package raytracer;

//import com.google.gson.annotations.Expose;
import math.Utils;
import math.Vec2D;
import math.Vec3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Texture {
    //@Expose
    public double angle;
    //@Expose
    public Vec2D scale;
    //@Expose
    public String type;

    public Texture(){

    }

    public Texture(double angleInDegrees, Vec2D scale) {
        setAngle(angleInDegrees);
        this.scale = new Vec2D(scale);
    }

    public void setAngle(double degrees) {
        angle = Math.toRadians(degrees);
    }

    protected void rotateTexCoords(Vec2D texCoords) {
        texCoords.x = texCoords.x * Math.cos(angle) - texCoords.y * Math.sin(angle);
        texCoords.y = texCoords.y * Math.cos(angle) + texCoords.x * Math.sin(angle);
    }

    public Vec3D getColor(Vec2D texCoords){
        return null;
    }
}
