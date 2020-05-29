package com.example.paseandopaseador;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SolicitudPaseoActivity extends AppCompatActivity {

    Button btnAcpetaPas;
    Button btnIrPaseo;
    TextView txtDatos;
    Codigos c = new Codigos();
    String id_contrato;
    String latitud;
    String longitud;
    String id_paseador;
    String id_mascota;
    String hora_inicio;
    String hora_fin;
    String costo;
    String direc;
    RequestQueue requestQueue;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    private ProgressDialog progressDialog;
    public String str_selectPaseo = "";
    public String paseoId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_paseo);
        btnAcpetaPas = (Button) findViewById(R.id.btnAcpetaPas);
        btnIrPaseo = (Button) findViewById(R.id.btnPaseoListo);
        btnIrPaseo.setVisibility(View.INVISIBLE);
        txtDatos = (TextView) findViewById(R.id.txtDatos);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        selectPaseo();


    }



    public  void selectPaseo(){

        progressDialog.setMessage("Procesando...");
        progressDialog.show();

        CollectionReference cR = db.collection("paseos");
        cR
                .whereEqualTo("id_paseador",firebaseUser.getUid())
                //.whereEqualTo("id_paseador","r4DDJlMRKmNMR68cfpQiJPK8m9R2")
                .whereIn("status", Arrays.asList("1", "2"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            String temp = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                temp = document.getId();
                                break;
                            }
                            if (!temp.equals("")){
                                progressDialog.dismiss();
                                txtDatos.setText("*****   Tienes un servicio en proceso   *****\n*****       FAVOR DE REALIZARLO       *****");
                                btnAcpetaPas.setEnabled(false);
                                btnAcpetaPas.setVisibility(View.INVISIBLE);
                                return;
                            }else{
                                CollectionReference collectionReference = db.collection("paseos");
                                collectionReference
                                        .whereEqualTo("status", "0")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(getApplicationContext(), "Exito", Toast.LENGTH_LONG).show();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (!document.get("id_usuario").toString().equals(firebaseUser.getUid())){
                                                        //if (!document.get("id_usuario").toString().equals("r4DDJlMRKmNMR68cfpQiJPK8m9R2")){
                                                            paseoId = document.getId();
                                                            latitud =  document.get("latitud").toString();
                                                            longitud = document.get("longitud").toString();
                                                            str_selectPaseo
                                                                    // "Id_usuario: " + document.get("id_usuario").toString()+ "\n"
                                                                    = "Latitud:    " + latitud + "\n"
                                                                    + "Longitud:   " + longitud  + "\n"
                                                                    + "Duracion:   " + document.get("duracion").toString() + " min\n"
                                                                    + "Hora inico: " + document.get("hora_inico").toString() + "\n"
                                                                    + "Hora fin:   " + document.get("hora_fin").toString() + "\n"
                                                                    + "Pago:        $" + document.get("costo").toString() + ".00 mxn\n";
                                                            break;
                                                        }
                                                    }
                                                    progressDialog.dismiss();
                                                    if (str_selectPaseo.equals("")){
                                                        txtDatos.setText("*****   No hay paseos disponibles   *****");
                                                        btnAcpetaPas.setEnabled(false);
                                                        btnAcpetaPas.setVisibility(View.INVISIBLE);
                                                        return;
                                                    }else{
                                                        txtDatos.setText(str_selectPaseo);
                                                        return;
                                                    }
                                                } else {
                                                    progressDialog.dismiss();
                                                    btnAcpetaPas.setEnabled(false);
                                                    btnAcpetaPas.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                        });
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            btnAcpetaPas.setEnabled(false);
                            btnAcpetaPas.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                });
    }

    public void aceptarPaseo(View view) {
        //consultaDatosPaseo(c.direccionIP + "select_all_contrato.php");
        if (str_selectPaseo == ""){
            Toast.makeText(getApplicationContext(), "No hay paseos disponibles", Toast.LENGTH_LONG).show();
            return;
        }
        //se hace el update para registrar que el paseo ay tiene paseador
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        DocumentReference docRef = db.collection("paseos").document(paseoId);
        Map<String,Object> updates = new HashMap<>();
        updates.put("id_paseador", firebaseUser.getUid());
        updates.put("id", paseoId);
        updates.put("status", "1");
        docRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Proceso exitoso", Toast.LENGTH_LONG).show();
                            btnAcpetaPas.setVisibility(View.INVISIBLE);
                            btnIrPaseo.setVisibility(View.VISIBLE);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

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



                        /*txtDatos.setText("Coordenadas de llegada: \n" + latitud + "," + longitud + "\n" + "De: "+hora_inicio+"-"+hora_fin+" hrs");
                        Toast.makeText(getApplicationContext(), "Ir al paseo...", Toast.LENGTH_SHORT).show();*/


                        buscarMascota(c.direccionIP + "buscar_mascota.php?id_mascota=" + id_mascota);


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


    public void irDestino(View view) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> direccion = geocoder.getFromLocation(Double.parseDouble(latitud), Double.parseDouble(longitud),1);
            direc = direccion.get(0).getAddressLine(0);
            //txtUbicacion.setText(direc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            // Launch Waze to look for Hawaii:
            String url = "https://waze.com/ul?q="+direc+"";
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }




    String idMas ;
    String idDue;
    String nombreMas;
    String tamanio;
    String cuidados;
    String raza;
    String edad;
    String seguro;
    public void buscarMascota(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        idMas = jsonObject.getString("id_mascota");
                        idDue = jsonObject.getString("id_duenio");
                        nombreMas = jsonObject.getString("nombre_mascota");
                        tamanio = jsonObject.getString("tamanio");
                        cuidados = jsonObject.getString("cuidados");
                        raza = jsonObject.getString("raza");
                        edad = jsonObject.getString("edad");
                        seguro = jsonObject.getString("id_seguro");
                        buscarDuenio(c.direccionIP+"buscar_duenio_id.php?id_duenio="+idDue+"");
                        //Toast.makeText(getApplicationContext(), "Iniciando...", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la conexion", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    public void buscarDuenio(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String d_id,d_nombre,d_correo,d_contrasenia,d_pasep;
                        jsonObject = response.getJSONObject(i);
                        d_id = jsonObject.getString("id_duenio");
                        d_nombre = jsonObject.getString("nombre");
                        d_correo = jsonObject.getString("correo");
                        d_contrasenia = jsonObject.getString("contrasenia");
                        d_pasep = jsonObject.getString("paseo");

                        txtDatos.setText("Coordenadas de llegada: " + latitud + "," + longitud + "\n" + "De: "
                                +hora_inicio+"-"+hora_fin+" hrs"+"\nMascota: "+nombreMas+"\nDel usuario: "+d_nombre);
                        Toast.makeText(getApplicationContext(), "Ir al paseo...", Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la conexion xd", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}
