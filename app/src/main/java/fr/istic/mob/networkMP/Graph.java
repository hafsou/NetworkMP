package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import java.util.HashMap;

public class Graph {

    public final static int SIZE = 60;
    private int objectId = 0;
    private HashMap<String,CustomRect> objects;
    private HashMap<String, HashMap<String,CustomPath>> connections;
    private HashMap<String, HashMap<String,ConnexionLabel>> connectionsNames;
    private HashMap<String, Integer> objectsColor;
    private HashMap<String, Bitmap> objectsIcons;

    public Graph(){
        this.connections = new HashMap<String, HashMap<String,CustomPath>>();
        this.connectionsNames = new HashMap<String, HashMap<String,ConnexionLabel>>();
        this.objects = new HashMap<String,CustomRect>();
        this.objectsColor = new HashMap<String, Integer>();
        this.objectsIcons = new HashMap<String, Bitmap>();
    }

    public void addObjet(Context context, String objectName, float x, float y){
        CustomRect rect = new CustomRect(objectName,x,y,x+SIZE,y+SIZE);
        String finalObjectName = "object_"+String.valueOf(objectId);
        objects.put(finalObjectName, rect);
        objectId++;
    }

    public void deleteObject(String objectName){
        this.objects.remove(objectName);
        HashMap<String, HashMap<String,CustomPath>> connexionsTmp = new HashMap<String, HashMap<String,CustomPath>>();
        //delete associated connections
        for(String object1 : connections.keySet()){
            for(String object2 : connections.get(object1).keySet()){
                HashMap<String,CustomPath> linkToObject2 = connections.get(object1);
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
        this.connections = connexionsTmp;
    }

    public void deleteConnection(String nameRect1, String nameRect2){
        HashMap<String,CustomPath> linkToObject2 = connections.get(nameRect1);
        if(linkToObject2 != null){
            linkToObject2.remove(nameRect2);
        }else{
            linkToObject2 = connections.get(nameRect2);
            if(linkToObject2 != null){
                linkToObject2.remove(nameRect1);
            }
        }
    }

    public HashMap<String, CustomRect> getObjects() {
        return objects;
    }

    public HashMap<String, HashMap<String, CustomPath>> getConnexions() {
        return connections;
    }

    public void reinitialize(){
        objects = new HashMap<String,CustomRect>();
        connections = new HashMap<String, HashMap<String,CustomPath>>();
        connectionsNames = new HashMap<String, HashMap<String,ConnexionLabel>>();
        objectId = 0;
    }

    public void addConnexionName(String object1, String object2, String connexionName){
        HashMap<String,ConnexionLabel> link = this.connectionsNames.get(object1);
        if(link != null){
            link.put(object2, new ConnexionLabel(connexionName));
            this.connectionsNames.put(object1, link);
        }else{
            link = this.connectionsNames.get(object2);
            if(link != null){
                link.put(object1, new ConnexionLabel(connexionName));
                this.connectionsNames.put(object2, link);
            }else{
                link = new HashMap<String,ConnexionLabel>();
                link.put(object2, new ConnexionLabel(connexionName));
                this.connectionsNames.put(object1, link);
            }
        }
    }

    public ConnexionLabel getConnexionName(String object1, String object2){
        ConnexionLabel result = null;
        HashMap<String,ConnexionLabel> link = this.connectionsNames.get(object1);
        if(link != null) {
            result = link.get(object2);
        }else{
            link = this.connectionsNames.get(object2);
            if(link != null) {
                result = link.get(object1);
            }
        }
        return result;
    }

    public boolean hasConnexionName(String object1, String object2){
        boolean hasName = false;
        ConnexionLabel label;
        HashMap<String,ConnexionLabel> link = this.connectionsNames.get(object1);
        if(link != null) {
            label = link.get(object2);
            if(label != null){
                hasName = true;
            }
        }else{
            link = this.connectionsNames.get(object2);
            if(link != null) {
                label = link.get(object1);
                if(label != null){
                    hasName = true;
                }
            }
        }
        return hasName;
    }

    public static int getSIZE() {
        return SIZE;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjects(HashMap<String,CustomRect> objects){
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

    public HashMap<String, HashMap<String, ConnexionLabel>> getConnectionsNames() {
        return connectionsNames;
    }
}

