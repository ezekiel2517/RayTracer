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
        camera.cameraToWorld = camera.cameraToWorld.rotate(-10, 0, 0).translate(0, 3, 0);
        Object[] objects = new Object[] {
                new Sphere(new Matrix44D().translate(0, 0, -20), 5, new Vec3D(0.92, 0.13, 0.06), Object.MaterialType.REFLECTIVE_AND_REFRACTIVE),
                /*new Sphere(new Matrix44D().translate(2, 7, -40), 7, new Vec3D(0.92, 0.83, 0.06), Object.MaterialType.PHONG),
                new Sphere(new Matrix44D().translate(-6, 0, -31), 5, new Vec3D(0.28, 0.42, 0.09), Object.MaterialType.PHONG),
                new Sphere(new Matrix44D().translate(-2, -2.5, -17), 2.5, new Vec3D(0.28, 0.42, 0.09), Object.MaterialType.REFLECTIVE),*/
                new Plane(new Matrix44D().translate(0, -5, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG)};
        Light[] lights = new Light[] {
                new DistantLight(new Matrix44D().rotate(-90, 0, 45), new Vec3D(1, 1, 1), 0),
                new PointLight(new Matrix44D().translate(5, 10, -25), new Vec3D(1, 1, 1), 4000),
                new PointLight(new Matrix44D().translate(-5, 10, -15), new Vec3D(1, 1, 1), 4000)};
        //objects[4].kd = 1;
        //objects[4].ks = 0;

        Scene scene = new Scene(objects, lights, camera);

        // Render scene and measure time
        long start = System.nanoTime();

        Vec3D[][] pix = scene.render(width, height);

        long stop = System.nanoTime();

        double time = (stop - start) / 1e9;

        System.out.println(time + "s");
        System.out.println(1 / time + "fps");

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
