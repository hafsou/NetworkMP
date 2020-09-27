package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ImageView planAppartement;
    //to save X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    //to draw connection, object
    private DrawView drawView;
    private Graph graph;
    private String objectName = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        planAppartement = findViewById(R.id.planAppartement);
        drawView = findViewById(R.id.drawview);
        graph = new Graph();

        planAppartement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //save the X,Y coordinates
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    lastTouchDownXY[0] = event.getX();
                    lastTouchDownXY[1] = event.getY();
                }
                //let the touch event pass on to whover needs it
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.renitialiser_reseau:
                renitialiserReseau();
                return true;
            case R.id.ajout_objets:
                ajoutObjets();
                return true;
            case R.id.ajout_connexions:
                ajoutConnexions();
                return true;
            case R.id.modifications_objets_connexions:
                modificationsObjetsConnexions();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void renitialiserReseau(){

    }

    public void ajoutConnexions(){
        drawView.setMode(Mode.CONNEXIONS);
        planAppartement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        planAppartement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float x = lastTouchDownXY[0];
                final float y = lastTouchDownXY[1];

                boolean touchObject = false;
                RectF rectToMove = null;
                HashMap<String,RectF> objects = graph.getObjects();
                for(String nameRect : objects.keySet()){
                    RectF rect = objects.get(nameRect);
                    System.out.println("right = "+ rect.right+" left = "+rect.left+" top = "+ rect.top +" bottom = "+ rect.bottom);
                    if(x<= rect.right && x>= rect.left && y>= rect.top && y<=rect.bottom){
                        touchObject = true;
                        rectToMove = rect;
                    }
                }
                if(touchObject == true) {
                    graph.addConnexion(getApplicationContext(),"test",x,y);
                    //drawView.setConnexions(graph.getConnexions());7
                    drawView.setObjectsConnexions(graph.getObjectsConnexions());
                    drawView.invalidate();
                }
            }
        });

    }

    public void modificationsObjetsConnexions(){

    }

    @SuppressLint("ClickableViewAccessibility")
    public void ajoutObjets(){
        drawView.setMode(Mode.OBJETS);
        planAppartement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final float x = lastTouchDownXY[0];
                final float y = lastTouchDownXY[1];

                boolean touchObject = false;
                RectF rectToMove = null;
                HashMap<String,RectF> objects = graph.getObjects();
                for(String nameRect : objects.keySet()){
                    RectF rect = objects.get(nameRect);
                    System.out.println("right = "+ rect.right+" left = "+rect.left+" top = "+ rect.top +" bottom = "+ rect.bottom);
                    if(x<= rect.right && x>= rect.left && y>= rect.top && y<=rect.bottom){
                        touchObject = true;
                        rectToMove = rect;
                    }
                }
                if(touchObject == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Nom de l'objet");
                    // Set up the input
                    final EditText input = new EditText(MainActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objectName = input.getText().toString();
                            graph.addObjet(getApplicationContext(), objectName, x, y);
                            drawView.setObjects(graph.getObjects());
                            drawView.invalidate();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objectName = null;
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                return true;
            }
        });
    }
}