package raytracer;

import math.Matrix44D;
import math.Vec2D;
import math.Vec3D;

/**
 * Created by piotr on 08.11.15.
 */
public class TriangleMesh extends Object {
    public int triangleIndex;
    public Vec2D uv = new Vec2D(0, 0);
    public int nTris;
    public Vec3D[] p;
    public int[] trisIndex;
    public Vec3D[] n;
    public Vec2D[] sts;
    public boolean smoothShading = true;
    public double triangleT;
    public double triangleU;
    public double triangleV;
    public Object boundingVolume;

    public int getNTris() {
        return nTris;
    }

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
        Vec3D[] normals = out ? new Vec3D[] {
                new Vec3D(0, -1, 0), new Vec3D(0, -1, 0), new Vec3D(0, -1, 0), new Vec3D(0, -1, 0),
                new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0),
                new Vec3D(0, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 1),
                new Vec3D(0, 0, -1), new Vec3D(0, 0, -1), new Vec3D(0, 0, -1), new Vec3D(0, 0, -1),
                new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0),
                new Vec3D(1, 0, 0), new Vec3D(1, 0, 0), new Vec3D(1, 0, 0), new Vec3D(1, 0, 0)
        } : new Vec3D[] {
                new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0),
                new Vec3D(0, -1, 0), new Vec3D(0, -1, 0), new Vec3D(0, -1, 0), new Vec3D(0, -1, 0),
                new Vec3D(0, 0, -1), new Vec3D(0, 0, -1), new Vec3D(0, 0, -1), new Vec3D(0, 0, -1),
                new Vec3D(0, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 1),
                new Vec3D(1, 0, 0), new Vec3D(1, 0, 0), new Vec3D(1, 0, 0), new Vec3D(1, 0, 0),
                new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0), new Vec3D(-1, 0, 0)
        };
        Vec2D[] st = new Vec2D[24];
        for (int i = 0; i < 24; i += 4) {
            st[i] = new Vec2D(0, 0);
            st[i + 1] = new Vec2D(1, 0);
            st[i + 2] = new Vec2D(1, 1);
            st[i + 3] = new Vec2D(0, 1);
        }
        return new TriangleMesh(o2w, faceIndex, vertsIndex, verts, normals, st);
    }

    public static TriangleMesh createCylinder(double R, double r, double height, int nSides, int nDivs) {
        int nPoints = (nDivs + 1) * nSides;
        int nVerts = (2 + 4 * nDivs) * nSides;
        int nPolys = nDivs * nSides + 2;
        Vec3D[] p = new Vec3D[nPoints];
        Vec3D[] n = new Vec3D[nVerts];
        Vec2D[] st = new Vec2D[nVerts];
        Vec3D[] norm = new Vec3D[nSides];
        int[] faceIndex = new int[nPolys];
        int[] vertsIndex = new int[nVerts];
        double da = 2 * Math.PI / nSides;
        double a = -0.05 * Math.PI;
        double dr = (R - r) / nDivs;
        double dh = height / nDivs;
        for (int i = 0; i < nSides; i++, a += da) {
            double x = Math.cos(a);
            double z = -Math.sin(a);
            double rr = r;
            double hh = 0.5 * height;
            for (int j = 0; j <= nDivs; j++, rr += dr, hh -= dh) {
                p[(nDivs + 1) * i + j] = new Vec3D(rr * x, hh, rr * z);
            }
            //p[2 * i] = new Vec3D(r * x, 0.5 * height, r * z);
            vertsIndex[i] = (nDivs + 1) * i;
            //p[2 * i + 1] = new Vec3D(R * x, -0.5 * height, R * z);
            vertsIndex[2 * nSides - 1 - i] = (nDivs + 1) * i + nDivs;
            Vec3D tangent = p[(nDivs + 1) * i].subtract(p[(nDivs + 1) * i + nDivs]);
            Vec3D bitangent = new Vec3D(x, 0, z).crossProduct(new Vec3D(0, -1, 0));
            norm[i] = bitangent.crossProduct(tangent);
            n[i] = new Vec3D(0, 1, 0);
            n[2 * nSides - 1 - i] = new Vec3D(0, -1, 0);
            st[i] = new Vec2D();
            st[2 * nSides - 1 - i] = new Vec2D();
        }

        faceIndex[0] = faceIndex[1] = nSides;

        for (int i = 0; i < nSides; i++) {
            for (int j = 0; j < nDivs; j++) {
                faceIndex[i * nDivs + j + 2] = 4;
                int idx = 2 * nSides + 4 * (i * nDivs + j);
                //System.out.print(idx + " ");
                int k = (nDivs + 1) * i + j;
                vertsIndex[idx] = k;
                vertsIndex[idx + 1] = k + 1;
                vertsIndex[idx + 2] = ((nDivs + 1) * i + nDivs + 1) % nPoints + j + 1;
                vertsIndex[idx + 3] = ((nDivs + 1) * i + nDivs + 1) % nPoints + j;
                n[idx] = new Vec3D(norm[i]);
                n[idx + 1] = new Vec3D(norm[i]);
                n[idx + 2] = new Vec3D(norm[(i + 1) % nSides]);
                n[idx + 3] = new Vec3D(norm[(i + 1) % nSides]);
                st[idx] = new Vec2D();
                st[idx + 1] = new Vec2D();
                st[idx + 2] = new Vec2D();
                st[idx + 3] = new Vec2D();
            }
        }

        //for (Vec3D v : n) System.out.println(v + " ");


        return new TriangleMesh(new Matrix44D().rotatedZ(0).scaled(new Vec3D(2, 1, 1)).translated(new Vec3D(0, 0, 0)), faceIndex, vertsIndex, p, n, st);
    }

    public static TriangleMesh createCone(double r, double height, int nSides, int nDivs) {
        int nPoints = 1 + nDivs * nSides;
        int nVerts = 3 * nSides + 4 * (nDivs - 1) * nSides + nSides;
        int nPolys = nDivs * nSides + 1;
        Vec3D[] p = new Vec3D[nPoints];
        Vec3D[] n = new Vec3D[nVerts];
        Vec2D[] st = new Vec2D[nVerts];
        Vec3D[] norm = new Vec3D[2 * nSides];
        int[] faceIndex = new int[nPolys];
        int[] vertsIndex = new int[nVerts];
        double da = 2 * Math.PI / nSides;
        double a = -0.25 * Math.PI;
        double dr = r / nDivs;
        double dh = height / nDivs;
        p[0] = new Vec3D(0, 0.5 * height, 0);
        for (int i = 0; i < nSides; i++, a += da) {
            double x = Math.cos(a);
            double z = -Math.sin(a);
            double rr = dr;
            double hh = 0.5 * height - dh;
            for (int j = 0; j < nDivs; j++, rr += dr, hh -= dh) {
                p[1 + nDivs * i + j] = new Vec3D(rr * x, hh, rr * z);
            }
            //p[2 * i] = new Vec3D(r * x, 0.5 * height, r * z);
            //p[2 * i + 1] = new Vec3D(R * x, -0.5 * height, R * z);
            vertsIndex[nSides - 1 - i] = 1 + nDivs * i + nDivs - 1;

            Vec3D tangent = p[0].subtract(p[1 + nDivs * i + nDivs - 1]);
            Vec3D bitangent = new Vec3D(x, 0, z).crossProduct(new Vec3D(0, -1, 0));
            norm[2 * i] = bitangent.crossProduct(tangent);

            double x1 = Math.cos(a + 0.5 * da);
            double z1 = -Math.sin(a + 0.5 * da);
            Vec3D tangent1 = p[0].subtract(new Vec3D(r * x1, -0.5 * height, r * z1));
            Vec3D bitangent1 = new Vec3D(x1, 0, z1).crossProduct(new Vec3D(0, -1, 0));
            norm[2 * i + 1] = bitangent1.crossProduct(tangent1);

            n[nSides - 1 - i] = new Vec3D(0, -1, 0);
            st[nSides - 1 - i] = new Vec2D();
        }

        faceIndex[0] = nSides;

        for (int i = 0; i < nSides; i++) {
            faceIndex[1 + i * nDivs] = 3;
            int idx = nSides + 3 * i + 4 * i * (nDivs - 1);
            //System.out.print(idx + " ");
            vertsIndex[idx] = 0;
            vertsIndex[idx + 1] = 1 + nDivs * i;
            vertsIndex[idx + 2] = 1 + (nDivs * i + nDivs) % (nDivs * nSides);
            n[idx] = new Vec3D(norm[2 * i + 1]);
            n[idx + 1] = new Vec3D(norm[2 * i]);
            n[idx + 2] = new Vec3D(norm[(2 * i + 2) % (2 * nSides)]);
            st[idx] = new Vec2D();
            st[idx + 1] = new Vec2D();
            st[idx + 2] = new Vec2D();
            idx += 3;
            for (int j = 0; j < nDivs - 1; j++) {
                faceIndex[1 + i * nDivs + 1 + j] = 4;
                //System.out.print(idx + " ");
                int k = 1 + nDivs * i + j;
                vertsIndex[idx] = k;
                vertsIndex[idx + 1] = k + 1;
                vertsIndex[idx + 2] = 1 + (nDivs * i + nDivs) % (nDivs * nSides) + j + 1;
                vertsIndex[idx + 3] = 1 + (nDivs * i + nDivs) % (nDivs * nSides) + j;
                n[idx] = new Vec3D(norm[2 * i]);
                n[idx + 1] = new Vec3D(norm[2 * i]);
                n[idx + 2] = new Vec3D(norm[(2 * i + 2) % (2 * nSides)]);
                n[idx + 3] = new Vec3D(norm[(2 * i + 2) % (2 * nSides)]);
                st[idx] = new Vec2D();
                st[idx + 1] = new Vec2D();
                st[idx + 2] = new Vec2D();
                st[idx + 3] = new Vec2D();
                idx += 4;
            }
        }

        //for (Vec3D v : n) System.out.println(v + " ");


        return new TriangleMesh(new Matrix44D().rotatedZ(0).scaled(new Vec3D(1, 1, 1)).translated(new Vec3D(0, 0, 0)), faceIndex, vertsIndex, p, n, st);
    }

    public static TriangleMesh createPolySphere(double rad, int nDivs) {
        int nPoints = (nDivs - 1) * nDivs + 2;
        int nVerts = (6 + (nDivs - 1) * 4) * nDivs;
        Vec3D[] p = new Vec3D[nPoints];
        Vec3D[] n = new Vec3D[nVerts];
        Vec2D[] st = new Vec2D[nVerts];

        double u = -Math.PI * 0.5;
        double v;// = -Math.PI;
        double du = Math.PI / nDivs;
        double dv = 2 * Math.PI / nDivs;

//        Vec2D[] tex = new Vec2D[nVerts];

        p[0] = new Vec3D(0, -rad, 0);
        //n[0] = new Vec3D(0, -rad, 0);
        //tex[0] = new Vec2D(0,0);
        int k = 1;
        for (int i = 0; i < nDivs - 1; i++) {
            u += du;
            v = -Math.PI;
            for (int j = 0; j < nDivs; j++) {
                double x = rad * Math.cos(u) * Math.cos(v);
                double y = rad * Math.sin(u);
                double z = rad * Math.cos(u) * Math.sin(v);
                p[k] = new Vec3D(x, y, z);
                //n[k] = new Vec3D(x, y, z);
//                tex[k] = new Vec2D(0, 0);
//                tex[k].x = u / Math.PI + 0.5;
//                tex[k].y = v * 0.5 / Math.PI + 0.5;
                v += dv;
                k++;
            }
        }
        p[k] = new Vec3D(0, rad, 0);
        //n[k] = new Vec3D(0, rad, 0);
        //tex[k] = new Vec2D(0,0);

        int nPolys = nDivs * nDivs;
        int[] faceIndex = new int[nPolys];
        int[] vertsIndex = new int[nVerts];

// create the connectivity lists
        int vid = 1, numV = 0, l = 0;
        k = 0;
        for (int i = 0; i < nDivs; i++) {
            for (int j = 0; j < nDivs; j++) {
                if (i == 0) {
                    faceIndex[k++] = 3;
                    vertsIndex[l] = 0;
                    vertsIndex[l + 1] = j + vid;
                    vertsIndex[l + 2] = (j == (nDivs - 1)) ? vid : j + vid + 1;
                    n[l] = new Vec3D(p[vertsIndex[l]]);
                    n[l+1] = new Vec3D(p[vertsIndex[l+1]]);
                    n[l+2] = new Vec3D(p[vertsIndex[l+2]]);
                    st[l] = new Vec2D(); //tex[vertsIndex[l]];
                    st[l+1] = new Vec2D();//tex[vertsIndex[l + 1]];
                    st[l+2] = new Vec2D();//tex[vertsIndex[l + 2]];
                    l += 3;
                }
                else if (i == (nDivs - 1)) {
                    faceIndex[k++] = 3;
                    vertsIndex[l] = j + vid + 1 - nDivs;
                    vertsIndex[l + 1] = vid + 1;
                    vertsIndex[l + 2] = (j == (nDivs - 1)) ? vid + 1 - nDivs : j + vid + 2 - nDivs;
                    n[l] = new Vec3D(p[vertsIndex[l]]);
                    n[l+1] = new Vec3D(p[vertsIndex[l+1]]);
                    n[l+2] = new Vec3D(p[vertsIndex[l+2]]);
                    st[l] = new Vec2D();//tex[vertsIndex[l]];
                    st[l+1] = new Vec2D();//tex[vertsIndex[l + 1]];
                    st[l+2] = new Vec2D();//tex[vertsIndex[l + 2]];
                    l += 3;
                }
                else {
                    faceIndex[k++] = 4;
                    vertsIndex[l] = j + vid + 1 - nDivs;
                    vertsIndex[l + 1] = j + vid + 1;
                    vertsIndex[l + 2] = (j == (nDivs - 1)) ? vid + 1 : j + vid + 2;
                    vertsIndex[l + 3] = (j == (nDivs - 1)) ? vid + 1 - nDivs : j + vid + 2 - nDivs;
                    n[l] = new Vec3D(p[vertsIndex[l]]);
                    n[l+1] = new Vec3D(p[vertsIndex[l+1]]);
                    n[l+2] = new Vec3D(p[vertsIndex[l+2]]);
                    n[l+3] = new Vec3D(p[vertsIndex[l+3]]);
                    st[l] = new Vec2D();//tex[vertsIndex[l]];
                    st[l+1] = new Vec2D();//tex[vertsIndex[l + 1]];
                    st[l+2] = new Vec2D();//tex[vertsIndex[l + 2]];
                    st[l+3] = new Vec2D();//tex[vertsIndex[l + 3]];
                    l += 4;
                }
                numV++;
            }
            vid = numV;
        }

        return new TriangleMesh(new Matrix44D().scaled(new Vec3D(4, 0.5, 2)), faceIndex, vertsIndex, p, n, st);
    }

    public static TriangleMesh createPlane(Matrix44D o2w) {
        int[] faceIndex = new int[] {4};
        int[] vertsIndex = new int[] {0, 1, 2, 3};
        Vec3D[] verts = new Vec3D[] {new Vec3D(-1, 0, 1), new Vec3D(1, 0, 1), new Vec3D(1, 0, -1), new Vec3D(-1, 0, -1)};
        Vec3D[] normals = new Vec3D[] {new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0), new Vec3D(0, 1, 0)};
        Vec2D[] st = new Vec2D[] {new Vec2D(0, 0), new Vec2D(1, 0), new Vec2D(1, 1), new Vec2D(0, 1)};
        return new TriangleMesh(o2w, faceIndex, vertsIndex, verts, normals, st);
    }

    public TriangleMesh(Matrix44D objectToWorld, int[] faceIndex, int[] vertsIndex, Vec3D[] verts, Vec3D[] normals, Vec2D[] st) {
        super(objectToWorld);
//        int nFaces = faceIndex.length;
//        int k = 0, maxVertIndex = 0;

        for (int i = 0; i < faceIndex.length; i++) {
            nTris += faceIndex[i] - 2;
//            for (int j = 0; j < faceIndex[i]; j++) {
//                if (vertsIndex[k + j] > maxVertIndex) {
//                    maxVertIndex = vertsIndex[k + j];
//                }
//            }
//            k += faceIndex[i];
        }
//        maxVertIndex += 1;
        p = new Vec3D[verts.length];

        for (int i = 0; i < p.length; i++) {
            p[i] = objectToWorld.multiplyPoint(verts[i]);
        }
        trisIndex = new int[nTris * 3];
        int l = 0;
        n = new Vec3D[nTris * 3];
        sts = new Vec2D[nTris * 3];
        Matrix44D transformNormals = worldToObject.transpose();

        int k = 0;
        for (int i = 0; i < faceIndex.length; i++) { // for each face
            for (int j = 0; j < faceIndex[i] - 2; j++) { // for each triangle
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
        type = "triangleMesh";
        //for (Vec3D v : n) System.out.println(v + " ");
        //System.out.print(transformNormals);
    }

    private boolean triangleIntersect(Ray ray, Vec3D v0, Vec3D v1, Vec3D v2) {
        //triangleT = Double.POSITIVE_INFINITY;
        // Moller-Trumbore algorithm
        Vec3D v0v1 = v1.subtract(v0);
        Vec3D v0v2 = v2.subtract(v0);
        Vec3D pvec = ray.direction.crossProduct(v0v2);
        double det = v0v1.dotProduct(pvec);
        if (Options.culling) {
            if (det < Options.kEpsilon) {
                return false;
            }
        } else {
            if (Math.abs(det) < Options.kEpsilon) {
                return false;
            }
        }
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
        //return true;
    }

    @Override
    public Double intersect(Ray ray) {
        if (boundingVolume != null && boundingVolume.intersect(ray) == null)
            return null;
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
            //System.out.print(triangleIndex + " ");
            Vec3D v0 = p[trisIndex[triangleIndex * 3]];
            Vec3D v1 = p[trisIndex[triangleIndex * 3 + 1]];
            Vec3D v2 = p[trisIndex[triangleIndex * 3 + 2]];
            props.hitNormal = v1.subtract(v0).crossProduct(v2.subtract(v0));
        }

        props.hitNormal.normalize();

        Vec2D st0 = sts[triangleIndex * 3];
        Vec2D st1 = sts[triangleIndex * 3 + 1];
        Vec2D st2 = sts[triangleIndex * 3 + 2];
        props.hitTextureCoordinates = st0.multiply(1 - uv.x - uv.y).add(st1.multiply(uv.x)).add(st2.multiply(uv.y));

        return props;
    }
}
