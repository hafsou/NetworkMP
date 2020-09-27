package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private static int taille = 50;
    //private ArrayList<Path> connexions;
    private HashMap<String,RectF> objects;
    private HashMap<String,ArrayList<Path>> objectsConnexions;


    public Graph(){
        //connexions = new ArrayList<Path>();
        objects = new HashMap<String,RectF>();
        objectsConnexions = new HashMap<String, ArrayList<Path>>();
    }

    public void addConnexion(Context context, String nomObjet, float x, float y){
        Path path = new Path();
        path.lineTo(x,y);
        ArrayList<Path> listPath = objectsConnexions.get(nomObjet);
        listPath.add(path);
        objectsConnexions.put(nomObjet,listPath);
        //connexions.add(path);
    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        objects.put(nomObjet, rect);
        objectsConnexions.put(nomObjet, new ArrayList<Path>());
        float[] position = {x,y};
        Toast.makeText(context,"Ajout de l'objet",Toast.LENGTH_LONG).show();

    }

    public HashMap<String, RectF> getObjects() {
        return objects;
    }


    //public ArrayList<Path> getConnexions() {
        //return connexions;
    //}


    public HashMap<String, ArrayList<Path>> getObjectsConnexions() {
        return objectsConnexions;
    }

    public int getTaille() {
        return taille;
    }
}

