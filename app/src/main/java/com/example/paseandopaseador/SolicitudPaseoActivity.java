package com.example.paseandopaseador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SolicitudPaseoActivity extends AppCompatActivity {

    Button btnAcepta;
    Button btnIrPaseo;
    TextView txtDatos;
    Codigos c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_paseo);
        btnAcepta = (Button) findViewById(R.id.btnAcpetaPas);
        btnIrPaseo = (Button) findViewById(R.id.btnPaseoListo);
        btnIrPaseo.setVisibility(View.INVISIBLE);
        txtDatos = (TextView) findViewById(R.id.txtDatos);

        consultaDatosPaseo(c.direccionIP+"select_all_contrato.php");
    }

    String id_contrato;
    String latitud;
    String longitud;
    String id_paseador;
    String id_mascota;
    String hora_inicio;
    String hora_fin;
    String costo;
    RequestQueue requestQueue;

    public void consultaDatosPaseo(String URL) {
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
                        hora_fin = jsonObject.getString("hora_fin");
                        costo = jsonObject.getString("costo");


                        txtDatos.setText("Latitud"+latitud+"\n"+"Longitud"+longitud);
                        /*
                        Intent intent = new Intent(PaseAndoNavi.this, SolicitudPaseoActivity.class);
                        intent.putExtra("datoIdContrato",id_contrato);
                        intent.putExtra("datoLatitud",latitud);
                        intent.putExtra("datoLongitud",longitud);
                        intent.putExtra("datoIdPaseador",id_paseador);
                        intent.putExtra("datoIdMascota",id_mascota);
                        intent.putExtra("datoHoraIni",hora_inicio);
                        intent.putExtra("datoHoraFin",hora_fin);
                        intent.putExtra("datoCosto",costo);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Iniciando...", Toast.LENGTH_SHORT).show();

                         */


                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión xd#", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
