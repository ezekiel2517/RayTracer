package raytracer;

import math.Matrix44D;
import math.Vec3D;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(new MyPanel());
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class MyPanel extends JPanel {

    private Color[][] pixels;
    private int width = 640;
    private int height = 480;

    public MyPanel() {
        // Create scene
        Camera camera = new Camera();
        camera.cameraToWorld = camera.cameraToWorld.rotate(0, 0, 0).translate(0, 0, 0);
        Sphere sphere = new Sphere(new Matrix44D().translate(0, 0, -20), 5, new Vec3D(0.92, 0.13, 0.06), Object.MaterialType.PHONG);
        //sphere.texture = new Texture();
        Plane p = new Plane(new Matrix44D().translate(0, -5.1, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p.texture = new Texture();
        Plane p3 = new Plane(new Matrix44D().translate(0, -5, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        Plane p1 = new Plane(new Matrix44D().rotate(90,90,0).translate(-10, 0, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p1.texture = new Texture();
        Plane p2 = new Plane(new Matrix44D().rotate(90,0,0).translate(0, 0, -50), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p2.texture = new Texture();
        Sphere s = new Sphere(new Matrix44D().translate(6, 0, -20), 5, new Vec3D(0.92, 0.13, 0.06), Object.MaterialType.PHONG);
        //s.texture = new Texture();
        Object[] objects = new Object[] {
                sphere,
                //s,
                /*new Sphere(new Matrix44D().translate(-2, -2.5, -17), 2.5, new Vec3D(0.28, 0.42, 0.09), Object.MaterialType.REFLECTIVE),*/
                //p,
                //p1,
                //p2,
                //p3
        };
        Light[] lights = new Light[] {
                new DistantLight(new Matrix44D().rotate(-60, 0, -45), new Vec3D(1, 1, 1), 1),
                //new DistantLight(new Matrix44D().rotate(0, 0, 0), new Vec3D(1, 1, 1), 0.5),
                //new PointLight(new Matrix44D().translate(-2, 12, -35), new Vec3D(1, 1, 1), 1000),
                //new PointLight(new Matrix44D().translate(-2, 20, -20), new Vec3D(1, 1, 1), 2000),
                //new PointLight(new Matrix44D().translate(-5, 10, -15), new Vec3D(1, 1, 1), 0)
        };
        //objects[0].kd = 1;
        //objects[0].ks = 0;

        Scene scene = new Scene(objects, lights, camera);

        // Render scene and measure time
        long start = System.nanoTime();

        Vec3D[][] pix = scene.render(width, height);

        long stop = System.nanoTime();

        double time = stop - start;
        int min = (int) (time / 60 / 1e9);
        int sec = (int) (time / 1e9 - min * 60);
        int mil = (int) Math.round(time / 1e6 - min * 60 * 1000 - sec * 1000);

        System.out.format("%d min %d sec %d mil%n%.3f fps", min, sec, mil, 1 / (time / 1e9));

        // Convert colors
        pixels = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j] = new Color(
                        (int) (pix[i][j].getX() * 255),
                        (int) (pix[i][j].getY() * 255),
                        (int) (pix[i][j].getZ() * 255));
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g.setColor(pixels[i][j]);
                g.drawLine(j, i, j, i);
            }
        }
    }
}
