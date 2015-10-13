package math;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Vec3DTest {

    private Vec3D v;

    @Before
    public void setUp() throws Exception {
        v = new Vec3D(3.0, -0.5, 1.25);
    }

    @Test
    public void addingVectorShouldReturnSum() throws Exception {
        Vec3D v1 = new Vec3D(0.0, -0.5, 1.0);
        Vec3D res = new Vec3D(v.getX() + v1.getX(), v.getY() + v1.getY(), v.getZ() + v1.getZ());
        assertEquals(res, v.add(v1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNullShouldThrowIllegalArgumentException() throws Exception {
        v.add(null);
    }

    @Test
    public void subtractingVectorShouldReturnDifference() throws Exception {
        Vec3D v1 = new Vec3D(0.0, -0.5, 1.0);
        Vec3D res = new Vec3D(v.getX() - v1.getX(), v.getY() - v1.getY(), v.getZ() - v1.getZ());
        assertEquals(res, v.subtract(v1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subtractingNullShouldThrowIllegalArgumentException() throws Exception {
        v.subtract(null);
    }

    @Test
    public void dotProductWithVectorShouldReturnDotProduct() throws Exception {
        Vec3D v1 = new Vec3D(0.0, -0.5, 1.0);
        double res = v.getX() * v1.getX() + v.getY() * v1.getY() + v.getZ() * v1.getZ();
        assertEquals(res, v.dotProduct(v1), 1e-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dotProductWithNullShouldThrowIllegalArgumentException() throws Exception {
        v.dotProduct(null);
    }

    @Test
    public void crossProductWithVectorShouldReturnCrossProduct() throws Exception {
        Vec3D v1 = new Vec3D(0.0, -0.5, 1.0);
        Vec3D res = new Vec3D(v.getY() * v1.getZ() - v.getZ() * v1.getY(),
                              v.getZ() * v1.getX() - v.getX() * v1.getZ(),
                              v.getX() * v1.getY() - v.getY() * v1.getX());
        assertEquals(res, v.crossProduct(v1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void crossProductWithNullShouldThrowIllegalArgumentException() throws Exception {
        v.crossProduct(null);
    }

    @Test
    public void lengthShouldReturnLength() throws Exception {
        double res = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
        assertEquals(res, v.length(), 1e-9);
    }

    @Test
    public void normalizingShouldNormalizeVector() throws Exception {
        double factor = 1.0 / Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
        Vec3D res = new Vec3D(v.getX() * factor, v.getY() * factor, v.getZ() * factor);
        v.normalize();
        assertEquals(res, v);
    }

    @Test(expected = ZeroVectorNormalizationException.class)
    public void normalizingZeroVectorShouldThrowZeroVectorNormalizationException() throws Exception {
        new Vec3D(0.0, 0.0, 0.0).normalize();
    }
}