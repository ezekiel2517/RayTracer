package raytracer;

import java.text.DecimalFormat;

/**
 * Created by piotr on 13.11.15.
 */
public class Stats {
    private int nPixels;
    private int nPrimaryRays;
    private int nShadowRays;
    private int nReflectionRays;
    private int nTransmissionRays;
    private int nTris;
    private int nObjects;
    private int nLights;

    public void setnPixels(int nPixels) {
        this.nPixels = nPixels;
    }

    public void setnPrimaryRays(int nPrimaryRays) {
        this.nPrimaryRays = nPrimaryRays;
    }

    public void addShadowRay() {
        nShadowRays++;
    }

    public void addReflectionRay() {
        nReflectionRays++;
    }

    public void addTransmissionRay() {
        nTransmissionRays++;
    }

    public void setnTris(int nTris) {
        this.nTris = nTris;
    }

    public void setnObjects(int nObjects) {
        this.nObjects = nObjects;
    }
    public void setnLights(int nLights) {
        this.nLights = nLights;
    }

    public void printStats() {
        int nRays = nPrimaryRays + nShadowRays + nReflectionRays + nTransmissionRays;
        DecimalFormat f = new DecimalFormat();
        f.setGroupingUsed(true);
        System.out.println("           #pixels: " + f.format(nPixels));
        System.out.println("             #rays: " + f.format(nRays));
        System.out.println("     #primary rays: " + f.format(nPrimaryRays));
        System.out.println("      #shadow rays: " + f.format(nShadowRays));
        System.out.println("  #reflection rays: " + f.format(nReflectionRays));
        System.out.println("#transmission rays: " + f.format(nTransmissionRays));
        System.out.println("        #triangles: " + f.format(nTris));
        System.out.println("          #objects: " + f.format(nObjects));
        System.out.println("           #lights: " + f.format(nLights));
    }
}
