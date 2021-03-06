package com.example.paseandopaseador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {//comentario
    RequestQueue requestQueue;
    String nombre;
    String correo;
    String id;
    String contrasenia;
    EditText txtCorreo,txtContrasenia;
    Switch swConecta;
    Button btnActualiza;
    Codigos c = new Codigos();

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //linea para trabajar solo con la orientacion vertical
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        txtCorreo = (EditText) findViewById(R.id.txtCorreoL);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        btnActualiza = (Button) findViewById(R.id.btnActualizarNav);
        swConecta = (Switch) findViewById(R.id.swConetate);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        validaSesion();

    }


    public void ctrlBotonIngresar(View view)
    {
        //String url2 = "http://192.168.100.119/prueba/buscar_paseador.php?correo="+txtCorreo.getText().toString()+"";
       // String url2 = c.direccionIP+"buscar_paseador.php?correo="+txtCorreo.getText().toString()+"";
        //buscarPaseador(url2);

        final String str_correo = txtCorreo.getText().toString().trim();
        String str_contrasena = txtContrasenia.getText().toString().trim();

        //obtenemos los valores de los EditText
        Codigos c = new Codigos ();
        boolean vacios = false;
        //validamos que no esten vacias
        if (TextUtils.isEmpty(str_correo)){
            txtCorreo.setError("Requerido");
            vacios=true;
        }
        if (TextUtils.isEmpty(str_contrasena)){
            txtContrasenia.setError("Requerido");
            vacios=true;
        }
        if (vacios){
            return;
        }
        if (!c.validacionCorreo(str_correo)){
            txtCorreo.setError("Invalido");
            return;
        }
        if (str_contrasena.length()<6){
            txtContrasenia.setError("Minimo 6 caracteres");
            return;
        }
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        //creacion del nuevo usuario
        mAuth.signInWithEmailAndPassword(str_correo, str_contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            if (mAuth.getCurrentUser().isEmailVerified()){
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                Query query = db.collection("usuarios").whereEqualTo("id", firebaseUser.getUid());
                                query
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(getApplicationContext(), "Exito", Toast.LENGTH_LONG).show();
                                                    String nombre = "";
                                                    String correo = "";
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        nombre = document.get("nombre").toString();
                                                        correo = document.get("correo").toString();
                                                    }
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplication(),"Bienvenido "+nombre,Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplication(), PaseAndoNavi.class);
                                                    /*
                                                    intent.putExtra("datoNombre",nombre);
                                                    intent.putExtra("datoCorreo",correo);
                                                     */
                                                    preferencias(correo,nombre);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this,"Correo sin verificar",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void buscarPaseador(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        id = jsonObject.getString("id_paseador");
                        nombre = jsonObject.getString("nombre_paseador");
                        correo = jsonObject.getString("correo_paseador");
                        contrasenia = jsonObject.getString("contrsenia_paseador");
                        String sCo=txtCorreo.getText().toString();
                        String sPa= txtContrasenia.getText().toString();
                        //validaciones
                        if (c.hacerValidaciones){
                            if (sCo.isEmpty()||sPa.isEmpty()){
                                Toast.makeText(getApplicationContext(), "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
                            }else if(!c.validacionCorreo(sCo)){
                                Toast.makeText(getApplicationContext(), "Correo invalido", Toast.LENGTH_LONG).show();
                            }else if(!(sPa.length() >= 6)){
                                Toast.makeText(getApplicationContext(), "Se requiere una contraseña mayor a 5 caracteres", Toast.LENGTH_LONG).show();
                            }else{
                                if(sPa.equals(contrasenia)){
                                    txtCorreo.setText("");
                                    txtContrasenia.setText("");
                                    Toast.makeText(getApplicationContext(), "Iniciando...", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, PaseAndoNavi.class);
                                    intent.putExtra("datoId",id);
                                    intent.putExtra("datoNombre",nombre);
                                    intent.putExtra("datoCorreo",correo);
                                    intent.putExtra("datoContrasenia",contrasenia);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            if(sPa.equals(contrasenia)){
                                txtCorreo.setText("");
                                txtContrasenia.setText("");
                                Toast.makeText(getApplicationContext(), "Iniciando...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, PaseAndoNavi.class);
                                intent.putExtra("datoId",id);
                                intent.putExtra("datoNombre",nombre);
                                intent.putExtra("datoCorreo",correo);
                                intent.putExtra("datoContrasenia",contrasenia);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage()+"Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Intentalo de nuevo", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    String id_contrato;
    String latitud;
    String longitud;
    String id_paseador;
    String id_mascota;
    String hora_inicio;
    String hora_fin;
    String costo;


    public void buscarPaseo(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        id_contrato = jsonObject.getString("id_contrato");
                        latitud = jsonObject.getString("latitud");
                        longitud = jsonObject.getString("longitud");
                        id_paseador = jsonObject.getString("id_paseador");
                        id_mascota = jsonObject.getString("id_mascota");
                        hora_inicio = jsonObject.getString("hora_inicio");
                        hora_fin= jsonObject.getString("hora_fin");
                        costo = jsonObject.getString("costo");
                        Toast.makeText(getApplicationContext(), "XD...", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión xd", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void ctrlBtnReg(View view)
    {
        txtCorreo.setText("");
        txtContrasenia.setText("");
        Intent intent = new Intent(view.getContext(), Registro.class);
        startActivity(intent);
    }

    public void ctrlReestablecerPass(View view)
    {

        //obtenemos los valores de los EditText
        final String str_correo = txtCorreo.getText().toString().trim();
        Codigos c = new Codigos ();
        if (TextUtils.isEmpty(str_correo)){
            txtCorreo.setError("Requerido");
            return;
        }
        if (!c.validacionCorreo(str_correo)){
            Toast.makeText(getApplicationContext(), "Correo invalido", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(str_correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Dialog dialog = new Dialog("Reestablecer contraseña","Se te ha enciado un correo para reestablecer la contraseña");
                    dialog.show(getSupportFragmentManager(),"");
                }else{
                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    public void actualizar()
    {
        if (swConecta.isChecked())
        {
            buscarPaseo(c.direccionIP+"buscar_paseo.php"+id);
        }else
        {
            Toast.makeText(getApplicationContext(),"Conectate Primero",Toast.LENGTH_LONG).show();
        }
    }

    private void preferencias(String str_cor, String str_nom)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("correo_",str_cor);
        editor.putString("nombre_",str_nom);
        editor.commit();
    }

    public void actulizaIdContrato(String URL)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Actualizacion exitosa xd", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error al actualizar xd -> "+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_paseador",id);//txtNombre.getText().toString()
                //parametros.put("correo",correo);//txtCorreo.getText().toString()
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validaSesion()
    {
        String correo = preferences.getString("correo_", null);
        String nombre = preferences.getString("nombre_", null);

        if (correo != null && nombre != null)
        {
            finish();
            Intent intent = new Intent(getApplication(), PaseAndoNavi.class);
            startActivity(intent);
        }
    }



}
