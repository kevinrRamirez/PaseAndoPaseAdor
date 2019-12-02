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

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String nombre;
    String correo;
    String id;
    String contrasenia;
    EditText txtCorreo,txtPass;
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
        txtPass = (EditText) findViewById(R.id.txtPass);
        btnActualiza = (Button) findViewById(R.id.btnActualizar);
        swConecta = (Switch) findViewById(R.id.swConetate);
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
                        loDeIntent();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage()+"hhhhh", Toast.LENGTH_SHORT).show();
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

    public void actualizar()
    {
        if (swConecta.isChecked())
        {

        }
    }

    public void ctrlBotonIngresar(View view)
    {
        String url = c.direccionIP+"buscar_paseador.php?correo="+txtCorreo.getText().toString()+"";
        buscarPaseador(url);
    }

    public void loDeIntent() {
        String sCo, sPa;
        sCo=txtCorreo.getText().toString();
        sPa=txtPass.getText().toString();
        if (c.hacerValidaciones = true) {
            if (sCo.isEmpty() || sPa.isEmpty()) {
                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
            } else if (!c.validacionCorreo(sCo)) {
                Toast.makeText(this, "Correo invalido", Toast.LENGTH_LONG).show();
            } else if (!(sPa.length() >= 6)) {
                Toast.makeText(this, "Se requiere una contraseña mayor a 5 caracteres", Toast.LENGTH_LONG).show();
            } else if (!sPa.equals(contrasenia)) {
                Toast.makeText(this, "Verifica la contraseña", Toast.LENGTH_LONG).show();
            }else {

                Intent intent = new Intent(MainActivity.this, PaseAndoNavi.class);
                intent.putExtra("datoId", id);
                intent.putExtra("datoNombre", nombre);
                intent.putExtra("datoCorreo", correo);
                intent.putExtra("datoContrasenia", contrasenia);
                startActivity(intent);
            }

            } else  if (!sPa.equals(contrasenia)){
            Toast.makeText(this, "Verifica la contraseña", Toast.LENGTH_LONG).show();

            }else{
            Intent intent = new Intent(MainActivity.this, PaseAndoNavi.class);
            intent.putExtra("datoId", id);
            intent.putExtra("datoNombre", nombre);
            intent.putExtra("datoCorreo", correo);
            intent.putExtra("datoContrasenia", contrasenia);
            startActivity(intent);

        }
        }

    public void buscarPaseo(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        /*textView1.setText(jsonObject.getString("id_duenio"));
                        textView1.setText(jsonObject.getString("nombre"));
                        textView1.setText(jsonObject.getString("correo"));
                        textView1.setText(jsonObject.getString("contrasenia"));
                        textView1.setText(jsonObject.getString("paseo"));*/
                        //textView1.setText(jsonObject.getString("correo")+"--"+jsonObject.getString("contrasenia"));
                        //obtenerCorreo= jsonObject.optString("correo");
                        //obtenerPass=jsonObject.optString("contrasenia");
                        //s=jsonObject.getString("id_duenio")+jsonObject.getString("nombre")+jsonObject.getString("correo")+jsonObject.getString("contrasenia")+jsonObject.getString("paseo");
                        //textView1.setText(s);
                        id = jsonObject.getString("id_duenio");
                        nombre = jsonObject.getString("nombre");
                        //corre = jsonObject.getString("correo");
                        contrasenia = jsonObject.getString("contrasenia");
                        //paseo = jsonObject.getString("paseo");

                        Toast.makeText(getApplicationContext(), "Iniciando...", Toast.LENGTH_SHORT).show();

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


    public void ctrlBotonIngresar(View view)
    {
        Intent intent = new Intent(view.getContext(), PaseAndoNavi.class);
        intent.putExtra("datoId",id);
        intent.putExtra("datoNombre",nombre);
        intent.putExtra("datoCorreo",correo);
        intent.putExtra("datoContrasenia",contrasenia);
        startActivity(intent);

        String url = "http://192.168.100.119/prueba/buscar_paseador.php?correo="+txtCorreo.getText().toString()+"";
        buscarPaseador(url);
        actulizaIdContrato("http://192.168.100.119/prueba/update_paseador.php");
    }




    public void ctrlBtnReg(View view)
    {
        Intent intent = new Intent(view.getContext(), Registro.class);
        startActivity(intent);
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
