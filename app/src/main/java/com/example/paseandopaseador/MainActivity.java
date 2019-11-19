package com.example.paseandopaseador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ctrlBotonIngresar(View view)
    {
        Intent intent = new Intent(view.getContext(), PaseAndoNavi.class);
        startActivity(intent);;
    }

}
