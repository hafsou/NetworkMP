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
import android.text.InputType;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that draw the network on the plan
 * @author Loan et Hafsa
 */
public class DrawView extends androidx.appcompat.widget.AppCompatImageView {

    Paint paint;
    private static final int TAILLE = Graph.SIZE/2;
    private SparseArray<CustomRect> mRectPointer = new SparseArray<CustomRect>();
    private SparseArray<CustomPath> mPathPointer = new SparseArray<CustomPath>();
    private SparseArray<CustomPath> mPathTouchedPointer = new SparseArray<CustomPath>();
    private Mode mode = Mode.OBJETS;
    private Graph graph;
    private ArrayList<CustomPath> pathTemporaryCreated; //used for the creation of the path between two objects
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
        //draw all the connections
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
                    canvas.drawPath(pathToDraw, paint);
                    if (graph.hasConnexionName(object1, object2)) {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setTextSize(60);
                        paint.setColor(Color.WHITE);
                        ConnexionLabel connexionLabel = graph.getConnexionLabel(object1, object2);
                        PathMeasure pm = new PathMeasure(pathToDraw, false);
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
                        //draw the connexion name on the middle of the connexion
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
        //draw all the objects
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
                paint.setTextSize(60);
                paint.setColor(Color.WHITE);
                canvas.drawText(rect.getName(), rect.left, rect.bottom + 60, paint);
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
            //press on the screen
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearRectPointer();
                clearPathPointer();
                clearPathTouchedPointer();
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                // check if we have touched inside some rectangle
                touchedRect = getTouchedRect(xTouch, yTouch);
                tmpRectName = getNameTouchedRect(xTouch,yTouch);
                nameTouchedRect = getNameTouchedRect(xTouch,yTouch);
                if(mode == Mode.OBJETS) {
                    if (touchedRect != null) {
                        setLongClickable(false);
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        touchedRect.left = xTouch - TAILLE;
                        touchedRect.top = yTouch - TAILLE;
                        touchedRect.right = xTouch + TAILLE;
                        touchedRect.bottom = yTouch + TAILLE;
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
                            touchedRect.left = xTouch - TAILLE;
                            touchedRect.top = yTouch - TAILLE;
                            touchedRect.right = xTouch + TAILLE;
                            touchedRect.bottom = yTouch + TAILLE;
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
                                        if(pathToModify.isBent()){
                                            pathToModify.lineTo(rectObject2.centerX(), rectObject2.centerY());
                                            //normalement lineTo a remplacer par quadTo ainsi qu'un calcul mathématique permettant de retrouver les
                                            //nouvelles coordonnées du point de controle. Nous n'avons pas reussi via les calculs a retrouver ces
                                            //nouvelles coordonnées.
                                        }else {
                                            pathToModify.lineTo(rectObject2.centerX(), rectObject2.centerY());
                                        }
                                        pathToModify.setStartPoints(xTouch,yTouch);
                                        pathToModify.setFinalPoints(rectObject2.centerX(), rectObject2.centerY());
                                    } else if (object2.equals(nameOfRect)) {
                                        CustomPath pathToModify = link.get(object2);
                                        pathToModify.reset();
                                        pathToModify.moveTo(xTouch, yTouch);
                                        CustomRect rectObject1 = allObjects.get(object1);
                                        if(pathToModify.isBent()){
                                            pathToModify.lineTo(rectObject1.centerX(), rectObject1.centerY());
                                            //normalement lineTo a remplacer par quadTo ainsi qu'un calcul mathématique permettant de retrouver les
                                            //nouvelles coordonnées du point de controle. Nous n'avons pas reussi via les calculs a retrouver ces
                                            //nouvelles coordonnées.
                                        }else {
                                            pathToModify.lineTo(rectObject1.centerX(), rectObject1.centerY());
                                        }
                                        pathToModify.setStartPoints(xTouch,yTouch);
                                        pathToModify.setFinalPoints(rectObject1.centerX(), rectObject1.centerY());
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
                            CustomPath tmp = new CustomPath();
                            tmp.moveTo(oldXStart,oldYStart);
                            tmp.lineTo(oldXFinal,oldYFinal);
                            PathMeasure pm = new PathMeasure(tmp, false);
                            float middleCoord[] = {0f, 0f};
                            //get point from the middle
                            pm.getPosTan(pm.getLength() * 0.5f, middleCoord, null);
                            pathTouched.reset();
                            pathTouched.moveTo(oldXStart, oldYStart);
                            double distanceY = yTouch - middleCoord[1];
                            double distanceX = middleCoord[0] - xTouch;
                            float y = Float.parseFloat(String.valueOf(distanceY));
                            float x = Float.parseFloat(String.valueOf(distanceX));
                            //Bezier point with control point
                            pathTouched.quadTo(xTouch-x,yTouch+y,oldXFinal,oldYFinal);
                            pathTouched.setBent(true);
                            pathTouched.setxControl(xTouch-x);
                            pathTouched.setyControl(yTouch+y);
                        }
                    }
                }
                invalidate();
                handled = true;
                break;

