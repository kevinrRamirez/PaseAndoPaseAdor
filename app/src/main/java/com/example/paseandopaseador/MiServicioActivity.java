package com.example.paseandopaseador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
                        "Presiona OK cuando llegado hallas llegado";
            }else if (str_status.equals("2")){
                temp += "        Es hora de dar un buen servicio :) \n\n" +
                        "Presiona OK si ya regresaste al perro a su hogar";
            }
            textView.setText(temp);
        }
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
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Proceso exitoso", Toast.LENGTH_LONG).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
