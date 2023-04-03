package com.example.hopreviews.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hopreviews.R;
import com.example.hopreviews.databinding.FragmentMapBinding;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment {

private FragmentMapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

    binding = FragmentMapBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    //Initialize map Fragment
    SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

    //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        //When clicked on map
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        //Set title to marker
                        markerOptions.title(latLng.latitude + ":" + latLng.longitude);
                        //Remove all markers
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                        //Add marker on map
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });

        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}