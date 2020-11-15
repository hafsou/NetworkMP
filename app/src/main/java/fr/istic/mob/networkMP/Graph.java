package fr.istic.mob.networkMP;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.HashMap;

/**
 * This class represent the graph (network)
 * @author Loan et Hafsa
 */
public class Graph {

    public final static int SIZE = 60;
    private int objectId = 0;  //create object with unique id that auto-increments
    private HashMap<String,CustomRect> objects;  //name of the object with the object
    private HashMap<String, HashMap<String,CustomPath>> connections;  //HashMap<Name object1, Hashmap<Name object2, the path>>
    private HashMap<String, HashMap<String,ConnexionLabel>> connectionsLabels; //HashMap<Name object1, Hashmap<Name object2, the path label>>
    private HashMap<String, Integer> objectsColor; //name of the object with the color value
    private HashMap<String, Bitmap> objectsIcons; //name of the object with the bitmap value

    public Graph(){
        this.connections = new HashMap<String, HashMap<String,CustomPath>>();
        this.connectionsLabels = new HashMap<String, HashMap<String,ConnexionLabel>>();
        this.objects = new HashMap<String,CustomRect>();
        this.objectsColor = new HashMap<String, Integer>();
        this.objectsIcons = new HashMap<String, Bitmap>();
    }

    /**
     * Add an object in the network
     * @param context
     * @param objectName the name of the object
     * @param x coordinate
     * @param y coordinate
     */
    public void addObjet(Context context, String objectName, float x, float y){
        CustomRect rect = new CustomRect(objectName,x,y,x+SIZE,y+SIZE);
        String finalObjectName = "object_"+String.valueOf(objectId);
        objects.put(finalObjectName, rect);
        objectId++;
    }

    /**
     * Delete the object from the network, and the connections of the object
     * @param objectName the name of the object to delete
     */
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

    /**
     * Delete a connection between two object
     * @param nameRect1 name of the first object
     * @param nameRect2 name of the second object
     */
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
        connectionsLabels = new HashMap<String, HashMap<String,ConnexionLabel>>();
        objectId = 0;
    }

    /**
     * Add a label of a connection between two objects
     * @param object1 first object
     * @param object2 second object
     * @param connexionName the name of the connection
     */
    public void addConnexionLabel(String object1, String object2, String connexionName){
        HashMap<String,ConnexionLabel> link = this.connectionsLabels.get(object1);
        if(link != null){
            link.put(object2, new ConnexionLabel(connexionName));
            this.connectionsLabels.put(object1, link);
        }else{
            link = this.connectionsLabels.get(object2);
            if(link != null){
                link.put(object1, new ConnexionLabel(connexionName));
                this.connectionsLabels.put(object2, link);
            }else{
                link = new HashMap<String,ConnexionLabel>();
                link.put(object2, new ConnexionLabel(connexionName));
                this.connectionsLabels.put(object1, link);
            }
        }
    }

    /**
     * Return the label of a connection between two objects
     * @param object1 first object
     * @param object2 second object
     * @return the label or null if not present
     */
    public ConnexionLabel getConnexionLabel(String object1, String object2){
        ConnexionLabel result = null;
        HashMap<String,ConnexionLabel> link = this.connectionsLabels.get(object1);
        if(link != null) {
            result = link.get(object2);
        }else{
            link = this.connectionsLabels.get(object2);
            if(link != null) {
                result = link.get(object1);
            }
        }
        return result;
    }

    /**
     * Check if the connection has a name
     * @param object1 first object
     * @param object2 second object
     * @return
     */
    public boolean hasConnexionName(String object1, String object2){
        boolean hasName = false;
        ConnexionLabel label;
        HashMap<String,ConnexionLabel> link = this.connectionsLabels.get(object1);
        if(link != null) {
            label = link.get(object2);
            if(label != null){
                hasName = true;
            }
        }else{
            link = this.connectionsLabels.get(object2);
            if(link != null) {
                label = link.get(object1);
                if(label != null){
                    hasName = true;
                }
            }
        }
        return hasName;
    }

    public boolean isEmpty(){
        return this.objects.isEmpty();
    }

    public static int getSIZE() {
        return SIZE;
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

    public HashMap<String, HashMap<String, ConnexionLabel>> getConnectionsLabels() {
        return connectionsLabels;
    }
}

