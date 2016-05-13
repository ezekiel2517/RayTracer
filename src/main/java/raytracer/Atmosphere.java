package raytracer;

import math.QuadraticRoots;
import math.Utils;
import math.Vec3D;

public class Atmosphere {

    Vec3D rayleighCoeff = new Vec3D(/*19.918e-6, 13.57e-6, 5.75e-6);*/5.5e-6, 13.0e-6, 22.4e-6);
    Vec3D mieCoeff = new Vec3D(21e-6, 21e-6, 21e-6);
    double rayleighScaleHeight = 7994;
    double mieScaleHeight = 1200;
    double earthRadius = 6360e3;
    double atmosphereRadius = 6420e3;
    Vec3D sunDirection = new Vec3D(0, 1, 0).normalize();
    double sunIntensity = 4;
    double meanCosine = 0.76;
    double t0, t1;
    Vec3D earthPos = new Vec3D(0, 0, 0);

    public Vec3D computeIncidentLight(Ray ray, Vec3D atmosphereColor, Double tmax) {
        if (!intersect(ray)) {
            atmosphereColor.setX(0);
            atmosphereColor.setY(0);
            atmosphereColor.setZ(0);
            return new Vec3D(1, 1, 1);
        }
        double tmin = Math.max(0, t0);
        if (t1 < tmax) tmax = t1;
        int nSamples = 16;
        int nSamplesLight = 8;
        double segmentLength = (tmax - tmin) / nSamples;
        double tCurrent = tmin;
        Vec3D sumR = new Vec3D(), sumM = new Vec3D();
        double opticalDepthR = 0, opticalDepthM = 0;
        double mu = ray.direction.dotProduct(sunDirection);
        double phaseR = 3 / (16 * Math.PI) * (1 + mu * mu);
        double g = meanCosine;
        double phaseM = 3 / (8 * Math.PI) * ((1 - g * g) * (1 + mu * mu))/((2 + g * g) * Math.pow(1 + g * g - 2 * g * mu, 1.5));

        for (int i = 0; i < nSamples; i++) {
            Vec3D samplePosition = ray.origin.add(ray.direction.multiply(tCurrent + 0.5 * segmentLength));
            double height = samplePosition.subtract(earthPos).length() - earthRadius;
            // compute optical depth for light
            double hr = Math.exp(-height / rayleighScaleHeight) * segmentLength;
            double hm = Math.exp(-height / mieScaleHeight) * segmentLength;
            opticalDepthR += hr;
            opticalDepthM += hm;
            // light optical depth
            Ray lightRay = new Ray(samplePosition, sunDirection, Ray.RayType.OTHER);
            intersect(lightRay);
            double segmentLengthLight = t1 / nSamplesLight, tCurrentLight = 0;
            double opticalDepthLightR = 0, opticalDepthLightM = 0;
            int j = 0;
            for (; j < nSamplesLight; j++) {
                Vec3D samplePositionLight = lightRay.origin.add(lightRay.direction.multiply(tCurrentLight + 0.5 * segmentLengthLight));
                double heightLight = samplePositionLight.subtract(earthPos).length() - earthRadius;
                if (heightLight < 0) break;
                opticalDepthLightR += Math.exp(-heightLight / rayleighScaleHeight) * segmentLengthLight;
                opticalDepthLightM += Math.exp(-heightLight / mieScaleHeight) * segmentLengthLight;
                tCurrentLight += segmentLengthLight;
            }
            if (j == nSamplesLight) {
                Vec3D tau = rayleighCoeff.multiply(opticalDepthR + opticalDepthLightR).add(mieCoeff.multiply(1.1 * (opticalDepthM + opticalDepthLightM)));
                Vec3D attenuation = new Vec3D(Math.exp(-tau.getX()), Math.exp(-tau.getY()), Math.exp(-tau.getZ()));
                sumR = sumR.add(attenuation.multiply(rayleighScaleHeight));
                sumM = sumM.add(attenuation.multiply(mieScaleHeight));
            }
            tCurrent += segmentLength;
        }

        Vec3D col = (sumR.multiply(rayleighCoeff).multiply(phaseR).add(sumM.multiply(mieCoeff).multiply(phaseM))).multiply(sunIntensity);
        atmosphereColor.setX(col.getX());
        atmosphereColor.setY(col.getY());
        atmosphereColor.setZ(col.getZ());
        return new Vec3D(Math.exp(-(rayleighCoeff.getX() + mieCoeff.getX() * 1.1) * (tmax - tmin)),
                Math.exp(-(rayleighCoeff.getY() + mieCoeff.getY() * 1.1) * (tmax - tmin)),
                Math.exp(-(rayleighCoeff.getZ() + mieCoeff.getZ() * 1.1) * (tmax - tmin)));
    }

    private boolean intersect(Ray ray) {
        Vec3D center = new Vec3D();
        Vec3D l = ray.origin.subtract(center);
        double a = ray.direction.dotProduct(ray.direction);
        double b = 2 * ray.direction.dotProduct(l);
        double c = l.dotProduct(l) - atmosphereRadius * atmosphereRadius;
        QuadraticRoots roots = Utils.solveQuadratic(a, b, c);
        if (roots == null) return false;
        if (roots.x0 > roots.x1) {
            t0 = roots.x1;
            t1 = roots.x0;
        } else {
            t0 = roots.x0;
            t1 = roots.x1;
        }
        return t1 >= 0;
    }
}
