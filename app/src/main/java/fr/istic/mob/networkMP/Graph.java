package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private static int taille = 50;
    private HashMap<String,RectF> objects;
    private HashMap<String, HashMap<String,Path>> connexions;

    public Graph(){
        connexions = new HashMap<String, HashMap<String,Path>>();
        objects = new HashMap<String,RectF>();
    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        objects.put(nomObjet, rect);
        Toast.makeText(context,"Ajout de l'objet",Toast.LENGTH_LONG).show();

    }

    public HashMap<String, RectF> getObjects() {
        return objects;
    }


    public int getTaille() {
        return taille;
    }
}

