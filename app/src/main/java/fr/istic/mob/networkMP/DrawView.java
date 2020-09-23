package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DrawView extends View {
    Paint paint;
    private int taille = 50;
    private ArrayList<Path> connexions;
    private HashMap<RectF,String> objects;
    private HashMap<RectF,float[]> positionObjets;
    private SparseArray<RectF> mRectPointer = new SparseArray<RectF>();



    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        connexions = new ArrayList<Path>();
        objects = new HashMap<RectF,String>();
        positionObjets = new HashMap<RectF,float[]>();
        paint = new Paint();

    }
    public DrawView(Context context, ArrayList<Path> connexions, HashMap<RectF, String> objects, HashMap<RectF,float[]> positionObjets){
        super(context);
        this.connexions = connexions;
        this.objects = objects;
        this.positionObjets = positionObjets;
    }

    @Override
    public void onDraw(Canvas canvas) {

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        for(RectF rect : objects.keySet()){
            paint.setColor(Color.BLACK);
            canvas.drawRect(rect,paint);
            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            if(objects.get(rect)!= null) {
                canvas.drawText(objects.get(rect), rect.left, rect.bottom + 40, paint);
            }else{
                canvas.drawText("zzz", rect.left, rect.bottom + 40, paint);
            }
        }
        System.out.println("on draw passé");
        //Toast.makeText(getContext()," dessin",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        RectF touchedRect;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent rect from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearRectPointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedRect = getTouchedRect(xTouch, yTouch);
                if(touchedRect != null) {
                    touchedRect.left = xTouch;
                    touchedRect.top = yTouch;
                    touchedRect.right = xTouch + taille;
                    touchedRect.bottom = yTouch + taille;


                    mRectPointer.put(event.getPointerId(0), touchedRect);

                    invalidate();
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedRect = getTouchedRect(xTouch, yTouch);
                if(touchedRect != null) {
                    mRectPointer.put(pointerId, touchedRect);
                    touchedRect.left = xTouch;
                    touchedRect.top = yTouch;
                    touchedRect.right = xTouch + taille;
                    touchedRect.bottom = yTouch + taille;
                    invalidate();
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                System.out.println("Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedRect = mRectPointer.get(pointerId);

                    if (null != touchedRect) {
                        touchedRect.left = xTouch;
                        touchedRect.top = yTouch;
                        touchedRect.right = xTouch + taille;
                        touchedRect.bottom = yTouch + taille;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearRectPointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mRectPointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearRectPointer() {
        System.out.println("clearCirclePointer");
        mRectPointer.clear();
    }


    private RectF getTouchedRect(final int xTouch, final int yTouch) {
        RectF touched = null;

        for (RectF rect : objects.keySet()) {
            if (xTouch<= rect.right && xTouch>= rect.left && yTouch>= rect.top && yTouch<=rect.bottom) {
                touched = rect;
                break;
            }
        }

        return touched;
    }

    public void setConnexions(ArrayList<Path> connexions) {
        this.connexions = connexions;
    }

    public void setObjects(HashMap<RectF, String> objets) {
        this.objects = objets;
    }

    public void setPositionObjects(HashMap<RectF, float[]> positionObjects) {
        this.positionObjets = positionObjets;
    }
}

