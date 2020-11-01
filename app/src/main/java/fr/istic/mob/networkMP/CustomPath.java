package fr.istic.mob.networkMP;

import android.graphics.Path;
import java.util.ArrayList;

public class CustomPath extends Path {

    private float xStart;
    private float yStart;
    private float xFinal;
    private float yFinal;

    public CustomPath(){}

    public CustomPath(float xStart, float yStart, float xFinal, float yFinal){
        this.xStart = xStart;
        this.yStart = yStart;
        this.xFinal = xFinal;
        this.yFinal = yFinal;
    }

    public void setStartPoints(float xStart, float yStart){
        this.xStart = xStart;
        this.yStart = yStart;
    }

    public void setFinalPoints(float xFinal, float yFinal){
        this.xFinal = xFinal;
        this.yFinal = yFinal;
    }

    public void drawThisPath(){
        this.moveTo(this.xStart, this.yStart);
        this.lineTo(this.xFinal, this.yFinal);

    }

    public float getxFinal() {
        return xFinal;
    }

    public float getxStart() {
        return xStart;
    }

    public float getyFinal() {
        return yFinal;
    }

    public float getyStart() {
        return yStart;
    }
}
