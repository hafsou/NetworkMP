package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private static int taille = 50;
    private int nbObjet = 0;
    private HashMap<String,RectF> objects;
    private HashMap<String, HashMap<String,Path>> connexions;

    public Graph(){
        connexions = new HashMap<String, HashMap<String,Path>>();
        objects = new HashMap<String,RectF>();
    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        String nomObjetFinal = nomObjet+"_"+String.valueOf(nbObjet);
        objects.put(nomObjetFinal, rect);
        nbObjet++;
        //Toast.makeText(context,"Ajout de l'objet",Toast.LENGTH_LONG).show();
    }

    public HashMap<String, RectF> getObjects() {
        return objects;
    }

    public HashMap<String, HashMap<String, Path>> getConnexions() {
        return connexions;
    }

    public void reinitialize(){
        objects = new HashMap<String,RectF>();
        connexions = new HashMap<String, HashMap<String,Path>>();
        nbObjet = 0;
    }

    public int getTaille() {
        return taille;
    }
}

