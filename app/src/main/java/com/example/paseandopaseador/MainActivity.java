package com.example.paseandopaseador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        btnActualiza = (Button) findViewById(R.id.btnActualizarNav);
        swConecta = (Switch) findViewById(R.id.swConetate);
    }
    public void ctrlBotonIngresar(View view)
    {
        String url = c.direccionIP+"buscar_paseador.php?correo="+txtCorreo.getText().toString()+"";
        buscarPaseador(url);
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
        Intent intent = new Intent(view.getContext(), Registro.class);
        startActivity(intent);
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



}
