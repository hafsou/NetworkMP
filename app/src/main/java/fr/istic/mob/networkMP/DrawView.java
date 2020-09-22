package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawView extends View {
    Paint paint;
    private ArrayList<Path> connexions;
    private HashMap<RectF,String> objets;
    private HashMap<RectF,float[]> positionObjets;

    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        connexions = new ArrayList<Path>();
        objets = new HashMap<RectF,String>();
        positionObjets = new HashMap<RectF,float[]>();
        paint = new Paint();

    }
    public DrawView(Context context, ArrayList<Path> connexions, HashMap<RectF, String> objets, HashMap<RectF,float[]> positionObjets){
        super(context);
        connexions = connexions;
        objets = objets;
        positionObjets = positionObjets;
    }

    @Override
    public void onDraw(Canvas canvas) {

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        for(RectF rect : objets.keySet()){
            canvas.drawRect(rect,paint);
            System.out.println("i");
        }
        System.out.println("on draw passé");
        //oast.makeText(getContext()," dessin",Toast.LENGTH_LONG).show();
    }

    public void setConnexions(ArrayList<Path> connexions) {
        this.connexions = connexions;
    }

    public void setObjets(HashMap<RectF, String> objets) {
        this.objets = objets;
    }

    public void setPositionObjets(HashMap<RectF, float[]> positionObjets) {
        this.positionObjets = positionObjets;
    }
}

