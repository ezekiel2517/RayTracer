package raytracer;

import math.Matrix44D;
import math.Vec3D;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static Future<String> print(final String text) throws IOException {
        return pool.submit(() -> {
            System.out.println(text);
            return null;
        });
    }

    public static void main(String[] args) {
        final MyPanel penis = new MyPanel();
        penis.addKeyListener(penis);
        penis.addMouseListener(penis);
        penis.addMouseMotionListener(penis);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(penis);
            frame.pack();
            frame.setVisible(true);
            penis.requestFocus();
        });
        //for(; ;){
        //    penis.repaint();
        //}
    }
}

class MyPanel extends JPanel  implements KeyListener, MouseInputListener {

    private Color[][] pixels;
    private int width = 640;
    private int height = 480;
    Camera camera;
    Scene scene;
    int x, y;
    boolean realTimeMode = true;

    public MyPanel() {
        // Create scene
        camera = new Camera();
        //camera.cameraToWorld = camera.cameraToWorld.rotate(0, 0, 0).translate(0, 0, 0);
        Sphere sphere = new Sphere(new Matrix44D().translate(0, 0, 0), 1, new Vec3D(0.92, 0.13, 0.06), Object.MaterialType.PHONG);
        sphere.texture = new Texture();
        Plane p = new Plane(new Matrix44D().translate(0, -1, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p.texture = new Texture();
        Plane p3 = new Plane(new Matrix44D().translate(0, -5, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        Plane p1 = new Plane(new Matrix44D().rotate(90,90,0).translate(-10, 0, 0), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p1.texture = new Texture();
        Plane p2 = new Plane(new Matrix44D().rotate(90,0,0).translate(0, 0, -50), new Vec3D(0.06, 0.31, 0.01), Object.MaterialType.PHONG);
        //p2.texture = new Texture();
        Sphere s = new Sphere(new Matrix44D().translate(6, 0, -20), 5, new Vec3D(0.92, 0.13, 0.06), Object.MaterialType.PHONG);
        //s.texture = new Texture();

        Triangle t = new Triangle(new Matrix44D());
        t.v0 = new Vec3D(-5, -2, -10);
        t.v1 = new Vec3D(5, -2, -10);
        t.v2 = new Vec3D(0, 4, -30);
        //t.objectToWorld = new Matrix44D();
        //t.worldToObject = t.objectToWorld.inverse();
        t.albedo = new Vec3D(1, 0, 0);
        t.materialType = Object.MaterialType.PHONG;

        TriangleMesh mesh = TriangleMesh.createCuboid(1, 2, 1, new Matrix44D().rotate(0, 0, 0).translate(0, 0, 0), true);
        mesh.albedo = new Vec3D(1, 0, 0);
        mesh.materialType = Object.MaterialType.PHONG;
        mesh.smoothShading = false;
        Options.culling = true;

        TriangleMesh mesh1 = TriangleMesh.createCuboid(1, 1, 1, new Matrix44D().rotate(0, 0, 0).translate(1, -0.5, 0), true);
        mesh1.albedo = new Vec3D(1, 1, 0);
        mesh1.materialType = Object.MaterialType.PHONG;
        mesh1.smoothShading = false;
        Options.culling = true;

        TriangleMesh mesh2 = TriangleMesh.createCuboid(1, 1, 1, new Matrix44D().rotate(0, 0, 0).translate(0, -0.5, 1), true);
        mesh2.albedo = new Vec3D(1, 0, 1);
        mesh2.materialType = Object.MaterialType.REFLECTIVE_AND_REFRACTIVE;
        mesh2.smoothShading = false;
        Options.culling = true;

        TriangleMesh mesh3 = TriangleMesh.createCuboid(2, 2, 2, new Matrix44D().rotate(0, 0, 0).translate(0.5, -2, 0.5), true);
        mesh3.albedo = new Vec3D(0, 1, 1);
        mesh3.materialType = Object.MaterialType.PHONG;
        mesh3.smoothShading = false;
        Options.culling = true;

        TriangleMesh mesh4 = TriangleMesh.createCuboid(8, 8, 8, new Matrix44D().rotate(0, 0, 0).translate(0, 1, 0), false);
        mesh4.albedo = new Vec3D(0, 1, 0);
        mesh4.materialType = Object.MaterialType.PHONG;
        mesh4.smoothShading = false;
        Options.culling = true;

        Object[] objects = new Object[] {
                //mesh,
                //mesh1,
                //mesh2,
                //mesh3,
                //mesh4,
                //t,
                sphere,
                //s,
                /*new Sphere(new Matrix44D().translate(-2, -2.5, -17), 2.5, new Vec3D(0.28, 0.42, 0.09), Object.MaterialType.REFLECTIVE),*/
                //p,
                //p1,
                //p2,

                //p3
        };
        Light[] lights = new Light[] {
                //new DistantLight(new Matrix44D().rotate(-40, 20, 0), new Vec3D(1, 1, 1), 1),
                //new DistantLight(new Matrix44D().rotate(0, 0, 0), new Vec3D(1, 1, 1), 0.5),
                new PointLight(new Matrix44D().translate(2, 4, 0), new Vec3D(1, 1, 1), 100),
                new PointLight(new Matrix44D().translate(2, 0, 3), new Vec3D(1, 1, 1), 100),
                //new PointLight(new Matrix44D().translate(-5, 10, -15), new Vec3D(1, 1, 1), 0)
        };
        //objects[0].kd = 1;
        //objects[0].ks = 0;

        scene = new ScalaScene(objects, lights, camera);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*camera.cameraToWorld = camera.cameraToWorld.translate(
                camera.cameraToWorld.multiplyDirection(new Vec3D(0, 0, -0.1)).getX(),
                camera.cameraToWorld.multiplyDirection(new Vec3D(0, 0, -0.1)).getY(),
                camera.cameraToWorld.multiplyDirection(new Vec3D(0, 0, -0.1)).getZ());*/

        //System.out.println(camera);
        //System.out.println(camera.cameraToWorld);

        Vec3D[][] pix = null;
            // Render scene and measure time
            long start = System.nanoTime();

            pix = scene.render(width, height, realTimeMode);

            long stop = System.nanoTime();

            double time = stop - start;
            int min = (int) (time / 60 / 1e9);
            int sec = (int) (time / 1e9 - min * 60);
            int mil = (int) Math.round(time / 1e6 - min * 60 * 1000 - sec * 1000);

            //System.out.format("%d min %d sec %d mil%n%.3f fps", min, sec, mil, 1 / (time / 1e9));
            try {
                Main.print(min + " m " + sec + " s " + mil + " ms");
                Main.print(1 / (time / 1e9) + " fps");
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g.setColor(pixels[i][j]);
                g.drawLine(j, i, j, i);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        double u = 0.1;
        double a = 2;
        if (e.getKeyCode() == KeyEvent.VK_W) {
            Vec3D dir = camera.cameraToWorld.multiplyDirection(new Vec3D(0, 0, -1));
            camera.cameraToWorld = camera.cameraToWorld.translate(dir.multiply(u).getX(), dir.multiply(u).getY(), dir.multiply(u).getZ());
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            Vec3D dir = camera.cameraToWorld.multiplyDirection(new Vec3D(0, 0, -1));
            camera.cameraToWorld = camera.cameraToWorld.translate(dir.multiply(-u).getX(), dir.multiply(-u).getY(), dir.multiply(-u).getZ());
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            Vec3D dir = camera.cameraToWorld.multiplyDirection(new Vec3D(1, 0, 0));
            camera.cameraToWorld = camera.cameraToWorld.translate(dir.multiply(-u).getX(), dir.multiply(-u).getY(), dir.multiply(-u).getZ());
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            Vec3D dir = camera.cameraToWorld.multiplyDirection(new Vec3D(1, 0, 0));
            camera.cameraToWorld = camera.cameraToWorld.translate(dir.multiply(u).getX(), dir.multiply(u).getY(), dir.multiply(u).getZ());
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            double x = camera.cameraToWorld.get(3, 0);
            double y = camera.cameraToWorld.get(3, 1);
            double z = camera.cameraToWorld.get(3, 2);
            camera.cameraToWorld.set(3, 0, 0);
            camera.cameraToWorld.set(3, 1, 0);
            camera.cameraToWorld.set(3, 2, 0);
            camera.rotX = camera.rotX.rotate(a, 0, 0);
            camera.cameraToWorld = new Matrix44D().multiply(camera.rotX).multiply(camera.rotY).translate(x, y, z);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            double x = camera.cameraToWorld.get(3, 0);
            double y = camera.cameraToWorld.get(3, 1);
            double z = camera.cameraToWorld.get(3, 2);
            camera.cameraToWorld.set(3, 0, 0);
            camera.cameraToWorld.set(3, 1, 0);
            camera.cameraToWorld.set(3, 2, 0);
            camera.rotX = camera.rotX.rotate(-a, 0, 0);
            camera.cameraToWorld = new Matrix44D().multiply(camera.rotX).multiply(camera.rotY).translate(x, y, z);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            double x = camera.cameraToWorld.get(3, 0);
            double y = camera.cameraToWorld.get(3, 1);
            double z = camera.cameraToWorld.get(3, 2);
            camera.cameraToWorld.set(3, 0, 0);
            camera.cameraToWorld.set(3, 1, 0);
            camera.cameraToWorld.set(3, 2, 0);
            camera.rotY = camera.rotY.rotate(0, a, 0);
            camera.cameraToWorld = new Matrix44D().multiply(camera.rotX).multiply(camera.rotY).translate(x, y, z);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            double x = camera.cameraToWorld.get(3, 0);
            double y = camera.cameraToWorld.get(3, 1);
            double z = camera.cameraToWorld.get(3, 2);
            camera.cameraToWorld.set(3, 0, 0);
            camera.cameraToWorld.set(3, 1, 0);
            camera.cameraToWorld.set(3, 2, 0);
            camera.rotY = camera.rotY.rotate(0, -a, 0);
            camera.cameraToWorld = new Matrix44D().multiply(camera.rotX).multiply(camera.rotY).translate(x, y, z);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            realTimeMode = !realTimeMode;
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());
        double rotX = (e.getY() - y) * 0.1;
        double rotY = (e.getX() - x) * 0.1;
        double x = camera.cameraToWorld.get(3, 0);
        double y = camera.cameraToWorld.get(3, 1);
        double z = camera.cameraToWorld.get(3, 2);
        camera.cameraToWorld.set(3, 0, 0);
        camera.cameraToWorld.set(3, 1, 0);
        camera.cameraToWorld.set(3, 2, 0);
        camera.rotX = camera.rotX.rotate(-rotX, 0, 0);
        camera.rotY = camera.rotY.rotate(0, -rotY, 0);
        camera.cameraToWorld = new Matrix44D().multiply(camera.rotX).multiply(camera.rotY).translate(x, y, z);
        this.x = e.getX();
        this.y = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
