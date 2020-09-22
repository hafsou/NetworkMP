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
    private HashMap<RectF,String> objets;
    private HashMap<RectF,float[]> positionObjets;

    public Graph(){
        connexions = new ArrayList<Path>();
        objets = new HashMap<RectF,String>();
        positionObjets = new HashMap<RectF,float[]>();
    }

    public void addConnexion(){

    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        objets.put(rect, nomObjet);
        float[] position = {x,y};
        positionObjets.put(rect, position);
        Toast.makeText(context,"Ajout de l'objet",Toast.LENGTH_LONG).show();

    }
    public void addPositionObjet(){

    }

    public HashMap<RectF, String> getObjets() {
        return objets;
    }

    public HashMap<RectF, float[]> getPositionObjets() {
        return positionObjets;
    }

    public ArrayList<Path> getConnexions() {
        return connexions;
    }
}

