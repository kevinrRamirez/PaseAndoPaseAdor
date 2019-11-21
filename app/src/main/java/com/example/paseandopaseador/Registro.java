package com.example.paseandopaseador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }



    public void ctrlBtnRegistrar(View view)
    {
        Intent intent = new Intent(view.getContext(), PaseAndoNavi.class);
        startActivity(intent);
    }



    public void ctrlBotonIngresar(View view)
    {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }
}