                //finger's lifted on the screen
            case MotionEvent.ACTION_UP:
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
                        float aCoordinates[] = {0f, 0f};
                        //get point from the middle
                        pm.getPosTan(pm.getLength() * 0f, aCoordinates, null);
                        pathCreated.reset();
                        pathCreated.moveTo(aCoordinates[0],aCoordinates[1]);
                        pathCreated.lineTo(xTouch,yTouch);
                        pathCreated.setStartPoints(aCoordinates[0],aCoordinates[1]);
                        pathCreated.setFinalPoints(xTouch, yTouch);
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
                break;
        }
        return super.onTouchEvent(event) || handled;
    }

    /**
     * Open a popup that ask the user for the name of the path.
     * If the user press cancel, the name of the connexion will be "default".
     * @param objet1 first object of the connexion
     * @param objet2 second object of the connexion
     * @param context context
     */
    private void popupNamePath(final String objet1, final String objet2, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.popupNamePath_title));
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connexionName = input.getText().toString();
                graph.addConnexionLabel(objet1, objet2, connexionName);
                invalidate();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connexionName = "default";
                graph.addConnexionLabel(objet1, objet2, connexionName);
                invalidate();
            }
        });
        builder.show();
    }

    /**
     * Check if a path exist between two objects
     * @param firstObject the first object of the connexion
     * @param secondObject the second object of the connexion
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

    /**
     * This method print the existing connexions between objects.
     */
    private void displayConnexions(){
        HashMap<String, HashMap<String,CustomPath>> connexions = graph.getConnexions();
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    System.out.println("Une connexion existe entre : "+object1+" et "+object2);
                }
            }
        }
    }

    /**
     * Reinitialize the graph
     */
    public void reinitializeGraph(){
        graph.reinitialize();
    }

    /**
     * Clear the rect pointer
     */
    private void clearRectPointer() {
        mRectPointer.clear();
    }

    /**
     * Clear the path pointer
     */
    private void clearPathPointer() {
        mPathPointer.clear();
    }

    /**
     * Clear the path touched pointer
     */
    private void clearPathTouchedPointer() {
        mPathTouchedPointer.clear();
    }

    /**
     * Return the rectangle touched by the user at the coordinate (xTouch, yTouch)
     * Return null if no rectangle touched
     * @param xTouch x coordinate
     * @param yTouch y coordinate
     * @return the CustomRect touched or null
     */
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

    /**
     * Return the path touched by the user at the coordinate (x,y).
     * The detection of the path touched is : the label is touched or not.
     * Return null if no path/label touched.
     * @param x
     * @param y
     * @return the CustomPath or null
     */
    private CustomPath getTouchedPath(final int x, final int y){
        CustomPath customPathNameTouched = null;
        HashMap<String, HashMap<String,ConnexionLabel>> connectionNames = graph.getConnectionsLabels();
        HashMap<String, HashMap<String,CustomPath>> connections = graph.getConnexions();
        for(String rect1 : connectionNames.keySet()){
            HashMap<String,ConnexionLabel> connexionToRect2 = connectionNames.get(rect1);
            for(String rect2 : connexionToRect2.keySet()){
                ConnexionLabel connexionLabel = connexionToRect2.get(rect2);
                if(x<= connexionLabel.getHeight() && x>= connexionLabel.getX() && y<= connexionLabel.getY() && y>=connexionLabel.getWidth()){
                    customPathNameTouched = connections.get(rect1).get(rect2);
                }
            }
        }
        return customPathNameTouched;
    }

    /**
     * Return the name of the rectangle touched
     * Return null if no rectangle touched
     * @param xTouch
     * @param yTouch
     * @return the name or null
     */
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

