package com.example.paseandopaseador.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.paseandopaseador.R;

public class PerfilFragment extends Fragment  {

    private PerfilViewModel galleryViewModel;
    TextView txtNombre;
    TextView txtCorreo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        galleryViewModel =
                ViewModelProviders.of(this).get(PerfilViewModel.class);
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        Bundle dato = getActivity().getIntent().getExtras();
        txtCorreo = (TextView) root.findViewById(R.id.txtCorreoElectronico);
        txtCorreo.setText(dato.getString("datoCorreo"));
        txtNombre = (TextView) root.findViewById(R.id.txtNombreUsuario);
        txtNombre.setText(dato.getString("datoNombre"));

         */

        return root;
    }

}