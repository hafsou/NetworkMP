package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private int taille = 50;
    private ArrayList<Path> connexions;
    private HashMap<String,RectF> objects;
    private HashMap<RectF,float[]> positionObjets;

    public Graph(){
        connexions = new ArrayList<Path>();
        objects = new HashMap<String,RectF>();
        positionObjets = new HashMap<RectF,float[]>();
    }

    public void addConnexion(){

    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        objects.put(nomObjet, rect);
        float[] position = {x,y};
        positionObjets.put(rect, position);
        Toast.makeText(context,"Ajout de l'objet",Toast.LENGTH_LONG).show();

    }
    public void addPositionObject(){

    }

    public HashMap<String, RectF> getObjects() {
        return objects;
    }

    public HashMap<RectF, float[]> getPositionObjects() {
        return positionObjets;
    }

    public ArrayList<Path> getConnexions() {
        return connexions;
    }

    public int getTaille() {
        return taille;
    }
}

