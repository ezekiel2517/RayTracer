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
            if (worker != null && !worker.isDone()) {
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
                    break;
                case KeyEvent.VK_C:
                    camera.reset();
                    break;
                case KeyEvent.VK_Z:
                    useAA = !useAA;
                    if (useAA) scene.aa = aa;
                    else scene.aa = 1;
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
                case KeyEvent.VK_V:
                    if (continueProgRend) {
                        continueProgRend = false;
                    }
                    break;
                case KeyEvent.VK_G:
                    scene.useGI = !scene.useGI;
                    break;
            }
            update();
            //render();
            //repaint();
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
            if (worker != null && !worker.isDone()) {
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

    class Worker extends SwingWorker<BufferedImage, Void> {

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if (Options.renderProgressively && !realTimeMode && scene.useGI) {
                return renderProgressively();
            }
            if (Options.renderIncrementally && !realTimeMode) {
                return renderIncrementally();
            }
            return render();
        }
    }

    private static int width = 640, height = 480;
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
    int maxSamples = 1024;
    Timer timer = new Timer(1, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (worker.isDone()) {
                timer.stop();
                try {
                    img = worker.get();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                paintImmediately(0, 0, width, height);
                if (Options.renderProgressively && !realTimeMode && scene.useGI) {
                    if (scene.samplesCounter == maxSamples - 1) {
                        scene.samplesCounter = 0;
                    } else {
                        scene.samplesCounter++;
                        update();
                    }
                    return;
                }
                if (Options.renderIncrementally && !realTimeMode) {
                    if (row == height - 1) {
                        row = 0;
                    } else {
                        row++;
                        update();
                    }
                }
            }
        }
    });


    //int[] ints = new int[width * height];

    public Panel() {
        setPreferredSize(new Dimension(width, height));
        f.setMaximumFractionDigits(3);
        Listener listener = new Listener();
        addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        pixels = new Vec3D[width * height];
        aa = scene.aa;
        if (!useAA) {
            scene.aa = 1;
        }
        //render();
    }

    public void update() {
        if (worker != null && !worker.isDone()) {
            return;
        }
        worker = new Worker();
        worker.execute();
        timer.start();
    }

    private BufferedImage render() {
        long start = System.nanoTime();

        if (realTimeMode) {
            scene.renderQuickly(width, height, pixels);
        } else {
            scene.render(width, height, pixels);
        }

        long stop = System.nanoTime();
        long time1 = stop - start;

        start = System.nanoTime();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = i * width + j;
                img.setRGB(j, i, (int) (Math.round(pixels[k].getX() * 255) << 16 |
                        Math.round(pixels[k].getY() * 255) << 8 | Math.round(pixels[k].getZ() * 255)));
            }
        }

//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                int k = i * width + j;
//                ints[k] = (int) (Math.round(pixels[k].getX() * 255) << 16 |
//                        Math.round(pixels[k].getY() * 255) << 8 | Math.round(pixels[k].getZ() * 255));
//            }
//        }
//        img.setRGB(0, 0, width, height, ints, 0, width);

        stop = System.nanoTime();
        long time2 = stop - start;

        start = System.nanoTime();

        //paintImmediately(0, 0, width, height);
        //repaint();

        stop = System.nanoTime();
        long time3 = stop - start;

        printData(time1, time2, time3);
        return img;
    }

    private BufferedImage renderProgressively() {
        long start, stop, time1 = 0, time2 = 0, time3 = 0;

        start = System.nanoTime();

        scene.render(width, height, pixels);

        stop = System.nanoTime();
        time1 += stop - start;

        start = System.nanoTime();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = i * width + j;
                img.setRGB(j, i, (int) (Math.round(pixels[k].getX() * 255 / (scene.samplesCounter + 1)) << 16 |
                        Math.round(pixels[k].getY() * 255 / (scene.samplesCounter + 1)) << 8 |
                        Math.round(pixels[k].getZ() * 255 / (scene.samplesCounter + 1))));
            }
        }

        stop = System.nanoTime();
        time2 = stop - start;

        start = System.nanoTime();

        //paintImmediately(0, 0, width, height);
        //repaint();

        stop = System.nanoTime();
        time3 = stop - start;

        try {
            print(Integer.toString(scene.samplesCounter + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //printData(time1, time2, time3);
        return img;
    }

    public BufferedImage renderIncrementally() {
        long start, stop, time1 = 0, time2 = 0, time3 = 0;


        start = System.nanoTime();

        scene.render(width, height, row, pixels);

        stop = System.nanoTime();
        time1 += stop - start;

        start = System.nanoTime();

        //BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < width; j++) {
            int k = row * width + j;
            img.setRGB(j, row, (int) (Math.round(pixels[k].getX() * 255) << 16 |
                    Math.round(pixels[k].getY() * 255) << 8 | Math.round(pixels[k].getZ() * 255)));
        }

        stop = System.nanoTime();
        time2 += stop - start;

        start = System.nanoTime();

        stop = System.nanoTime();
        time3 += stop - start;

        //printData(time1, time2, time3);
        return img;
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
        int xx = 72, yy = height - 72, l = 64;

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
        Panel panel = new Panel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        panel.requestFocusInWindow();
        frame.setResizable(false);
        //panel.render();
        frame.setVisible(true);
        panel.update();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Panel::createUI);
    }
}
