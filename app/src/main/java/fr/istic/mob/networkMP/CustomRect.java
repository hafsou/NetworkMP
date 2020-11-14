package fr.istic.mob.networkMP;

import android.graphics.RectF;

public class CustomRect extends RectF {

    public String name;

    public CustomRect(String name, float left, float top, float right, float bottom){
        super(left,top,right,bottom);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
