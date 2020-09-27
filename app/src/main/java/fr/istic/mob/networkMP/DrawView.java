package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
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
    private static int taille = 50;
    //private ArrayList<Path> connexions;
    private HashMap<String,RectF> objects;
    private SparseArray<RectF> mRectPointer = new SparseArray<RectF>();
    private SparseArray<Path> mPathPointer = new SparseArray<Path>();
    private Mode mode = Mode.OBJETS;
    private HashMap<String,ArrayList<Path>> objectsConnexions;

    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        //connexions = new ArrayList<Path>();
        objects = new HashMap<String,RectF>();
        paint = new Paint();
        objectsConnexions = new HashMap<String, ArrayList<Path>>();

    }
    public DrawView(Context context, HashMap<String, ArrayList<Path>> objectsConnexions, HashMap<String, RectF> objects){
        super(context);
        //this.connexions = connexions;
        this.objectsConnexions = objectsConnexions;
        this.objects = objects;
    }

    @Override
    public void onDraw(Canvas canvas) {

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        for(String nameRect : objects.keySet()){
            RectF rect = objects.get(nameRect);
            paint.setColor(Color.BLACK);
            canvas.drawRect(rect,paint);
            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            canvas.drawText(nameRect, rect.left, rect.bottom + 40, paint);
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        for(String object : objectsConnexions.keySet()){
            ArrayList<Path> listPath = objectsConnexions.get(object);
            for(Path path : listPath) {
                canvas.drawPath(path, paint);
            }
        }
//        Path tst = new Path();
//        tst.moveTo(500,500);
//        tst.lineTo(400,800);
//        tst.reset();
//        tst.lineTo(100,300);
//        canvas.drawPath(tst,paint);
        System.out.println("on draw passé");
        //Toast.makeText(getContext()," dessin",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        RectF touchedRect;
        RectF firstRectTouched;
        Path pathT;
        String nameTouchedRect;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent rect from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:    //appui sur l'écran
                // it's the first pointer, so clear all existing pointers data
                clearRectPointer();
                clearPathPointer();
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                // check if we've touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                firstRectTouched = getTouchedRect(xTouch,yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                if(mode == Mode.OBJETS) {
                    if (touchedRect != null) {
                        touchedRect.left = xTouch;
                        touchedRect.top = yTouch;
                        touchedRect.right = xTouch + taille;
                        touchedRect.bottom = yTouch + taille;
                        mRectPointer.put(event.getPointerId(0), touchedRect);
                        invalidate();
                        handled = true;
                    }
                }else if(mode == Mode.CONNEXIONS){
                    if(touchedRect != null && nameTouchedRect != null){
                      System.out.println(nameTouchedRect);
                      ArrayList<Path> listPath = objectsConnexions.get(nameTouchedRect);
                      if(listPath != null && listPath.size() >0){
                          Path path = listPath.get(listPath.size() - 1);
                          path.moveTo(xTouch,yTouch);
                          mPathPointer.put(event.getPointerId(0), path);
                          invalidate();
                          handled = true;
                          System.out.println("o");
                      }

                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                if(mode == Mode.OBJETS) {
                    if (touchedRect != null) {
                        mRectPointer.put(pointerId, touchedRect);
                        touchedRect.left = xTouch;
                        touchedRect.top = yTouch;
                        touchedRect.right = xTouch + taille;
                        touchedRect.bottom = yTouch + taille;
                        invalidate();
                        handled = true;
                    }
                }else if(mode == Mode.CONNEXIONS){
                    if(touchedRect != null && nameTouchedRect != null){
                        System.out.println(nameTouchedRect);
                        ArrayList<Path> listPath = objectsConnexions.get(nameTouchedRect);
                        if(listPath != null && listPath.size()>0) {
                            Path path = listPath.get(listPath.size() - 1);
                            mPathPointer.put(pointerId, path);
                            path.moveTo(xTouch,yTouch);
                            invalidate();
                            handled = true;
                            System.out.println("b");
                        }
                    }
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
                    pathT = mPathPointer.get(pointerId);
                    if(mode == Mode.OBJETS) {
                        if (null != touchedRect) {
                            touchedRect.left = xTouch;
                            touchedRect.top = yTouch;
                            touchedRect.right = xTouch + taille;
                            touchedRect.bottom = yTouch + taille;
                        }
                    }else if(mode == Mode.CONNEXIONS){
                        if (null != pathT) {
                            pathT.lineTo(xTouch, yTouch);
                        }
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:   //relachement du doigt sr l'ecran
                //draw the path only if the last position is an another object
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                // check if we've touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                if(mode == Mode.CONNEXIONS){
                    pointerId = event.getPointerId(actionIndex);
                    pathT = mPathPointer.get(pointerId);
                    if(touchedRect != null && nameTouchedRect != null){
                        PathMeasure pm = new PathMeasure(pathT, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0f, aCoordinates, null);
                        pathT.reset();
                        pathT.moveTo(aCoordinates[0],aCoordinates[1]);
                        pathT.lineTo(xTouch,yTouch);
                        System.out.println(nameTouchedRect);
                    }else{
                        pathT.reset();
                    }
                }

                clearRectPointer();
                clearPathPointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mRectPointer.remove(pointerId);
                mPathPointer.remove(pointerId);
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

    private void clearPathPointer() {
        System.out.println("clearPathPointer");
        mPathPointer.clear();
    }


    private RectF getTouchedRect(final int xTouch, final int yTouch) {
        RectF touched = null;
        for (String nameRect : objects.keySet()) {
            RectF rect = objects.get(nameRect);
            if (xTouch<= rect.right && xTouch>= rect.left && yTouch>= rect.top && yTouch<=rect.bottom) {
                touched = rect;
                break;
            }
        }
        return touched;
    }

    private String getNameTouchedRect(final int xTouch, final int yTouch){
        String touched = null;
        for (String nameRect : objects.keySet()) {
            RectF rect = objects.get(nameRect);
            if (xTouch<= rect.right && xTouch>= rect.left && yTouch>= rect.top && yTouch<=rect.bottom) {
                touched = nameRect;
                break;
            }
        }
        return touched;
    }

    //public void setConnexions(ArrayList<Path> connexions) {
        //this.connexions = connexions;
    //}

    public void setObjects(HashMap<String, RectF> objets) {
        this.objects = objets;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setObjectsConnexions(HashMap<String, ArrayList<Path>> objectsConnexions) {
        this.objectsConnexions = objectsConnexions;
    }
}

