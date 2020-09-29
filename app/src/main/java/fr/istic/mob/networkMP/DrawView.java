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
    private HashMap<String,RectF> objects;
    private SparseArray<RectF> mRectPointer = new SparseArray<RectF>();
    private SparseArray<Path> mPathPointer = new SparseArray<Path>();
    private Mode mode = Mode.OBJETS;
    private HashMap<String, HashMap<String,Path>> connexions;
    private ArrayList<Path> pathTemporaryCreated;
    private String tmpRectF = "";

    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        objects = new HashMap<String,RectF>();
        paint = new Paint();
        connexions = new HashMap<String,HashMap<String, Path>>();
        pathTemporaryCreated = new ArrayList<Path>();

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
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,Path> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    canvas.drawPath(linkToObject2.get(object2),paint);
                }
            }
        }

        for(Path path: pathTemporaryCreated) {
            canvas.drawPath(path, paint);
        }

//        Path tst = new Path();
//        tst.moveTo(500,500);
//        tst.lineTo(400,800);
//        tst.reset();
//        tst.lineTo(100,300);
//        canvas.drawPath(tst,paint);
        //Toast.makeText(getContext()," dessin",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        RectF touchedRect;
        //String firstRectTouchedName;
        Path pathCreated;
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
                tmpRectF = getNameTouchedRect(xTouch,yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                System.out.println("name touched rect : "+nameTouchedRect);
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
                        pathCreated = new Path();
                        pathCreated.moveTo(xTouch,yTouch);
                        Path tmpPath = new Path();
                        tmpPath.moveTo(xTouch,yTouch);
                        pathTemporaryCreated.add(tmpPath);
                        mPathPointer.put(event.getPointerId(0), pathCreated);
                        invalidate();
                        handled = true;
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
                    pathCreated = mPathPointer.get(pointerId);
                    if(mode == Mode.OBJETS) {
                        if (null != touchedRect) {
                            touchedRect.left = xTouch;
                            touchedRect.top = yTouch;
                            touchedRect.right = xTouch + taille;
                            touchedRect.bottom = yTouch + taille;
                        }
                    }else if(mode == Mode.CONNEXIONS){
                        if (null != pathCreated) {
                            pathCreated.lineTo(xTouch, yTouch);
                            pathTemporaryCreated.get(0).lineTo(xTouch,yTouch);
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
                    pathCreated = mPathPointer.get(pointerId);
                    if(touchedRect != null && nameTouchedRect != null){
                        PathMeasure pm = new PathMeasure(pathCreated, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0f, aCoordinates, null);
                        pathCreated.reset();
                        pathCreated.moveTo(aCoordinates[0],aCoordinates[1]);
                        pathCreated.lineTo(xTouch,yTouch);
                        HashMap<String,Path> test = this.connexions.get(tmpRectF);   //FAIRE UN TEST object1 -> object2, link
                                                                                    // existe pas deja object2 -> object1, link!
                        if(test != null){
                            Path test2 = test.get(nameTouchedRect);
                            if(test2 == null){
                                test.put(nameTouchedRect,pathCreated);
                            }else{
                                System.out.println("testssss:"+test);
                            }
                        }else{
                            HashMap<String,Path> link = new HashMap<String,Path>();
                            link.put(nameTouchedRect,pathCreated);
                            System.out.println("firstRectTouchedName : "+tmpRectF);
                            this.connexions.put(tmpRectF,link);
                        }

                        pathTemporaryCreated.remove(0);
                    }else{
                        pathCreated.reset();
                        pathTemporaryCreated.remove(0);
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

//    public void setObjectsConnexions(HashMap<String, ArrayList<Path>> objectsConnexions) {
//        this.objectsConnexions = objectsConnexions;
//    }
}

