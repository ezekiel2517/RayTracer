package raytracer;

import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 08.11.15.
 */
public class TriangleMesh extends Object {
    private int triangleIndex;
    private Vec2D uv = new Vec2D(0, 0);
    private int nTris;
    private Vec3D[] p;
    private int[] trisIndex;
    private Vec3D[] n;
    private Vec2D[] sts;
    public boolean smoothShading = true;
    private double triangleT;
    private double triangleU;
    private double triangleV;

    public static TriangleMesh createCuboid(double xDim, double yDim, double zDim, Matrix44D o2w, boolean out) {
        int[] faceIndex = new int[] {4, 4, 4, 4, 4, 4};
        int[] vertsIndex = out ? new int[] {
                0, 1, 2, 3,
                7, 6, 5, 4,
                0, 3, 7, 4,
                2, 1, 5, 6,
                1, 0, 4, 5,
                3, 2, 6, 7} : new int[] {
                3, 2, 1, 0,
                4, 5, 6, 7,
                4, 7, 3, 0,
                6, 5, 1, 2,
                5, 4, 0, 1,
                7, 6, 2, 3};
        double x = xDim * 0.5;
        double y = yDim * 0.5;
        double z = zDim * 0.5;
        Vec3D[] verts = new Vec3D[] {new Vec3D(-x, -y, z), new Vec3D(-x, -y, -z), new Vec3D(x, -y, -z), new Vec3D(x, -y, z),
                                     new Vec3D(-x, y, z), new Vec3D(-x, y, -z), new Vec3D(x, y, -z), new Vec3D(x, y, z)};
        Vec3D[] normals = new Vec3D[24];
        for (int i = 0; i < 24; i++) {
                normals[i] = new Vec3D(verts[vertsIndex[i]]).normalize();
        }
        Vec2D[] st = new Vec2D[24];
        for (int i = 0; i < 24; i++) {
            st[i] = new Vec2D(0, 0);
        }
        return new TriangleMesh(o2w, faceIndex, vertsIndex, verts, normals, st);
    }

    public TriangleMesh(Matrix44D objectToWorld, int[] faceIndex, int[] vertsIndex, Vec3D[] verts, Vec3D[] normals, Vec2D[] st) {
        super(objectToWorld);
        int nFaces = faceIndex.length;
        int k = 0, maxVertIndex = 0;

        for (int i = 0; i < nFaces; i++) {
            nTris += faceIndex[i] - 2;
            for (int j = 0; j < faceIndex[i]; j++) {
                if (vertsIndex[k + j] > maxVertIndex) {
                    maxVertIndex = vertsIndex[k + j];
                }
            }
            k += faceIndex[i];
        }
        maxVertIndex += 1;
        p = new Vec3D[maxVertIndex];
        for (int i = 0; i < maxVertIndex; i++) {
            p[i] = objectToWorld.multiplyPoint(verts[i]);
        }
        trisIndex = new int[nTris * 3];
        int l = 0;
        n = new Vec3D[nTris * 3];
        sts = new Vec2D[nTris * 3];
        Matrix44D transformNormals = worldToObject.transpose();
        k = 0;
        for (int i = 0; i < nFaces; i++) {
            for (int j = 0; j < faceIndex[i] - 2; j++) {
                trisIndex[l] = vertsIndex[k];
                trisIndex[l + 1] = vertsIndex[k + j + 1];
                trisIndex[l + 2] = vertsIndex[k + j + 2];
                n[l] = transformNormals.multiplyDirection(normals[k]).normalize();
                n[l + 1] = transformNormals.multiplyDirection(normals[k + j + 1]).normalize();
                n[l + 2] = transformNormals.multiplyDirection(normals[k + j + 2]).normalize();
                sts[l] = st[k];
                sts[l + 1] = st[k + j + 1];
                sts[l + 2] = st[k + j + 2];
                l += 3;
            }
            k += faceIndex[i];
        }
    }

    private boolean triangleIntersect(Ray ray, Vec3D v0, Vec3D v1, Vec3D v2) {
        triangleT = Double.POSITIVE_INFINITY;
        // Moller-Trumbore algorithm
        Vec3D v0v1 = v1.subtract(v0);
        Vec3D v0v2 = v2.subtract(v0);
        Vec3D pvec = ray.direction.crossProduct(v0v2);

        double det = v0v1.dotProduct(pvec);
        if (Options.culling)
            if (det < Options.kEpsilon)
                return false;
            else
            if (Math.abs(det) < Options.kEpsilon)
                return false;
        double invDet = 1 / det;
        Vec3D tvec = ray.origin.subtract(v0);
        triangleU = tvec.dotProduct(pvec) * invDet;
        if (triangleU < 0 || triangleU > 1)
            return false;
        Vec3D qvec = tvec.crossProduct(v0v1);
        triangleV = ray.direction.dotProduct(qvec) * invDet;
        if (triangleV < 0 || triangleU + triangleV > 1)
            return false;
        triangleT = v0v2.dotProduct(qvec) * invDet;
        return triangleT > 0;
    }

    @Override
    public Double intersect(Ray ray) {
        int j = 0;
        boolean isect = false;
        double tNear = Double.POSITIVE_INFINITY;
        for (int i = 0; i < nTris; i++) {
            Vec3D v0 = p[trisIndex[j]];
            Vec3D v1 = p[trisIndex[j + 1]];
            Vec3D v2 = p[trisIndex[j + 2]];
            if (triangleIntersect(ray, v0, v1, v2) && triangleT < tNear) {
                tNear = triangleT;
                uv.x = triangleU;
                uv.y = triangleV;
                triangleIndex = i;
                isect = true;
            }
            j += 3;
        }
        return isect ? tNear : null;
    }

    @Override
    public SurfaceProperties getSurfaceProperties(Vec3D hitPoint) {
        SurfaceProperties props = new SurfaceProperties();
        if (smoothShading) {
            Vec3D n0 = n[triangleIndex * 3];
            Vec3D n1 = n[triangleIndex * 3 + 1];
            Vec3D n2 = n[triangleIndex * 3 + 2];
            props.hitNormal = n0.multiply(1 - uv.x - uv.y).add(n1.multiply(uv.x)).add(n2.multiply(uv.y));
        }
        else {
            Vec3D v0 = p[trisIndex[triangleIndex * 3]];
            Vec3D v1 = p[trisIndex[triangleIndex * 3 + 1]];
            Vec3D v2 = p[trisIndex[triangleIndex * 3 + 2]];
            props.hitNormal = (v1.subtract(v0)).crossProduct(v2.subtract(v0));
        }

        props.hitNormal.normalize();

        Vec2D st0 = sts[triangleIndex * 3];
        Vec2D st1 = sts[triangleIndex * 3 + 1];
        Vec2D st2 = sts[triangleIndex * 3 + 2];
        props.hitTextureCoordinates = st0.multiply(1 - uv.x - uv.y).add(st1.multiply(uv.x)).add(st2.multiply(uv.y));

        return props;
    }
}
