package com.example.paseandopaseador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    EditText txtNombre;
    EditText txtCorreo;
    EditText txtPass;
    EditText txtPassConf;
    Codigos c = new Codigos();
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtCorreo = (EditText) findViewById(R.id.txtCorreoL);
        txtPass = (EditText) findViewById(R.id.txtPass);
        txtPassConf = (EditText) findViewById(R.id.txtPassConf);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }



    public void ctrlBtnRegistrar(View view)
    {
        final String nombre = txtNombre.getText().toString().trim();
        final String correo = txtCorreo.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();
        String passConf = txtPassConf.getText().toString().trim();

        if(c.hacerValidaciones) {
            if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {//todos los campos son requeridos
                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_LONG).show();
            } else if (!c.validacionNombreApellido(nombre)) {//no es Nombre Apellido
                Toast.makeText(this, "Ingresar Nombre y Apellido de forma correcta", Toast.LENGTH_LONG).show();
            } else if (!c.validacionCorreo(correo)) {
                Toast.makeText(this, "Correo electronico invalido", Toast.LENGTH_LONG).show();
            } else if (!(pass.length() >= 6)) {
                Toast.makeText(this, "Se requiere una contraseña mayor a 5 caracteres", Toast.LENGTH_LONG).show();
            } else if (!pass.equals(passConf)) {
                Toast.makeText(this, "Las contaseñas no coinciden", Toast.LENGTH_LONG).show();
            }
        }
        progressDialog.setMessage("Realizando registro...");
        progressDialog.show();
        //creacion del nuevo usuario
        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //enviar correo de verificacion
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        if (c.bln_CloudFirestore){
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            Map<String, Object> usuario = new HashMap<>();
                                            usuario.put("nombre", nombre);
                                            usuario.put("apellidoPat", "");
                                            usuario.put("apellidoMat", "");
                                            usuario.put("correo", correo);
                                            usuario.put("contrasena", pass);
                                            // Add a new document with a generated ID
                                            db.collection("usuarios")
                                                    .document(firebaseUser.getUid())
                                                    .set(usuario)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                                                                Intent i= new Intent(getApplication(), MainActivity.class);
                                                                startActivity(i);
                                                            }else{
                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }else if(c.bln_RealtimeDatabase){
                                            Usuario usuario = new Usuario(nombre,"","",correo,pass);
                                            FirebaseDatabase.getInstance().getReference("Usuarios").child(mAuth.getCurrentUser().getUid()).setValue(usuario)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                                                                Intent i= new Intent(getApplication(), MainActivity.class);
                                                                startActivity(i);
                                                            }else{
                                                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }else {
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){//Si el usuario ya existe
                                Toast.makeText(getApplicationContext(),"El usuario ya existe",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });



        /*
            servicioRegistro(c.direccionIP+"registro_paseador.php");
            //Intent intent = new Intent(view.getContext(), PaseAndoNavi.class);
            //startActivity(intent);
            finish()
         */
    }



    public void ctrlBotonIngresar(View view)
    {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void servicioRegistro(String url)
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



    boolean validacionNombreApellido(String s) {
        boolean bandera = true;
        StringTokenizer t = new StringTokenizer(s, " ");
        if (t.countTokens() != 2) {
            Toast.makeText(this, "Ingresar Nombre y Apellido", Toast.LENGTH_LONG).show();
            return false;
        }
        while (t.hasMoreTokens()) {
            if (!validacionNombre(t.nextToken())) {
                Toast.makeText(this, "Ingresar Nombre y Apellido de forma correcta", Toast.LENGTH_LONG).show();
                bandera = false;
            }
        }
        return bandera;
    }

    boolean validacionNombre(String s) {
        boolean bandera = true;
        String ABC = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
        String abc = "abcdefghijklmnñopqrstuvwxyz";
        if (!ABC.contains(Character.toString(s.charAt(0)))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!abc.contains(Character.toString(s.charAt(i)))) {
                return false;
            }
        }
        return bandera;
    }

    /*
    Método para hacer la validación de un coreo electronico
     */
    boolean validacionCorreo(String correo) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");/*expresión regular para el correo electronico*/
        Matcher mather = pattern.matcher(correo);
        if (mather.find()) { /*cuando cumple con la expresión regular regresa un valor bolleano true*/
            return true;
        } else { /*cuando no cumple con la expresión regular manda un Toast al usuario y regresa un valor bolleano false*/
            Toast.makeText(this, "Correo electronico invalido", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
