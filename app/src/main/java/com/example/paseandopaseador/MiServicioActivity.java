package com.example.paseandopaseador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiServicioActivity extends AppCompatActivity {

    TextView textView;
    Button btnOk;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    String str_idPaseo ;
    String str_infoPaseo ;
    String str_status ;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_servicio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);


        textView = (TextView) findViewById(R.id.textView);
        btnOk = (Button) findViewById(R.id.btnOk);

        Bundle b = getIntent().getExtras();
        str_idPaseo = b.getString("str_idPaseo");
        str_infoPaseo = b.getString("str_infoPaseo");
        str_status = b.getString("str_status");

        if ( str_idPaseo != null && str_infoPaseo != null && str_status != null){
            String temp = str_infoPaseo+"\n\n";
            if (str_status.equals("1")){
                temp += " Deberías estar en camino al destino\n" +
                        "      para llegar a tiempo :)      \n\n" +
                        "Presiona OK cuando llegado hayas llegado";
            }else if (str_status.equals("2")){
                temp += "        Es hora de dar un buen servicio :) \n\n" +
                        "Presiona OK cuando hayas regresado el perro a su hogar";
            }
            textView.setText(temp);
        }

        //inicio locattion
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        //locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//use GPS (mayor presicion)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//use Torrres y Wifi
        //event that is triggered whenever the update interval is met (1000)
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //save the location
                Location location = locationResult.getLastLocation();
                updateLatLon(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
            }
        };
        //fin location
        updateGPS();

    }

    public void ctrlBtnOk(View view){
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        DocumentReference docRef = db.collection("paseos").document(str_idPaseo);
        Map<String,Object> updates = new HashMap<>();
        if (str_status.equals("1")){
            updates.put("status", "2");
        }else if (str_status.equals("2")){
            updates.put("status", "3");
        }
        docRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Proceso exitoso", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            if (str_status.equals("1")){
                                //activar el gps
                                updateGPS();
                                //variable para detectar los cambios de ubicacion
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
                            }else if (str_status.equals("3")){
                                fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
                            }
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }

    public void updateLatLon(String lat, String lon){
        String str_idPaseo ="EUcfgCXaUhlGy0pyRm9Y";
        DocumentReference docRef = db.collection("paseos").document(str_idPaseo);
        Map<String,Object> updates = new HashMap<>();
        updates.put("latitudPaseador", lat);
        updates.put("longitudPaseador", lon);
        docRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //Toast.makeText(getApplicationContext(), "Compartiendo", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void updateGPS(){
        //get permissions from the user ti track GPS
        //get current location from the fused client
        //update the UI - i.e. set all properties in their associated text view items
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got permissions. Put the values of location
                    //Toast.makeText(getApplicationContext(), String.valueOf(location.getLatitude()), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            //permission not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }

}
