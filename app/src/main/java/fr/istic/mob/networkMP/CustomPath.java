package fr.istic.mob.networkMP;

import android.graphics.Path;
import java.util.ArrayList;

public class CustomPath extends Path {

    private ArrayList<float[]> pathPoints = new ArrayList<float[]>();

    public CustomPath(){}

    public CustomPath(float xStart, float yStart, float xFinal, float yFinal){
        float[] tmp = new float[2];
        tmp[0] = xStart;
        tmp[1] = yStart;
        pathPoints.add(tmp);
        float[] tmp2 = new float[2];
        tmp2[0] = xFinal;
        tmp2[1] = yFinal;
        pathPoints.add(tmp2);
    }

    public void drawThisPath(){
        float[] initPoints = pathPoints.remove(0);
        this.moveTo(initPoints[0], initPoints[1]);
        for (float[] pointSet : pathPoints) {
            this.lineTo(pointSet[0], pointSet[1]);
        }
    }

    public void addPathPoints(float[] points) {
        this.pathPoints.add(points);
    }

    public ArrayList<float[]> getPathPoints() {
        return pathPoints;
    }
}
