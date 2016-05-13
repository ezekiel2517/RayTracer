package raytracer;

//import com.google.gson.*;
import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;
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
                    realTimeMode = !realTimeMode;
                    if (!realTimeMode) {
                        renderIncrementally();
                        return;
                    }
                    break;
                case KeyEvent.VK_C:
                    camera.reset();
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

    Vec3D[][] pixels;
    int row;

    public Panel() {
        setPreferredSize(new Dimension(width, height));
        f.setMaximumFractionDigits(3);
        Listener listener = new Listener();
        addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);

        //Scene.writeScene(scene, "/home/piotr/scene.json");

//        GsonBuilder gb = new GsonBuilder();
//
//        gb.registerTypeAdapter(Object.class, (JsonSerializer<Object>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("sphere")){
//                return gson.toJsonTree(src, Sphere.class);
//            }
//            if(objectType.equals("plane")){
//                return gson.toJsonTree(src, Plane.class);
//            }
//            if(objectType.equals("triangle")){
//                return gson.toJsonTree(src, Triangle.class);
//            }
//            if(objectType.equals("triangleMesh")){
//                return gson.toJsonTree(src, TriangleMesh.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Texture.class, (JsonSerializer<Texture>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("checkerboard")){
//                return gson.toJsonTree(src, Checkerboard.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Light.class, (JsonSerializer<Light>) (src, type, jsonSerializationContext) -> {
//            Gson gson = gb.excludeFieldsWithoutExposeAnnotation().create();
//            String objectType = src.type;
//            if(objectType.equals("distantLight")){
//                return gson.toJsonTree(src, DistantLight.class);
//            }
//            if(objectType.equals("pointLight")){
//                return gson.toJsonTree(src, PointLight.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Object.class, (JsonDeserializer<Object>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("sphere")){
//                //return gson.fromJson(jsonElement, Sphere.class);
//                //return new Sphere((Matrix44D)data.get("objectToWorld"), (double)data.get("radius"), (Vec3D)data.get("albedo"), (Object.MaterialType) data.get("materialType"));
//                Sphere sphere = gson.fromJson(jsonElement, Sphere.class);
//                sphere.radius2 = sphere.radius * sphere.radius;
//                sphere.center = sphere.objectToWorld.multiplyPoint(new Vec3D());
//                sphere.worldToObject = sphere.objectToWorld.inverse();
//                return sphere;
//            }
//            if(objectType.equals("plane")){
//                return gson.fromJson(jsonElement, Plane.class);
//            }
//            if(objectType.equals("triangle")){
//                return gson.fromJson(jsonElement, Triangle.class);
//            }
//            if(objectType.equals("triangleMesh")){
//                return gson.fromJson(jsonElement, TriangleMesh.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Light.class, (JsonDeserializer<Light>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("distantLight")){
//                return gson.fromJson(jsonElement, DistantLight.class);
//            }
//            if(objectType.equals("pointLight")){
//                return gson.fromJson(jsonElement, PointLight.class);
//            }
//            return null;
//        });
//
//        gb.registerTypeAdapter(Texture.class, (JsonDeserializer<Texture>) (jsonElement, type, jsonDeserializationContext) -> {
//            Gson gson = new Gson();
//            HashMap data = gson.fromJson(jsonElement, HashMap.class);
//            java.lang.Object objectType = data.get("type");
//            if(objectType.equals("checkerboard")){
//                return gson.fromJson(jsonElement, Checkerboard.class);
//            }
//            return null;
//        });
//
//        Sphere sphere = new Sphere(new Matrix44D(), 1, new Vec3D(0.18, 0.18, 0.18), Object.MaterialType.PHONG);
//        //sphere.kd = 1;
//        //sphere.ks = 0;
//        sphere.texture = new Checkerboard(0, new Vec2D(1, 1), new Vec3D(1, 0, 1), new Vec3D(0, 1, 1));
//        Camera cam = new Camera();
//        cam.translate(new Vec3D(0, 0, 1), 4);
//        Scene scene1 = new Scene(new Object[] {sphere}, new Light[] {}, cam);
//        String json = gb.setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(scene1);
//        try (FileWriter writer = new FileWriter("/home/piotr/test.json")) {
//            writer.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Scene scene2 = null;
//        try (BufferedReader br = new BufferedReader(new FileReader("/home/piotr/test.json"))) {
//            scene2 = gb.create().fromJson(br, Scene.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assert scene2 != null;
//        this.scene = new ScalaScene(scene2.objects, scene2.lights, scene2.camera);
//        camera = scene2.camera;
        //System.out.println(camera.cameraToWorld);
        //System.out.println(((Sphere) this.scene.objects[0]).albedo);

//        String js = gb.setPrettyPrinting().create().toJson(new Scene(this.scene.objects, this.scene.lights, this.scene.camera));
//        try (FileWriter writer = new FileWriter("/home/piotr/test.json")) {
//            writer.write(js);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public void renderIncrementally() {
        for (int i = 0; i < height; i++) {
            pixels[i] = scene.render(width, height, i);
            row = i;
            paintImmediately(0, i, width, 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        long start = System.nanoTime();

        if (realTimeMode) pixels = scene.render(width, height, realTimeMode);

        long stop = System.nanoTime();
        long time = stop - start;
        //printData(time);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g.setColor(new Color(
                        (int) (pixels[i][j].getX() * 255),
                        (int) (pixels[i][j].getY() * 255),
                        (int) (pixels[i][j].getZ() * 255)));
                g.drawLine(j, i, j, i);
            }
        }

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
