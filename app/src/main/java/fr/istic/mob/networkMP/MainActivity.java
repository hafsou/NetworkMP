package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //private ImageView planAppartement;
    //to save X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    //to draw connection, object
    private DrawView drawView;
    private String objectName = null;
    private Button choosePlan;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HashMap<String,Drawable> plansImages;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //planAppartement = findViewById(R.id.planAppartement);
        drawView = findViewById(R.id.drawview);
        choosePlan = findViewById(R.id.button_choose_plan);
        plansImages = new HashMap<String,Drawable>();
        plansImages.put("plan",getDrawable(R.drawable.plan));
        plansImages.put("plan T2", getDrawable(R.drawable.plandeux));
        plansImages.put("plan T3", getDrawable(R.drawable.plantrois));
        choosePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.popup_plan));
                View rowList = getLayoutInflater().inflate(R.layout.row, null);
                listView = rowList.findViewById(R.id.listView);
                Button import_button = rowList.findViewById(R.id.import_image);
                String[] names = new String[plansImages.keySet().size()];
                int i=0;
                for(String name : plansImages.keySet()){
                    names[i] = name;
                    i++;
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names );
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                builder.setView(rowList);
                final AlertDialog dialog = builder.create();
                import_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                        dialog.cancel();
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String planName = (String) parent.getItemAtPosition(position);
                        //planAppartement.setImageDrawable(plansImages.get(planName));
                        drawView.setBackground(plansImages.get(planName));
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        //planAppartement
        drawView.setOnTouchListener(new View.OnTouchListener() {
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

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    // Get the Uri of the selected file
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        int indexDelimiter = uri.toString().lastIndexOf("/");
                        String name = uri.toString().substring(indexDelimiter+1);
                        Drawable imagePlan = Drawable.createFromStream(inputStream, uri.toString() );
                        //planAppartement.setImageDrawable(imagePlan);
                        drawView.setBackground(imagePlan);
                        plansImages.put(name,imagePlan);
                    } catch (FileNotFoundException e) {
                        Drawable imagePlan = getDrawable(R.drawable.plan);
                        //planAppartement.setImageDrawable(imagePlan);
                        drawView.setBackground(imagePlan);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        drawView.reinitializeGraph();
        drawView.invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void ajoutConnexions(){
        drawView.setMode(Mode.CONNEXIONS);
        //planAppartement
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public void modificationsObjetsConnexions(){
        drawView.setMode(Mode.MODIFICATIONS);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void ajoutObjets(){
        drawView.setMode(Mode.OBJETS);
        //planAppartement
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final float x = lastTouchDownXY[0];
                final float y = lastTouchDownXY[1];
                //System.out.println("OHJFNDDDDDDDDDDDDDDDDDDDDDDDD");
                boolean touchObject = false;
                RectF rectToMove = null;
                final Graph graph = drawView.getGraph();
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
                    builder.setTitle(getResources().getString(R.string.popup_title));
                    // Set up the input
                    final EditText input = new EditText(MainActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objectName = input.getText().toString();
                            graph.addObjet(getApplicationContext(), objectName, x, y);
                            drawView.invalidate();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
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