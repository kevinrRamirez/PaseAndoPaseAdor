package com.example.paseandopaseador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PaseAndoNavi extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    //Button btnPrueba = (Button) findViewById(R.id.btnPrueba);
    TextView txtCorreoNav;
    TextView txtNombreNav;
    RequestQueue requestQueue;
    Switch swConecta;
    String id;
    Codigos c;
    private SharedPreferences preferences;

    private ProgressDialog progressDialog;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pase_ando_navi);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_perfil, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_acerca, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //linea para trabajar solo con la orientacion vertical
/*
        Bundle datoCorreo = getIntent().getExtras();
        String correo = datoCorreo.getString("datoCorreo");
        String nombre = datoCorreo.getString("datoNombre");
        //id = datoCorreo.getString("datoId");

 */




        View headerView = navigationView.getHeaderView(0);
        txtCorreoNav = (TextView) headerView.findViewById(R.id.txtCorreoNav);
       // txtCorreoNav.setText(nombre);

        txtNombreNav = (TextView) headerView.findViewById(R.id.txtNombreNav);
        //txtNombreNav.setText(correo);
        swConecta = (Switch) findViewById(R.id.swConetate);

        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String correo = preferences.getString("correo_", null);
        String nombre = preferences.getString("nombre_", null);

        if (correo != null && nombre != null)
        {
            txtCorreoNav.setText(nombre);
            txtNombreNav.setText(correo);
        }


    }

    public void ctrlBtnMiServicio(View view) {
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        CollectionReference collectionReference = db.collection("paseos");
        collectionReference
                .whereIn("status", Arrays.asList("1","2"))
                .whereEqualTo("id_paseador", firebaseUser.getUid())
                //.whereEqualTo("id_paseador", "r4DDJlMRKmNMR68cfpQiJPK8m9R2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String str_status = "";
                            String str_infoPaseo = "";
                            String str_idPaseo = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                str_status = document.get("status").toString();
                                str_idPaseo = document.get("id").toString();
                                str_infoPaseo = "***********        DETALLES         **********\n\n"+
                                        "Pago:        $" + document.get("costo").toString() + ".00 mxn\n"+
                                        "Duracion:    "+document.get("duracion").toString()+"min\n"+
                                        "Hora inicio: "+document.get("hora_inico").toString()+"\n"+
                                        "Hora fin:    "+document.get("hora_fin").toString()+"\n\n" +
                                        "**********************************************\n\n";
                            }
                            if (str_status.equals("")){
                                progressDialog.dismiss();
                                Dialog dialog = new Dialog("Aviso","No tienes ningun servicio activo por el momento");
                                dialog.show(getSupportFragmentManager(),"");
                                return;
                            }
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplication(), MiServicioActivity.class);
                            intent.putExtra("str_idPaseo",str_idPaseo);
                            intent.putExtra("str_infoPaseo",str_infoPaseo);
                            intent.putExtra("str_status",str_status);
                            startActivity(intent);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void prbBotonActualizar(View view) {
        Intent intent = new Intent(view.getContext(), SolicitudPaseoActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pase_ando_navi, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Uri uri = Uri.parse("https://paseando.bss.design/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    public void actualizar(View view)
    {
     //consultaDatosPaseo(c.direccionIP+"select_all_contrato.php");
    }

    String id_contrato;
    String latitud;
    String longitud;
    String id_paseador;
    String id_mascota;
    String hora_inicio;
    String hora_fin;
    String costo;

    /*
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
*/
}
