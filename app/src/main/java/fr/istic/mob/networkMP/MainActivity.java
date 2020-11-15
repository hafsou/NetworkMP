package fr.istic.mob.networkMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //to save X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    //to draw connections, objects..
    private DrawView drawView;
    private String objectName = null; //temporary string
    private String newObjectName = null; //temporary string
    private Button choosePlan;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HashMap<String,Drawable> plansImages;
    private static final int FILE_SELECT_CODE = 0;
    private SharedPreferences mPrefs;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPrefs = getPreferences(MODE_PRIVATE);
        drawView = findViewById(R.id.drawview);
        choosePlan = findViewById(R.id.button_choose_plan);
        plansImages = new HashMap<String,Drawable>();
        plansImages.put(getResources().getString(R.string.default_map),getDrawable(R.drawable.plan));
        plansImages.put(getResources().getString(R.string.map_t2), getDrawable(R.drawable.plandeux));
        plansImages.put(getResources().getString(R.string.map_t3), getDrawable(R.drawable.plantrois));
        choosePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.popup_plan));
                View rowList = getLayoutInflater().inflate(R.layout.row, null);
                listView = rowList.findViewById(R.id.listView);
                Button import_button = rowList.findViewById(R.id.import_image);
                String[] names = new String[plansImages.keySet().size()];
                int i = 0;
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
                        drawView.setBackground(plansImages.get(planName));
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //save the X,Y coordinates
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    lastTouchDownXY[0] = event.getX();
                    lastTouchDownXY[1] = event.getY();
                }
                //let the touch event pass on to whoever needs it
                return false;
            }
        });

    }

    /**
     *
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file_upload)),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getResources().getString(R.string.install_file_manager),Toast.LENGTH_LONG).show();
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
                        Bitmap b = BitmapFactory.decodeStream(inputStream);
                        b.setDensity(Bitmap.DENSITY_NONE);
                        Drawable imagePlan = new BitmapDrawable(getResources(),b);
                        drawView.setBackground(imagePlan);
                        plansImages.put(name,imagePlan);
                    } catch (FileNotFoundException e) {
                        Drawable imagePlan = getDrawable(R.drawable.plan);
                        drawView.setBackground(imagePlan);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.renitialiser_reseau:
                reinitializeNetwork();
                return true;
            case R.id.ajout_objets:
                addObjects();
                return true;
            case R.id.ajout_connexions:
                addConnexions();
                return true;
            case R.id.modifications_objets_connexions:
                modifyObjectsOrConnexions();
                return true;
            case R.id.save_network:
                saveNetwork();
                return true;
            case R.id.upload_network:
                upload_network();
                return true;
            case R.id.send_mail:
                screenshot();
                return true;
            case R.id.curve_connections:
                curveConnections();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reinitializeNetwork(){
        drawView.reinitializeGraph();
        drawView.invalidate();
    }

    public void saveNetwork(){
        final SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomPath.class, new PathSerializer()).setPrettyPrinting().create();
        final String json = gson.toJson(drawView.getGraph());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_network_name));
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                objectName = input.getText().toString();
                prefsEditor.putString(objectName, json);
                prefsEditor.commit();

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void upload_network(){
        final Gson gson = new GsonBuilder().registerTypeAdapter(CustomPath.class, new PathDeserializer()).create();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_network));
        Map<String,?> test = mPrefs.getAll();
        final String[] choices = test.keySet().toArray(new String[0]);
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String json = mPrefs.getString(choices[which], "");
                Graph graph = gson.fromJson(json, Graph.class);
                drawView.setGraph(graph);
                drawView.invalidate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void addConnexions(){
        drawView.setMode(Mode.CONNEXIONS);
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public void modifyObjectsOrConnexions(){
        drawView.setMode(Mode.MODIFICATIONS);
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final float x = lastTouchDownXY[0];
                final float y = lastTouchDownXY[1];
                boolean touchObject = false;
                CustomRect rectTouched = null;
                final Graph graph = drawView.getGraph();
                final HashMap<String,CustomRect> objects = graph.getObjects();
                final HashMap<String, HashMap<String,ConnexionLabel>> connectionNames = graph.getConnectionsNames();
                HashMap<String, HashMap<String,CustomPath>> connections = graph.getConnexions();
                String rect1ToDeleteTmp = null;
                String rect2ToDeleteTmp = null;
                CustomPath customPathNameTouched = null;
                ConnexionLabel connectionLabelTouched = null;
                boolean touchConnectionLabel = false;
                for(String nameRect : objects.keySet()){
                    CustomRect rect = objects.get(nameRect);
                    if(x<= rect.right && x>= rect.left && y>= rect.top && y<=rect.bottom){
                        touchObject = true;
                        rectTouched = rect;
                        objectName = nameRect;
                    }
                }
                for(String rect1 : connectionNames.keySet()){
                    HashMap<String,ConnexionLabel> connexionToRect2 = connectionNames.get(rect1);
                    for(String rect2 : connexionToRect2.keySet()){
                        ConnexionLabel connexionLabel = connexionToRect2.get(rect2);
                        if(x<= connexionLabel.getHeight() && x>= connexionLabel.getX() && y<= connexionLabel.getY() && y>=connexionLabel.getWidth()){
                            touchConnectionLabel = true;
                            connectionLabelTouched = connexionLabel;
                            customPathNameTouched = connections.get(rect1).get(rect2);
                            rect1ToDeleteTmp = rect1;
                            rect2ToDeleteTmp = rect2;
                        }
                    }
                }
                if(touchObject == true) {
                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getResources().getString(R.string.choose_action));

                    // add a list
                    String[] choices = {getResources().getString(R.string.delete_object_action)
                            ,getResources().getString(R.string.modify_label_action)
                            ,getResources().getString(R.string.modify_color_action)
                            , getResources().getString(R.string.choose_icon_action)};
                    final CustomRect finalRectTouched = rectTouched;
                    builder.setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // delete object
                                    graph.deleteObject(objectName);
                                    drawView.invalidate();
                                    break;
                                case 1: //modify label
                                    changeObjectName(objectName);
                                    break;
                                case 2: //modify color
                                    changeObjectColor(objectName);
                                    break;
                                case 3: //choose icon
                                    changeObjectIcon(objectName);
                                    break;
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(touchConnectionLabel == true){
                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getResources().getString(R.string.choose_action));

                    // add a list
                    String[] choices = {getResources().getString(R.string.delete_connection)
                            ,getResources().getString(R.string.modify_connection_label)
                            ,getResources().getString(R.string.modify_connection_color)
                            , getResources().getString(R.string.modify_connection_width)};
                    final ConnexionLabel finalConnexionLabelTouched = connectionLabelTouched;
                    final CustomPath finalCustomPathNameTouched = customPathNameTouched;
                    final String finalRect1ToDeleteTmp = rect1ToDeleteTmp;
                    final String finalRect2ToDeleteTmp = rect2ToDeleteTmp;
                    builder.setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // delete object
                                    graph.deleteConnection(finalRect1ToDeleteTmp, finalRect2ToDeleteTmp);
                                    drawView.invalidate();
                                    break;
                                case 1: //modify connection label
                                    changeConnectionLabel(finalConnexionLabelTouched);
                                    break;
                                case 2: //modify connection color
                                    changeConnectionColor(finalCustomPathNameTouched);
                                    break;
                                case 3: //modify connection width
                                    changeConnectionWidth(finalCustomPathNameTouched);
                                    break;
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addObjects(){
        drawView.setMode(Mode.OBJETS);
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final float x = lastTouchDownXY[0];
                final float y = lastTouchDownXY[1];
                boolean touchObject = false;
                CustomRect rectToMove = null;
                final Graph graph = drawView.getGraph();
                HashMap<String,CustomRect> objects = graph.getObjects();
                for(String nameRect : objects.keySet()){
                    CustomRect rect = objects.get(nameRect);
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

    private void screenshot(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        View view = drawView.getRootView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        File imagePath = new File(new File(Environment.getExternalStorageDirectory(), "Pictures"), "screenshot.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
            sendEmailIntent.setType("image/png");
            sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"adress"});
            sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
            sendEmailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imagePath));
            startActivity(Intent.createChooser(sendEmailIntent, "Send email"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeObjectName(final String oldName){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_title));
        final Graph graph = drawView.getGraph();
        final EditText input = new EditText(MainActivity.this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        final HashMap<String,CustomRect> objects = graph.getObjects();
        final HashMap<String,Integer> objectsColor = graph.getObjectsColor();
        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newObjectName = input.getText().toString();
                CustomRect rect = objects.get(oldName);
                rect.setName(newObjectName);
                drawView.invalidate();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newObjectName = null;
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void changeObjectColor(final String objectName){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_choose_color));
        final Graph graph = drawView.getGraph();
        final HashMap<String,Integer> objectsColor = graph.getObjectsColor();
        // add a list
        String[] choices = {getResources().getString(R.string.red)
                ,getResources().getString(R.string.green)
                ,getResources().getString(R.string.blue)
                ,getResources().getString(R.string.orange)
                ,getResources().getString(R.string.cyan)
                ,getResources().getString(R.string.magenta)
                ,getResources().getString(R.string.black)};
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        objectsColor.put(objectName,Color.RED);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 1:
                        objectsColor.put(objectName,Color.GREEN);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 2:
                        objectsColor.put(objectName,Color.BLUE);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 3:
                        objectsColor.put(objectName,Color.rgb(255, 165, 0));
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 4:
                        objectsColor.put(objectName,Color.CYAN);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 5:
                        objectsColor.put(objectName,Color.MAGENTA);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;
                    case 6:
                        objectsColor.put(objectName,Color.BLACK);
                        graph.setObjectsColor(objectsColor);
                        drawView.invalidate();
                        break;

                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeObjectIcon(final String oldName){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_choose_icon));
        final Graph graph = drawView.getGraph();
        final HashMap<String,Bitmap> objectsIcons = graph.getObjectsIcons();
        // add a list
        String[] choices = {getResources().getString(R.string.printer)
                ,getResources().getString(R.string.television)
                ,getResources().getString(R.string.laptop)};
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        objectsIcons.put(objectName,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.imprimante)
                                ,graph.getSIZE()
                                ,graph.getSIZE()
                                ,false));
                        graph.setObjectsIcons(objectsIcons);
                        drawView.invalidate();
                        break;
                    case 1:
                        objectsIcons.put(objectName, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.television)
                                ,graph.getSIZE()
                                ,graph.getSIZE()
                                ,false));
                        graph.setObjectsIcons(objectsIcons);
                        drawView.invalidate();
                        break;
                    case 2:
                        objectsIcons.put(objectName, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ordinateur)
                                ,graph.getSIZE()
                                ,graph.getSIZE()
                                ,false));
                        graph.setObjectsIcons(objectsIcons);
                        drawView.invalidate();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeConnectionLabel(final ConnexionLabel connexionLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_connection_name));
        final Graph graph = drawView.getGraph();
        final EditText input = new EditText(MainActivity.this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.confirmer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newConnexionLabelName = input.getText().toString();
                connexionLabel.setLabel(newConnexionLabelName);
                drawView.invalidate();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void changeConnectionColor(final CustomPath customPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_choose_color));
        final Graph graph = drawView.getGraph();
        final HashMap<String,Integer> objectsColor = graph.getObjectsColor();
        // add a list
        String[] choices = {getResources().getString(R.string.red)
                ,getResources().getString(R.string.green)
                ,getResources().getString(R.string.blue)
                ,getResources().getString(R.string.orange)
                ,getResources().getString(R.string.cyan)
                ,getResources().getString(R.string.magenta)
                ,getResources().getString(R.string.black)};
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        customPath.setColor(Color.RED);
                        drawView.invalidate();
                        break;
                    case 1:
                        customPath.setColor(Color.GREEN);
                        drawView.invalidate();
                        break;
                    case 2:
                        customPath.setColor(Color.BLUE);
                        drawView.invalidate();
                        break;
                    case 3:
                        customPath.setColor(Color.rgb(255, 165, 0));
                        drawView.invalidate();
                        break;
                    case 4:
                        customPath.setColor(Color.CYAN);
                        drawView.invalidate();
                        break;
                    case 5:
                        customPath.setColor(Color.MAGENTA);
                        drawView.invalidate();
                        break;
                    case 6:
                        customPath.setColor(Color.BLACK);
                        drawView.invalidate();
                        break;

                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeConnectionWidth(final CustomPath customPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_connection_width));
        final Graph graph = drawView.getGraph();
        final HashMap<String,Integer> objectsColor = graph.getObjectsColor();
        // add a list
        String[] choices = {getResources().getString(R.string.thin)
                ,getResources().getString(R.string.medium)
                ,getResources().getString(R.string.large)};
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        customPath.setStrokeWidth(5);
                        drawView.invalidate();
                        break;
                    case 1:
                        customPath.setStrokeWidth(10);
                        drawView.invalidate();
                        break;
                    case 2:
                        customPath.setStrokeWidth(15);
                        drawView.invalidate();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void curveConnections(){
        drawView.setMode(Mode.CURVES);
        drawView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }
}