package math;

public class Matrix44D {
    private double[][] c;

    public Matrix44D(double c00, double c01, double c02, double c03,
                     double c10, double c11, double c12, double c13,
                     double c20, double c21, double c22, double c23,
                     double c30, double c31, double c32, double c33) {
        c = new double[][] {{c00, c01, c02, c03}, {c10, c11, c12, c13}, {c20, c21, c22, c23}, {c30, c31, c32, c33}};
    }

    public Matrix44D() {
        c = new double[][] {{1.0, 0.0, 0.0, 0.0}, {0.0, 1.0, 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}, {0.0, 0.0, 0.0, 1.0}};
    }

    public Matrix44D(Matrix44D m) {
        c = new double[4][4];
        for (int row = 0; row < 4; row++) {
            System.arraycopy(m.c[row], 0, c[row], 0, 4);
        }
    }

    public double get(int row, int col) {
        return c[row][col];
    }

    public void set(int row, int col, double val) {
        c[row][col] = val;
    }

    public Matrix44D multiply(Matrix44D m) {
        Matrix44D res = new Matrix44D();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double val = 0.0;
                for (int i = 0; i < 4; i++) {
                    val += c[row][i] * m.get(i, col);
                }
                res.set(row, col, val);
            }
        }
        return res;
    }

    @Deprecated
    public Matrix44D translate(double x, double y, double z) {
        return this.multiply(new Matrix44D(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, y, z, 1));
    }

    @Deprecated
    public Matrix44D rotate(double x, double y, double z) {
        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);
        return this
                .multiply(new Matrix44D(1, 0, 0, 0, 0, Math.cos(x), Math.sin(x), 0, 0, -Math.sin(x), Math.cos(x), 0, 0, 0, 0, 1))
                .multiply(new Matrix44D(Math.cos(y), 0, -Math.sin(y), 0, 0, 1, 0, 0, Math.sin(y), 0, Math.cos(y), 0, 0, 0, 0, 1))
                .multiply(new Matrix44D(Math.cos(z), Math.sin(z), 0, 0, -Math.sin(z), Math.cos(z), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1));
    }

    public Matrix44D translated(Vec3D v) {
        Matrix44D m = new Matrix44D(this);
        m.c[3][0] += v.getX();
        m.c[3][1] += v.getY();
        m.c[3][2] += v.getZ();
        return m;
    }

    public Matrix44D rotatedX(double degrees) {
        double a = Math.toRadians(degrees);
        return multiply(new Matrix44D(1, 0, 0, 0, 0, Math.cos(a), Math.sin(a), 0, 0, -Math.sin(a), Math.cos(a), 0, 0, 0, 0, 1));
    }

    public Matrix44D rotatedY(double degrees) {
        double a = Math.toRadians(degrees);
        return multiply(new Matrix44D(Math.cos(a), 0, -Math.sin(a), 0, 0, 1, 0, 0, Math.sin(a), 0, Math.cos(a), 0, 0, 0, 0, 1));
    }

    public Matrix44D rotatedZ(double degrees) {
        double a = Math.toRadians(degrees);
        return multiply(new Matrix44D(Math.cos(a), Math.sin(a), 0, 0, -Math.sin(a), Math.cos(a), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1));
    }

    public Matrix44D scaled(Vec3D v) {
        Matrix44D m = new Matrix44D(this);
        m.c[0][0] *= v.getX();
        m.c[1][1] *= v.getY();
        m.c[2][2] *= v.getZ();
        return m;
    }

    public Matrix44D transpose() {
        Matrix44D res = new Matrix44D();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                res.set(row, col, c[col][row]);
            }
        }
        return res;
    }

    public Vec3D multiplyPoint(Vec3D p) {
        return new Vec3D(
                p.getX() * c[0][0] + p.getY() * c[1][0] + p.getZ() * c[2][0] + c[3][0],
                p.getX() * c[0][1] + p.getY() * c[1][1] + p.getZ() * c[2][1] + c[3][1],
                p.getX() * c[0][2] + p.getY() * c[1][2] + p.getZ() * c[2][2] + c[3][2]
        );
    }

    public Vec3D multiplyDirection(Vec3D d) {
        return new Vec3D(
                d.getX() * c[0][0] + d.getY() * c[1][0] + d.getZ() * c[2][0],
                d.getX() * c[0][1] + d.getY() * c[1][1] + d.getZ() * c[2][1],
                d.getX() * c[0][2] + d.getY() * c[1][2] + d.getZ() * c[2][2]
        );
    }

    public Matrix44D inverse() {
        Matrix44D s = new Matrix44D();
        Matrix44D t = new Matrix44D(this);
        for (int i = 0; i < 3; i++) {
            int pivot = i;
            double pivotSize = t.get(i, i);
            if (pivotSize < 0)
                pivotSize = -pivotSize;
            for (int j = i + 1; j < 4; j++) {
                double tmp = t.get(j, i);
                if (tmp < 0)
                    tmp = -tmp;
                if (tmp > pivotSize) {
                    pivot = j;
                    pivotSize = tmp;
                }
            }
            if (pivotSize == 0)
                return new Matrix44D();
            if (pivot != i) {
                for (int j = 0; j < 4; j++) {
                    double tmp = t.get(i, j);
                    t.set(i, j, t.get(pivot, j));
                    t.set(pivot, j, tmp);

                    tmp = s.get(i, j);
                    s.set(i, j, s.get(pivot, j));
                    s.set(pivot, j, tmp);
                }
            }

            for (int j = i + 1; j < 4; j++) {
                double f = t.get(j, i) / t.get(i, i);

                for (int k = 0; k < 4; k++) {
                    t.set(j, k, t.get(j, k) - f * t.get(i, k));
                    s.set(j, k, s.get(j, k) - f * s.get(i, k));
                }
            }
        }

        for (int i = 3; i >= 0; --i) {
            double f;

            if ((f = t.get(i, i)) == 0) {
                return new Matrix44D();
            }

            for (int j = 0; j < 4; j++) {
                t.set(i, j, t.get(i, j) / f);
                s.set(i, j, s.get(i, j) / f);
            }

            for (int j = 0; j < i; j++) {
                f = t.get(j, i);

                for (int k = 0; k < 4; k++) {
                    t.set(j, k, t.get(j, k) - f * t.get(i, k));
                    s.set(j, k, s.get(j, k) - f * s.get(i, k));
                }
            }
        }

        return s;
    }

    @Override
    public String toString() {
        return
                "[" + c[0][0] + "\t" + c[0][1] + "\t" + c[0][2] + "\t" + c[0][3] + "\n" +
                " " + c[1][0] + "\t" + c[1][1] + "\t" + c[1][2] + "\t" + c[1][3] + "\n" +
                " " + c[1][0] + "\t" + c[2][1] + "\t" + c[2][2] + "\t" + c[2][3] + "\n" +
                " " + c[3][0] + "\t" + c[3][1] + "\t" + c[3][2] + "\t" + c[3][3] + "]";
    }
}
