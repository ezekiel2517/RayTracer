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
        camera.cameraToWorld = camera.cameraToWorld.rotate(0, 0, 0);
        Object[] objects = new Object[] {
                new Sphere(new Matrix44D().translate(0, 0, -25), 5, new Vec3D(0.92, 0.13, 0.06)),
                /*new Sphere(new Matrix44D().translate(-5, 5.5, -22), 2.5, new Vec3D(0.28, 0.42, 0.09)),*/
                new Plane(new Matrix44D().translate(0, -5, 0), new Vec3D(0.06, 0.31, 0.01))};
        Light light = new DistantLight(new Matrix44D().rotate(-45, -45, 0), new Vec3D(1, 1, 1), 1);

        Scene scene = new Scene(objects, light, camera);

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
