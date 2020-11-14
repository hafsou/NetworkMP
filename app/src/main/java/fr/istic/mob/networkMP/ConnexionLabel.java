package fr.istic.mob.networkMP;

public class ConnexionLabel {

    private String label;
    private float x;
    private float y;
    private int size = 30;
    private float height;
    private float width;

    public ConnexionLabel(String label){
        this.label = label;
        this.x = 0;
        this.y = 0;
        this.height = 30;
        this.width = 30;
    }

    public String getLabel() {
        return label;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setX(float x) {
        this.x = x;
        this.height = this.x + this.size;
    }

    public void setY(float y) {
        this.y = y;
        this.width = this.y - this.size;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
