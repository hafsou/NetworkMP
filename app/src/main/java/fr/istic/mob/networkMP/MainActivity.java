package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView planAppartement;
    //to save X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    //to draw connection, object
    private DrawView drawView;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        planAppartement = findViewById(R.id.planAppartement);
        drawView = findViewById(R.id.drawview);
        graph = new Graph();

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
                return true;
            case R.id.modifications_objets_connexions:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void renitialiserReseau(){

    }

    @SuppressLint("ClickableViewAccessibility")
    public void ajoutObjets(){

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

        planAppartement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                float X = lastTouchDownXY[0];
                float Y = lastTouchDownXY[1];
                String coordinates = X + " "+ Y;
                Toast.makeText(getApplicationContext(),coordinates,Toast.LENGTH_LONG).show();
                graph.addObjet(getApplicationContext(), "montre",X,Y);
                drawView.setObjets(graph.getObjets());
                drawView.invalidate();
                return true;
            }
        });
    }
}