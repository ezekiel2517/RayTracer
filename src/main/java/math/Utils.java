package math;

public class Utils {

    public static QuadraticRoots solveQuadratic(double a, double b, double c) {
        double discr = b * b - 4 * a * c;
        if (discr < 0) return null;
        if (discr == 0)
            return new QuadraticRoots(-0.5 * b / a);
        double q = b > 0 ? -0.5 * (b + Math.sqrt(discr)) :  -0.5 * (b - Math.sqrt(discr));
        return new QuadraticRoots(q / a, c / q);
    }

    public static double clamp(double lo, double hi, double v) {
        return Math.max(lo, Math.min(hi, v));
    }

    public static double modulo(double f) {
        return f - Math.floor(f);
    }
}
