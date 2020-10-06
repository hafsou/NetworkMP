package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Color;
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
    private HashMap<String, HashMap<String,String>> connexionsNames;
    private HashMap<String, Color> objectsColor;

    public Graph(){
        connexions = new HashMap<String, HashMap<String,Path>>();
        connexionsNames = new HashMap<String, HashMap<String,String>>();
        objects = new HashMap<String,RectF>();
    }

    public void addObjet(Context context, String nomObjet, float x, float y){
        RectF rect = new RectF(x,y,x+taille,y+taille);
        String nomObjetFinal = nomObjet+"_"+String.valueOf(nbObjet);
        objects.put(nomObjetFinal, rect);
        nbObjet++;
    }

    public void deleteObjet(String nomObjet){
        objects.remove(nomObjet);
        HashMap<String, HashMap<String,Path>> connexionsTmp = new HashMap<String, HashMap<String,Path>>();
        //suppression des connexions associées
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,Path> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    if((!object1.equals(nomObjet) && !object2.equals(nomObjet))){
                        HashMap<String,Path> linkTmp = new HashMap<String,Path>();
                        linkTmp.put(object2,linkToObject2.get(object2));
                        connexionsTmp.put(object1,linkTmp);
                    }

                }
            }
        }
        connexions = connexionsTmp;
        nbObjet--;
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
        connexionsNames = new HashMap<String, HashMap<String,String>>();
        nbObjet = 0;
    }

    public void addConnexionName(String objet1, String objet2, String connexionName){
        HashMap<String,String> link = this.connexionsNames.get(objet1);
        if(link != null){
            link.put(objet2,connexionName);
            this.connexionsNames.put(objet1, link);
        }else{
            link = this.connexionsNames.get(objet2);
            if(link != null){
                link.put(objet1, connexionName);
                this.connexionsNames.put(objet2, link);
            }else{
                link = new HashMap<String,String>();
                link.put(objet2,connexionName);
                this.connexionsNames.put(objet1, link);
            }
        }
    }

    public String getConnexionName(String objet1, String objet2){
        String result = "";
        HashMap<String,String> link = this.connexionsNames.get(objet1);
        if(link != null) {
            result = link.get(objet2);
        }else{
            link = this.connexionsNames.get(objet2);
            if(link != null) {
                result = link.get(objet1);
            }
        }
        return result;
    }

    public boolean hasConnexionName(String objet1, String objet2){
        boolean hasName = false;
        String name;
        HashMap<String,String> link = this.connexionsNames.get(objet1);
        if(link != null) {
            name = link.get(objet2);
            if(name != null){
                hasName = true;
            }
        }else{
            link = this.connexionsNames.get(objet2);
            if(link != null) {
                name = link.get(objet1);
                if(name != null){
                    hasName = true;
                }
            }
        }
        return hasName;
    }

    public int getTaille() {
        return taille;
    }
}

