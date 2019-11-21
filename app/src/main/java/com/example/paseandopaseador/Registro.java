package com.example.paseandopaseador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    EditText txtNombre;
    EditText txtCorreo;
    EditText txtPass;
    EditText txtPassConf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtPass = (EditText) findViewById(R.id.txtPass);
        txtPassConf = (EditText) findViewById(R.id.txtPassConf);
    }



    public void ctrlBtnRegistrar(View view)
    {
        servicio("http://192.168.1.66/paseando/registro_paseador.php"); //compu Orlas
        //servicio("http://192.168.209.23/paseando/registro_paseador.php"); //compu Kevin

        Intent intent = new Intent(view.getContext(), PaseAndoNavi.class);
        startActivity(intent);
    }



    public void ctrlBotonIngresar(View view)
    {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void servicio(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Regsitro exitoso xd", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en el registro xd -> "+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();

                parametros.put("nombre_paseador",txtNombre.getText().toString());
                parametros.put("correo_paseador",txtCorreo.getText().toString());
                parametros.put("contrasenia_paseador",txtPass.getText().toString());

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
