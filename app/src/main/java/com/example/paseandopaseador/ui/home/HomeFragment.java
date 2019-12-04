package com.example.paseandopaseador.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.paseandopaseador.Codigos;
import com.example.paseandopaseador.R;
import com.example.paseandopaseador.SolicitudPaseoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    private MapView mapView;
    private double lat;
    private double lon;
    private boolean conectado = false;
    private Switch swConecta;
    private TextView lblConecta;
    private Location location;
    private LocationManager locationManager;
    JSONObject jso;
    Button btnActualizar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        lblConecta = (TextView) v.findViewById(R.id.lblConectate);
        lblConecta.setVisibility(View.INVISIBLE);
        swConecta = (Switch) v.findViewById(R.id.swConetate);
        mapView = (MapView) v.findViewById(R.id.mapHome);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //this is important

        return v;

       // return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.addMarker(new MarkerOptions().position(/*some location*/));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(/*some location*/, 10));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lon = location.getLongitude();
        lat = location.getLatitude();
        // Add a marker in Sydney and move the camera
        final LatLng ubicacion = new LatLng(lat, lon);
        //mMap.addMarker(new MarkerOptions().position(ubicacion).title("Mi Ubicación"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15));


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE);

        swConecta.setChecked(sharedPreferences.getBoolean("value",true));
        swConecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swConecta.isChecked())
                {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    swConecta.setChecked(true);
                    lblConecta.setVisibility(View.INVISIBLE);
                    conectado = true;
                }else
                {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    swConecta.setChecked(false);
                    lblConecta.setVisibility(View.VISIBLE);
                }
            }
        });



    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}