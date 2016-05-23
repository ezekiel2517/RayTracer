package raytracer;

import math.Vec2D;
import math.Vec3D;

import java.util.ArrayList;

public class RayTreesArray {
    class Node {
        private Vec3D factor, element, hitPoint, hitNormal, Nt, Nb;
        private ArrayList<Node> children;
        private ArrayList<Double> r;

        public Node() {
            children = new ArrayList<>();
            r = new ArrayList<>();
        }

        public void setInvariants(Vec3D hitPoint, Vec3D hitNormal, Vec3D Nt, Vec3D Nb,
                                  Vec3D objectColor, Vec3D directDiffuse, Vec3D specular, double kd, double ks) {
            factor = objectColor.multiply(2 * kd);
            element = directDiffuse.multiply(objectColor).multiply(kd / Math.PI).add(specular.multiply(ks));
            this.hitPoint = hitPoint;
            this.hitNormal = hitNormal;
            this.Nt = Nt;
            this.Nb = Nb;
        }

        public void addChild(Node child, double r) {
            children.add(child);
            this.r.add(r);
        }

        public Vec3D update(int depth) {
            if (element == null) {
                return new Vec3D();
            }

            if (depth == scene.maxDepth) {
                return element;
            }

            Vec3D indirectDiffuse = new Vec3D();

            for (int i = 0; i < children.size(); i++) {
                indirectDiffuse = indirectDiffuse.add(children.get(i).update(depth + 1).multiply(r.get(i)));
            }

            //Vec3D Nt = new Vec3D(), Nb = new Vec3D();
            //scene.createCoordinateSystem(hitNormal, Nt, Nb);
            double r1 = scene.generator.nextDouble();
            double r2 = scene.generator.nextDouble();
            Vec3D sample = scene.uniformSampleHemisphere(r1, r2);
            Vec3D sampleWorld = new Vec3D(
                    sample.getX() * Nb.getX() + sample.getY() * hitNormal.getX() + sample.getZ() * Nt.getX(),
                    sample.getX() * Nb.getY() + sample.getY() * hitNormal.getY() + sample.getZ() * Nt.getY(),
                    sample.getX() * Nb.getZ() + sample.getY() * hitNormal.getZ() + sample.getZ() * Nt.getZ());

            Node node = new Node();
            addChild(node, r1);

            Ray ray = new Ray(hitPoint.add(sampleWorld.multiply(scene.bias)), sampleWorld, Ray.RayType.OTHER);
            indirectDiffuse = indirectDiffuse.add(scene.castRay(ray, depth + 1, new Vec2D(), node).multiply(r1));

            return indirectDiffuse.multiply(1.0 / getnSamples()).multiply(factor).add(element);
        }
    }

    private Node[] array;
    private int nSamples, width, raysPerPixelLength;
    private Scene scene;

    public RayTreesArray(int width, int height, int raysPerPixelLength, Scene scene) {
        array = new Node[width * height * raysPerPixelLength * raysPerPixelLength];
        nSamples = 1;
        this.width = width;
        this.raysPerPixelLength = raysPerPixelLength;
        this.scene = scene;
    }

    public void addNode(int col, int row, int c, int r) {
        array[(row * raysPerPixelLength + r) * width * raysPerPixelLength + col * raysPerPixelLength + c] = new Node();
    }

    public Node getNode(int col, int row, int c, int r) {
        return array[(row * raysPerPixelLength + r) * width * raysPerPixelLength + col * raysPerPixelLength + c];
    }

    public void reset() {
        nSamples = 1;
    }

    public boolean isReset() {
        return nSamples == 1;
    }

    public Vec3D update(int col, int row) {
        Vec3D color = new Vec3D();
        for (int r = 0; r < raysPerPixelLength; r++) {
            for (int c = 0; c < raysPerPixelLength; c++) {
                color = color.add(getNode(col, row, c, r).update(0));
            }
        }
        return color.multiply(1.0 / (raysPerPixelLength * raysPerPixelLength));
    }

    public int getnSamples() {
        return nSamples;
    }

    public void incrnSamples() {
        nSamples++;
    }
}
