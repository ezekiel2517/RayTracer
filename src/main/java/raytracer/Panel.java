package raytracer;

//import com.google.gson.*;
import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.*;
import java.lang.Object;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Panel extends JPanel {

    private class Listener extends MouseInputAdapter implements KeyListener {
        private double movUnit = 0.25;
        private double keyRotUnit = 2;
        private double mouseRotUnit = 0.1;

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_V:
                    if (continueProgRend) {
                        continueProgRend = false;
                    }
                    return;
            }

            if (renderingInProgress) {
                return;
            }

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
                    realTimeMode = !realTimeMode;
                    continueProgRend = true;
                    break;
                case KeyEvent.VK_C:
                    camera.reset();
                    break;
                case KeyEvent.VK_Z:
                    useAA = !useAA;
                    if (useAA) scene.aa = aa;
                    else scene.aa = 1;
                    scene.createRayTreesArray(Options.width, Options.height, scene.aa);
                    break;
                case KeyEvent.VK_X:
                    scene.stats.printStats();
                    break;
                case KeyEvent.VK_I:
                    Options.renderIncrementally = !Options.renderIncrementally;
                    break;
                case KeyEvent.VK_P:
                    Options.renderProgressively = !Options.renderProgressively;
                    break;
                case KeyEvent.VK_G:
                    scene.useGI = !scene.useGI;
                    break;
                case KeyEvent.VK_M:
                    for (int i = 0; i < Options.width; i++) {
                        for (int j = 0; j < Options.height; j++) {
                            if (pixels[j * Options.width + i].x == 0 && pixels[j * Options.width + i].y == 0 && pixels[j * Options.width + i].z == 0) {
                                System.out.println(i + " " + j);
                                return;
                            }
                        }
                    }
            }
            update();
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
            if (renderingInProgress) {
                return;
            }
            double rotX = (cursorY - e.getY()) * mouseRotUnit;
            double rotY = (cursorX - e.getX()) * mouseRotUnit;
            camera.rotate(rotX, rotY);
            update();
            //repaint();
            //render();
            cursorX = e.getX();
            cursorY = e.getY();
        }
    }

    class Worker extends SwingWorker<Vec3D[], Void> {

        @Override
        protected Vec3D[] doInBackground() throws Exception {
            if (Options.renderIncrementally) {
                scene.render(pixels, row, realTimeMode);
            } else {
                scene.render(pixels, realTimeMode);
            }
            return pixels;
        }
    }

    private Camera camera = new Camera();
    private ScalaScene scene = Scenes.globalIllum(camera);
    private int cursorX, cursorY;
    private boolean realTimeMode = true;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private DecimalFormat f = new DecimalFormat();
    private boolean useAA = false;
    int aa;
    boolean continueProgRend;
    Vec3D[] pixels;
    BufferedImage img;
    Worker worker;
    int row;
    boolean renderingInProgress;
    Timer timer = new Timer(1, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (worker.isDone()) {
                timer.stop();
                try {
                    pixels = worker.get();
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }

                if (Options.renderIncrementally) {
                    for (int j = 0; j < Options.width; j++) {
                        int k = row * Options.width + j;
                        img.setRGB(j, row, (int) (Math.round(pixels[k].getX() * 255) << 16 |
                                Math.round(pixels[k].getY() * 255) << 8 | Math.round(pixels[k].getZ() * 255)));
                    }
                    paintImmediately(0, row, Options.width, 1);
                    if (row == Options.height - 1) {
                        row = 0;
                        if (Options.renderProgressively && scene.useGI && !realTimeMode) {
                            try {
                                print(String.valueOf(scene.getnSamples()));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                            if (scene.getnSamples() == scene.N || !continueProgRend) {
                                scene.resetRayTreesArray();
                                renderingInProgress = false;
                            } else {
                                scene.incrnSamples();
                                update();
                            }
                        } else {
                            renderingInProgress = false;
                        }
                    } else {
                        row++;
                        update();
                    }
                } else {
                    for (int i = 0; i < Options.height; i++) {
                        for (int j = 0; j < Options.width; j++) {
                            int k = i * Options.width + j;
                            img.setRGB(j, i, (int) (Math.round(pixels[k].getX() * 255) << 16 |
                                    Math.round(pixels[k].getY() * 255) << 8 | Math.round(pixels[k].getZ() * 255)));
                        }
                    }
                    paintImmediately(0, 0, Options.width, Options.height);
                    if (Options.renderProgressively && scene.useGI && !realTimeMode) {
                        try {
                            print(String.valueOf(scene.getnSamples()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        if (scene.getnSamples() == scene.N || !continueProgRend) {
                            scene.resetRayTreesArray();
                            renderingInProgress = false;
                        } else {
                            scene.incrnSamples();
                            update();
                        }
                    } else {
                        renderingInProgress = false;
                    }
                }
            }
        }
    });


    //int[] ints = new int[width * height];

    public Panel() {
        setPreferredSize(new Dimension(Options.width, Options.height));
        f.setMaximumFractionDigits(3);
        Listener listener = new Listener();
        addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        pixels = new Vec3D[Options.width * Options.height];
        img = new BufferedImage(Options.width, Options.height, BufferedImage.TYPE_INT_RGB);
        aa = scene.aa;
        if (!useAA) {
            scene.aa = 1;
        }
        scene.createRayTreesArray(Options.width, Options.height, scene.aa);
        //render();
    }

    public void update() {
        worker = new Worker();
        worker.execute();
        renderingInProgress = true;
        timer.start();
    }

    public void printData(long time1, long time2, long time3) {
        int min1 = (int) (time1 / 1e9 / 60);
        int sec1 = (int) (time1 / 1e9 - min1 * 60);
        int mil1 = (int) Math.round(time1 / 1e6 - min1 * 60 * 1000 - sec1 * 1000);
        //double fps = 1 / (time / 1e9);
        int min2 = (int) (time2 / 1e9 / 60);
        int sec2 = (int) (time2 / 1e9 - min2 * 60);
        int mil2 = (int) Math.round(time2 / 1e6 - min2 * 60 * 1000 - sec2 * 1000);

        int min3 = (int) (time3 / 1e9 / 60);
        int sec3 = (int) (time3 / 1e9 - min3 * 60);
        int mil3 = (int) Math.round(time3 / 1e6 - min3 * 60 * 1000 - sec3 * 1000);

        try {
            print(min1 + " m " + sec1 + " s " + mil1 + " ms\t" + /*f.format(fps) + " fps"*/
                    min2 + " m " + sec2 + " s " + mil2 + " ms\t" + min3 + " m " + sec3 + " s " + mil3 + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img == null) {
            return;
        }

        g.drawImage(img, 0, 0, null);

        if (realTimeMode) {
            renderXYZ(g);
        }
    }

    private void renderXYZ(Graphics g) {
        int xx = 72, yy = Options.height - 72, l = 64;

        Matrix44D worldToCamera = camera.cameraToWorld.inverse();

        Vec3D[] coords = new Vec3D[] {
                worldToCamera.multiplyDirection(new Vec3D(1, 0, 0)),
                worldToCamera.multiplyDirection(new Vec3D(0, 1, 0)),
                worldToCamera.multiplyDirection(new Vec3D(0, 0, 1))};
        double[] zs = new double[] {coords[0].getZ(), coords[1].getZ(), coords[2].getZ()};
        Color[] colors = new Color[] {Color.RED, Color.GREEN, Color.BLUE};
        int[] index = new int[] {0, 1, 2};

        for (int i = 0; i < 3; i++) {
            int min = i;
            for (int j = i + 1; j < 3; j++) {
                if (zs[j] < zs[min]) {
                    min = j;
                }
            }
            int tmp = index[i];
            index[i] = index[min];
            index[min] = tmp;
        }

        for (int i : index) {
            g.setColor(colors[i]);
            g.drawLine(xx, yy, xx + (int) (coords[i].getX() * l), yy - (int) (coords[i].getY() * l));
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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Panel panel = new Panel();
        //panel.setIgnoreRepaint(true);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        panel.requestFocusInWindow();
        frame.setVisible(true);
        panel.update();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Panel::createUI);
    }
}
