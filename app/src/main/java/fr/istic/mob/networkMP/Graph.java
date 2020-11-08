package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import java.util.HashMap;

public class Graph {

    private final static int SIZE = 70;
    private int nbObject = 0;
    private HashMap<String,RectF> objects;
    private HashMap<String, HashMap<String,CustomPath>> connexions;
    private HashMap<String, HashMap<String,String>> connexionsNames;
    private HashMap<String, Integer> objectsColor;
    private HashMap<String, Bitmap> objectsIcons;

    public Graph(){
        connexions = new HashMap<String, HashMap<String,CustomPath>>();
        connexionsNames = new HashMap<String, HashMap<String,String>>();
        objects = new HashMap<String,RectF>();
        objectsColor = new HashMap<String, Integer>();
        objectsIcons = new HashMap<String, Bitmap>();
    }

    public void addObjet(Context context, String objectName, float x, float y){
        RectF rect = new RectF(x,y,x+SIZE,y+SIZE);
        String finalObjectName = objectName+"_"+String.valueOf(nbObject);
        objects.put(finalObjectName, rect);
        nbObject++;
    }

    public void deleteObject(String objectName){
        this.objects.remove(objectName);
        HashMap<String, HashMap<String,CustomPath>> connexionsTmp = new HashMap<String, HashMap<String,CustomPath>>();
        //suppression des connexions associées
        for(String object1 : connexions.keySet()){
            for(String object2 : connexions.get(object1).keySet()){
                HashMap<String,CustomPath> linkToObject2 = connexions.get(object1);
                if(linkToObject2 != null) {
                    if((!object1.equals(objectName) && !object2.equals(objectName))){
                        HashMap<String,CustomPath> tmp = connexionsTmp.get(object1);
                        if(tmp != null){
                            tmp.put(object2,linkToObject2.get(object2));
                        }else {
                            HashMap<String, CustomPath> linkTmp = new HashMap<String, CustomPath>();
                            linkTmp.put(object2, linkToObject2.get(object2));
                            connexionsTmp.put(object1, linkTmp);
                        }
                    }

                }
            }
        }
        this.connexions = connexionsTmp;
        this.nbObject--;
    }

    public HashMap<String, RectF> getObjects() {
        return objects;
    }

    public HashMap<String, HashMap<String, CustomPath>> getConnexions() {
        return connexions;
    }

    public void reinitialize(){
        objects = new HashMap<String,RectF>();
        connexions = new HashMap<String, HashMap<String,CustomPath>>();
        connexionsNames = new HashMap<String, HashMap<String,String>>();
        nbObject = 0;
    }

    public void addConnexionName(String object1, String object2, String connexionName){
        HashMap<String,String> link = this.connexionsNames.get(object1);
        if(link != null){
            link.put(object2,connexionName);
            this.connexionsNames.put(object1, link);
        }else{
            link = this.connexionsNames.get(object2);
            if(link != null){
                link.put(object1, connexionName);
                this.connexionsNames.put(object2, link);
            }else{
                link = new HashMap<String,String>();
                link.put(object2,connexionName);
                this.connexionsNames.put(object1, link);
            }
        }
    }

    public String getConnexionName(String object1, String object2){
        String result = "";
        HashMap<String,String> link = this.connexionsNames.get(object1);
        if(link != null) {
            result = link.get(object2);
        }else{
            link = this.connexionsNames.get(object2);
            if(link != null) {
                result = link.get(object1);
            }
        }
        return result;
    }

    public boolean hasConnexionName(String object1, String object2){
        boolean hasName = false;
        String name;
        HashMap<String,String> link = this.connexionsNames.get(object1);
        if(link != null) {
            name = link.get(object2);
            if(name != null){
                hasName = true;
            }
        }else{
            link = this.connexionsNames.get(object2);
            if(link != null) {
                name = link.get(object1);
                if(name != null){
                    hasName = true;
                }
            }
        }
        return hasName;
    }

    public static int getSIZE() {
        return SIZE;
    }

    public int getNbObject(){
        return nbObject;
    }
    public void setObjects(HashMap<String,RectF> objects){
        this.objects = objects;
    }

    public void setObjectsColor(HashMap<String,Integer> objectsColor){
        this.objectsColor = objectsColor;
    }

    public HashMap<String, Integer> getObjectsColor(){
        return this.objectsColor;
    }

    public HashMap<String, Bitmap> getObjectsIcons() {
        return objectsIcons;
    }

    public void setObjectsIcons(HashMap<String, Bitmap> objectsIcons) {
        this.objectsIcons = objectsIcons;
    }
}

