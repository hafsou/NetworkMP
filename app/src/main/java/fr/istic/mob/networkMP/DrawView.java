package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
    private static int taille = 70;
    private SparseArray<CustomRect> mRectPointer = new SparseArray<CustomRect>();
    private SparseArray<CustomPath> mPathPointer = new SparseArray<CustomPath>();
    private SparseArray<CustomPath> mPathTouchedPointer = new SparseArray<CustomPath>();
    private Mode mode = Mode.OBJETS;
    private Graph graph;
    private ArrayList<CustomPath> pathTemporaryCreated;
    private String tmpRectName = "";
    private String connexionName;

    public DrawView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        paint = new Paint();
        graph = new Graph();
        pathTemporaryCreated = new ArrayList<CustomPath>();
    }


    @Override
    public void onDraw(Canvas canvas) {
        HashMap<String,CustomRect> objects = graph.getObjects();
        HashMap<String,Integer> objectsColor = graph.getObjectsColor();
        HashMap<String,Bitmap> objectsIcons = graph.getObjectsIcons();
        HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    CustomPath pathToDraw = linkToObject2.get(object2);
                    paint.setColor(pathToDraw.getColor());
                    paint.setStrokeWidth(pathToDraw.getStrokeWidth());
                    System.out.println("DESSIN : " + object1 + " --- " + object2);
                    canvas.drawPath(pathToDraw, paint);
                    if (graph.hasConnexionName(object1, object2)) {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setTextSize(40);
                        paint.setColor(Color.WHITE);
                        ConnexionLabel connexionLabel = graph.getConnexionName(object1, object2);
                        PathMeasure pm = new PathMeasure(pathToDraw, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
                        canvas.drawText(connexionLabel.getLabel(), aCoordinates[0], aCoordinates[1], paint);
                        connexionLabel.setX(aCoordinates[0]);
                        connexionLabel.setY(aCoordinates[1]);
                    }
                }
            }
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        for(String nameRect : objects.keySet()){
            CustomRect rect = objects.get(nameRect);
            if(objectsColor.containsKey(nameRect)) {
                int color = objectsColor.get(nameRect);
                paint.setColor(color);
            }else{
                paint.setColor(Color.BLACK);
            }
            if(rect != null) {
                if(objectsIcons.containsKey(nameRect)){
                    Bitmap bitmap = objectsIcons.get(nameRect);
                    canvas.drawBitmap(bitmap,rect.left,rect.top,paint);
                }else{
                    canvas.drawRect(rect, paint);
                }
                paint.setTextSize(40);
                paint.setColor(Color.WHITE);
                canvas.drawText(rect.getName(), rect.left, rect.bottom + 40, paint);
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
        CustomPath pathTouched;
        CustomRect touchedRect;
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
                clearPathTouchedPointer();
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
                }else if(mode == Mode.CURVES){
                    pathTouched = getTouchedPath(xTouch,yTouch);
                    if(pathTouched != null){
                        setLongClickable(false);
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        mPathTouchedPointer.put(event.getPointerId(0), pathTouched);
                        invalidate();
                        handled = true;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();
                HashMap<String, HashMap<String,CustomPath>> theConnexions = graph.getConnexions();
                HashMap<String,CustomRect> allObjects = graph.getObjects();
                System.out.println("Move");
                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedRect = mRectPointer.get(pointerId);
                    pathCreated = mPathPointer.get(pointerId);
                    pathTouched = mPathTouchedPointer.get(pointerId);
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
                                        CustomRect rectObject2 = allObjects.get(object2);
                                        pathToModify.lineTo(rectObject2.left, rectObject2.top);
                                        pathToModify.setStartPoints(xTouch,yTouch);
                                        pathToModify.setFinalPoints(rectObject2.left, rectObject2.top);
                                    } else if (object2.equals(nameOfRect)) {
                                        CustomPath pathToModify = link.get(object2);
                                        pathToModify.reset();
                                        pathToModify.moveTo(xTouch, yTouch);
                                        CustomRect rectObject1 = allObjects.get(object1);
                                        pathToModify.lineTo(rectObject1.left, rectObject1.top);
                                        pathToModify.setStartPoints(xTouch,yTouch);
                                        pathToModify.setFinalPoints(rectObject1.left, rectObject1.top);
                                    }
                                }
                            }
                        }
                    } else if (mode == Mode.CONNEXIONS) {
                        if (pathCreated != null) {
                            pathCreated.lineTo(xTouch, yTouch);
                            pathTemporaryCreated.get(0).lineTo(xTouch, yTouch);
                        }
                    }else if(mode == Mode.CURVES){
                        if(pathTouched != null){
                            float oldXStart = pathTouched.getxStart();
                            float oldYStart = pathTouched.getyStart();
                            float oldXFinal = pathTouched.getxFinal();
                            float oldYFinal = pathTouched.getyFinal();
                            pathTouched.reset();
                            pathTouched.moveTo(oldXStart, oldYStart);
                            pathTouched.quadTo(xTouch,yTouch,oldXFinal,oldYFinal); //calcul a realiser car courbe de bezier, avec le point de controle
                        }
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:   //relachement du doigt sr l'ecran
                setLongClickable(true);
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                // check if we've touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
                if(mode == Mode.CONNEXIONS){
                    pointerId = event.getPointerId(actionIndex);
                    pathCreated = mPathPointer.get(pointerId);
                    //draw the CustomPath only if the last position is an another object
                    if(touchedRect != null && nameTouchedRect != tmpRectName){
                        PathMeasure pm = new PathMeasure(pathCreated, false);
                        //coordinates will be here
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0f, aCoordinates, null);
                        pathCreated.reset();
                        pathCreated.moveTo(aCoordinates[0],aCoordinates[1]);
                        pathCreated.lineTo(xTouch,yTouch);
                        pathCreated.setStartPoints(aCoordinates[0],aCoordinates[1]);
                        pathCreated.setFinalPoints(xTouch, yTouch);
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
                clearPathTouchedPointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mRectPointer.remove(pointerId);
                mPathPointer.remove(pointerId);
                mPathTouchedPointer.remove(pointerId);
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

    private void clearPathTouchedPointer() {
        System.out.println("clearPathTouchedPointer");
        mPathTouchedPointer.clear();
    }

    private CustomRect getTouchedRect(final int xTouch, final int yTouch) {
        CustomRect touched = null;
        HashMap<String,CustomRect> objects = graph.getObjects();
        for (String nameRect : objects.keySet()) {
            CustomRect rect = objects.get(nameRect);
            if (xTouch<= rect.right && xTouch>= rect.left && yTouch>= rect.top && yTouch<=rect.bottom) {
                touched = rect;
                break;
            }
        }
        return touched;
    }

    private CustomPath getTouchedPath(final int x, final int y){
        CustomPath customPathNameTouched = null;
        HashMap<String, HashMap<String,ConnexionLabel>> connectionNames = graph.getConnectionsNames();
        HashMap<String, HashMap<String,CustomPath>> connections = graph.getConnexions();
        for(String rect1 : connectionNames.keySet()){
            HashMap<String,ConnexionLabel> connexionToRect2 = connectionNames.get(rect1);
            for(String rect2 : connexionToRect2.keySet()){
                ConnexionLabel connexionLabel = connexionToRect2.get(rect2);
                System.out.println(connexionLabel.getLabel());
                System.out.println("h : "+connexionLabel.getHeight() + " x : "+connexionLabel.getX() + " y : "+connexionLabel.getY() +" w :"+connexionLabel.getWidth());
                if(x<= connexionLabel.getHeight() && x>= connexionLabel.getX() && y<= connexionLabel.getY() && y>=connexionLabel.getWidth()){
                    customPathNameTouched = connections.get(rect1).get(rect2);
                }
            }
        }
        return customPathNameTouched;
    }

    private String getNameTouchedRect(final int xTouch, final int yTouch){
        String touched = null;
        HashMap<String,CustomRect> objects = graph.getObjects();
        for (String nameRect : objects.keySet()) {
            CustomRect rect = objects.get(nameRect);
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

