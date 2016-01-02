package raytracer;

import math.Matrix44D;
import math.Vec3D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Panel extends JPanel {

    private class Listener extends MouseInputAdapter implements KeyListener {
        private double movUnit = 0.1;
        private double keyRotUnit = 2;
        private double mouseRotUnit = 0.1;

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    camera.translate(new Vec3D(0, 0, -1), movUnit); break;
                case KeyEvent.VK_S:
                    camera.translate(new Vec3D(0, 0, 1), movUnit); break;
                case KeyEvent.VK_A:
                    camera.translate(new Vec3D(-1, 0, 0), movUnit); break;
                case KeyEvent.VK_D:
                    camera.translate(new Vec3D(1, 0, 0), movUnit); break;
                case KeyEvent.VK_UP:
                    camera.rotateX(keyRotUnit); break;
                case KeyEvent.VK_DOWN:
                    camera.rotateX(-keyRotUnit); break;
                case KeyEvent.VK_LEFT:
                    camera.rotateY(keyRotUnit); break;
                case KeyEvent.VK_RIGHT:
                    camera.rotateY(-keyRotUnit); break;
                case KeyEvent.VK_R:
                    realTimeMode = !realTimeMode; break;
                case KeyEvent.VK_C:
                    System.out.println(camera); return;
                case KeyEvent.VK_Z:
                    useAA = !useAA;
                    if (useAA) scene.aa = 4;
                    else scene.aa = 1;
                    break;
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            cursorX = e.getX();
            cursorY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            double rotX = (cursorY - e.getY()) * mouseRotUnit;
            double rotY = (cursorX - e.getX()) * mouseRotUnit;
            camera.rotate(rotX, rotY);
            repaint();
            cursorX = e.getX();
            cursorY = e.getY();
        }
    }

    private static int width = 640, height = 480;
    private Camera camera = new Camera();
    private Scene scene = Scenes.refractions(camera);
    private int cursorX, cursorY;
    private boolean realTimeMode = true;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private DecimalFormat f = new DecimalFormat();
    private boolean useAA = false;

    public Panel() {
        setPreferredSize(new Dimension(width, height));
        f.setMaximumFractionDigits(3);
        Listener listener = new Listener();
        addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    public void printData(long time) {
        int min = (int) (time / 1e9 / 60);
        int sec = (int) (time / 1e9 - min * 60);
        int mil = (int) Math.round(time / 1e6 - min * 60 * 1000 - sec * 1000);
        double fps = 1 / (time / 1e9);

        try {
            print(min + " m " + sec + " s " + mil + " ms\t" + f.format(fps) + " fps");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        long start = System.nanoTime();

        Vec3D[][] pixels = scene.render(width, height, realTimeMode);

        long stop = System.nanoTime();
        long time = stop - start;
        printData(time);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g.setColor(new Color(
                        (int) (pixels[i][j].getX() * 255),
                        (int) (pixels[i][j].getY() * 255),
                        (int) (pixels[i][j].getZ() * 255)));
                g.drawLine(j, i, j, i);
            }
        }
    }

    public static Future<String> print(final String text) throws IOException {
        return pool.submit(() -> {
            System.out.println(text);
            return null;
        });
    }

    public static void createUI() {
        JFrame frame = new JFrame("Ray tracer");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Panel panel = new Panel();
        frame.add(panel);
        frame.pack();
        panel.requestFocusInWindow();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Panel::createUI);
    }
}
