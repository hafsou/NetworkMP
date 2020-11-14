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
    private boolean isBent;
    private float xControl;
    private float yControl;

    public CustomPath(){
        this.xStart = 0;
        this.yStart = 0;
        this.xFinal = 0;
        this.yFinal = 0;
        this.xControl = 0;
        this.yControl = 0;
        this.color = Color.BLACK;
        this.strokeWidth = 10;
        this.isBent = false;
    }

    public CustomPath(float xStart, float yStart, float xFinal, float yFinal, float xControl, float yControl, boolean isBent, int color, int strokeWidth){
        this.xStart = xStart;
        this.yStart = yStart;
        this.xFinal = xFinal;
        this.yFinal = yFinal;
        this.xControl = xControl;
        this.yControl =yControl;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.isBent = isBent;
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
        if(this.isBent){
            this.quadTo(this.xControl,this.yControl, this.xFinal, this.yFinal);
        }else {
            this.lineTo(this.xFinal, this.yFinal);
        }

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

    public void setBent(boolean bent) {
        isBent = bent;
    }

    public boolean isBent() {
        return isBent;
    }

    public float getxControl() {
        return xControl;
    }

    public float getyControl() {
        return yControl;
    }

    public void setxControl(float xControl) {
        this.xControl = xControl;
    }

    public void setyControl(float yControl) {
        this.yControl = yControl;
    }

}
