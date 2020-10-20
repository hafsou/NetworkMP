package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawView extends androidx.appcompat.widget.AppCompatImageView {
    Paint paint;
    private static int taille = 50;
    private float[] lastTouchDownXY = new float[2];
    private SparseArray<RectF> mRectPointer = new SparseArray<RectF>();
    private SparseArray<CustomPath> mPathPointer = new SparseArray<CustomPath>();
    private Mode mode = Mode.OBJETS;
    private Graph graph;
    private ArrayList<CustomPath> pathTemporaryCreated;
    private String tmpRectName = "";
    private String objectName;
    private String connexionName;

    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        paint = new Paint();
        graph = new Graph();
        pathTemporaryCreated = new ArrayList<CustomPath>();
    }


    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        HashMap<String,RectF> objects = graph.getObjects();
        HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
        for(String nameRect : objects.keySet()){
            RectF rect = objects.get(nameRect);
            paint.setColor(Color.BLACK);
            canvas.drawRect(rect,paint);
            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            int indexDelimiter = nameRect.lastIndexOf("_");
            String objectName = nameRect.substring(0,indexDelimiter);
            canvas.drawText(objectName, rect.left, rect.bottom + 40, paint);
        }
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    CustomPath pathToDraw = linkToObject2.get(object2);
                    System.out.println("DESSIN : " + object1 + " --- " + object2);
                    canvas.drawPath(pathToDraw, paint);
                    if (graph.hasConnexionName(object1, object2)) {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setTextSize(40);
                        paint.setColor(Color.WHITE);
                        String connexionName = graph.getConnexionName(object1, object2);
                        System.out.println("Connexion name : "+connexionName);
                        PathMeasure pm = new PathMeasure(pathToDraw, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
                        canvas.drawText(connexionName, aCoordinates[0], aCoordinates[1], paint);
                    }
                }
            }
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        for(CustomPath CustomPath: pathTemporaryCreated) {
            canvas.drawPath(CustomPath, paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        RectF touchedRect;
        CustomPath pathCreated;
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
                tmpRectName = getNameTouchedRect(xTouch,yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                System.out.println("name touched rect : "+nameTouchedRect);
                if(mode == Mode.OBJETS) {
                    if (touchedRect != null) {
                        setLongClickable(false);
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        touchedRect.left = xTouch;
                        touchedRect.top = yTouch;
                        touchedRect.right = xTouch + taille;
                        touchedRect.bottom = yTouch + taille;
                        mRectPointer.put(event.getPointerId(0), touchedRect);
                        invalidate();
                        handled = true;
                    }else{
                        setLongClickable(true);
                    }
                }else if(mode == Mode.CONNEXIONS){
                    if(touchedRect != null && nameTouchedRect != null){
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        pathCreated = new CustomPath();
                        pathCreated.moveTo(xTouch,yTouch);
                        CustomPath tmpPath = new CustomPath();
                        tmpPath.moveTo(xTouch,yTouch);
                        pathTemporaryCreated.add(tmpPath);
                        mPathPointer.put(event.getPointerId(0), pathCreated);
                        invalidate();
                        handled = true;
                    }
                }else if(mode == Mode.MODIFICATIONS){
                    setLongClickable(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();
                HashMap<String, HashMap<String,CustomPath>> theConnexions = graph.getConnexions();
                HashMap<String,RectF> allObjects = graph.getObjects();
                System.out.println("Move");
                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedRect = mRectPointer.get(pointerId);
                    pathCreated = mPathPointer.get(pointerId);
                    if (mode == Mode.OBJETS) {
                        if (null != touchedRect) {
                            touchedRect.left = xTouch;
                            touchedRect.top = yTouch;
                            touchedRect.right = xTouch + taille;
                            touchedRect.bottom = yTouch + taille;
                            String nameOfRect = getNameTouchedRect(xTouch, yTouch);
                            HashMap<String, CustomPath> link = theConnexions.get(nameOfRect);
                            for (String object1 : theConnexions.keySet()) {
                                link = theConnexions.get(object1);
                                for (String object2 : link.keySet()) {
                                    if (object1.equals(nameOfRect)) {
                                        CustomPath pathToModify = link.get(object2);
                                        pathToModify.reset();
                                        pathToModify.moveTo(xTouch, yTouch);
                                        RectF rectObject2 = allObjects.get(object2);
                                        pathToModify.lineTo(rectObject2.left, rectObject2.top);
                                    } else if (object2.equals(nameOfRect)) {
                                        CustomPath pathToModify = link.get(object2);
                                        pathToModify.reset();
                                        pathToModify.moveTo(xTouch, yTouch);
                                        RectF rectObject1 = allObjects.get(object1);
                                        pathToModify.lineTo(rectObject1.left, rectObject1.top);
                                    }
                                }
                            }
                        }
                    } else if (mode == Mode.CONNEXIONS) {
                        if (null != pathCreated) {
                            pathCreated.lineTo(xTouch, yTouch);
                            pathTemporaryCreated.get(0).lineTo(xTouch, yTouch);
                        }
                    }
                }

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:   //relachement du doigt sr l'ecran
                setLongClickable(true);
                //draw the CustomPath only if the last position is an another object
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                // check if we've touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
                if(mode == Mode.CONNEXIONS){
                    pointerId = event.getPointerId(actionIndex);
                    pathCreated = mPathPointer.get(pointerId);
                    if(touchedRect != null && nameTouchedRect != tmpRectName){
                        PathMeasure pm = new PathMeasure(pathCreated, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0f, aCoordinates, null);
                        pathCreated.reset();
                        pathCreated.moveTo(aCoordinates[0],aCoordinates[1]);
                        pathCreated.lineTo(xTouch,yTouch);
                        float[] initPoints = new float[2];
                        initPoints[0] = aCoordinates[0];
                        initPoints[1] = aCoordinates[1];
                        pathCreated.addPathPoints(initPoints);
                        float[] finalPoints = new float[2];
                        finalPoints[0] = xTouch;
                        finalPoints[1] = yTouch;
                        pathCreated.addPathPoints(finalPoints);
                        System.out.println("un lien existe : "+pathExist(tmpRectName,nameTouchedRect));
                        displayConnexions();
                        if(!pathExist(tmpRectName,nameTouchedRect)){
                            if(connexions.size() == 0){
                                HashMap<String,CustomPath> link = new HashMap<String,CustomPath>();
                                link.put(nameTouchedRect,pathCreated);
                                connexions.put(tmpRectName,link);
                                popupNamePath(tmpRectName,nameTouchedRect,getContext());
                            }else {
                                HashMap<String, CustomPath> link = connexions.get(tmpRectName);
                                if (link != null) {
                                    link.put(nameTouchedRect,pathCreated);
                                    connexions.put(tmpRectName,link);
                                    popupNamePath(tmpRectName,nameTouchedRect,getContext());
                                } else {
                                    link = connexions.get(nameTouchedRect);
                                    if(link !=null) {
                                        link.put(tmpRectName, pathCreated);
                                        connexions.put(nameTouchedRect,link);
                                        popupNamePath(tmpRectName,nameTouchedRect,getContext());
                                    }else{
                                        HashMap<String,CustomPath> link2 = new HashMap<String,CustomPath>();
                                        link2.put(nameTouchedRect,pathCreated);
                                        connexions.put(tmpRectName,link2);
                                        popupNamePath(tmpRectName,nameTouchedRect,getContext());
                                    }
                                }
                            }
                        }else{
                            tmpRectName = "";
                            pathCreated = null;
                        }
                        pathTemporaryCreated.remove(0);
                    }else{
                        tmpRectName = "";
                        pathCreated = null;
                        if(pathTemporaryCreated.size()>0) {
                            pathTemporaryCreated.remove(0);
                        }

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
        //setLongClickable(true);
        return super.onTouchEvent(event) || handled;
    }

    private boolean popupNamePath(final String objet1, final String objet2, Context context){
        final boolean[] addConnexion = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.popupNamePath_title));
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connexionName = input.getText().toString();
                graph.addConnexionName(objet1, objet2, connexionName);
                addConnexion[0] = true;
                invalidate();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connexionName = null;
                addConnexion[0] = false;
                dialog.cancel();
            }
        });
        builder.show();
        return addConnexion[0];
    }

    /**
     *
     * @param firstObject
     * @param secondObject
     * @return
     */
    private boolean pathExist(String firstObject, String secondObject){
        boolean exist = false;
        HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    if((object1.equals(firstObject) && object2.equals(secondObject)) || (object1.equals(secondObject) && object2.equals(firstObject))){
                        exist = true;
                    }

                }
            }
        }
        return exist;
    }

    private void displayConnexions(){
        HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    System.out.println("Lien entre : "+object1+" et "+object2);
                }
            }
        }
    }

    public void reinitializeGraph(){
        graph.reinitialize();
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
        HashMap<String,RectF> objects = graph.getObjects();
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
        HashMap<String,RectF> objects = graph.getObjects();
        for (String nameRect : objects.keySet()) {
            RectF rect = objects.get(nameRect);
            if (xTouch<= rect.right && xTouch>= rect.left && yTouch>= rect.top && yTouch<=rect.bottom) {
                touched = nameRect;
                break;
            }
        }
        return touched;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph){
        this.graph = graph;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

}

