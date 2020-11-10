package fr.istic.mob.networkMP;

import android.graphics.Color;
import android.graphics.Path;

public class CustomPath extends Path {

    private float xStart;
    private float yStart;
    private float xFinal;
    private float yFinal;
    private int color;
    private int strokeWidth;

    public CustomPath(){
        this.xStart = 0;
        this.yStart = 0;
        this.xFinal = 0;
        this.yFinal = 0;
        this.color = Color.BLACK;
        this.strokeWidth = 10;
    }

    public CustomPath(float xStart, float yStart, float xFinal, float yFinal){
        this.xStart = xStart;
        this.yStart = yStart;
        this.xFinal = xFinal;
        this.yFinal = yFinal;
        this.color = Color.BLACK;
        this.strokeWidth = 10;
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

    public int getColor() {
        return color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
