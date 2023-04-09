package com.example.hopreviews.ui.map;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hopreviews.LocationActivity;
import com.example.hopreviews.R;
import com.example.hopreviews.databinding.FragmentMapBinding;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/* The majority of this class is copied from Open Source Maps SDK for Android
    tutorial on Google Developers.
    Reference: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String TAG = MapFragment.class.getSimpleName();
    private FragmentMapBinding binding;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng defaultLocation = new LatLng(39.3299, -76.6205);
    private PlacesClient placesClient;
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater);
        View root = binding.getRoot();

        //Initialize map Fragment
        SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());

        Objects.requireNonNull(supportMapFragment).getMapAsync(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        populateLocations();

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null && map != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        if (map != null) {
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    /**
     * Handles the result of the request for location permissions.
     */
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }

        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<FindCurrentPlaceResponse> placeResult =
                    placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener (task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();

                    // Set the count, handling cases where less than 5 entries are returned.
                    int count;
                    if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                        count = likelyPlaces.getPlaceLikelihoods().size();
                    } else {
                        count = M_MAX_ENTRIES;
                    }

                    int i = 0;
                    likelyPlaceNames = new String[count];
                    likelyPlaceAddresses = new String[count];
                    likelyPlaceAttributions = new List[count];
                    likelyPlaceLatLngs = new LatLng[count];

                    for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                        likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                        likelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                .getAttributions();
                        likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > (count - 1)) {
                            break;
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    MapFragment.this.openPlacesDialog();
                }
                else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            map.addMarker(new MarkerOptions()
                    .title("Baltimore, MD")
                    .position(defaultLocation)
                    .snippet("Hopkins"));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }
    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = likelyPlaceLatLngs[which];
            String markerSnippet = likelyPlaceAddresses[which];
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            map.addMarker(new MarkerOptions()
                    .title(likelyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this.requireContext())
                .setTitle("Pick a Place")
                .setItems(likelyPlaceNames, listener)
                .show();
    }

    private void populateLocations() {
        HashMap<String, LatLng> locations = new HashMap<>(30);
        locations.put("Hopkins Cafe (FFC)", new LatLng(39.331610268771605, -76.6195978883285));
        locations.put("Nolan's Cafe", new LatLng(39.3283984802178, -76.61654356473828));
        locations.put("Levering Kitchens", new LatLng(39.32789682363558, -76.62178585228017));
        locations.put("Charles Street Market", new LatLng(39.32898418675939, -76.61721996854631));
        locations.put("Brody Reading Room", new LatLng(39.32835813777735, -76.61932382677452));
        locations.put("Brody Atrium", new LatLng(39.328462865692046, -76.61945594021617));
        locations.put("Gilman Atrium", new LatLng(39.32897433156248, -76.62180893076324));

        locations.put("R House", new LatLng(39.32174358987388, -76.62230796246828));
        locations.put("Papermoon Diner", new LatLng(39.32248195852942, -76.62127504462953));
        locations.put("Power Plant Live", new LatLng(39.28913653484303, -76.60738906961856));
        locations.put("National Aquarium", new LatLng(39.285425995252425, -76.60840707009508));
        locations.put("Clavel", new LatLng(39.31523919473816, -76.62044637298729));
        locations.put("Tagliata", new LatLng(39.28466331516136, -76.59978856134195));
        locations.put("The Charmery (Hampden)", new LatLng(39.331188763706486, -76.6295553131882));
        locations.put("Mona's Super Noodle", new LatLng(39.3316066779916, -76.63110604362373));
        locations.put("Barcocina", new LatLng(39.28135327202415, -76.59326884230492));
        locations.put("Miss Shirley's Cafe, Roland Park", new LatLng(39.34464688170542, -76.63145630182196));
        locations.put("Ekiben (Hampden)", new LatLng(39.33085205582459, -76.63176863065772));
        locations.put("Blue Moon Cafe", new LatLng(39.28391200923804, -76.59404125084309));
        locations.put("Topgolf", new LatLng(39.27467857352671, -76.62450354363561));
        locations.put("Maryland Zoo", new LatLng(39.32296407007038, -76.64974522881016));
        locations.put("Federal Hill Park", new LatLng(39.27974376855076, -76.6084331504898));
        locations.put("Monarque", new LatLng(39.284961171885875, -76.59996477298806));
        locations.put("Wayward Smokehouse", new LatLng(39.27944258171549, -76.61439678412442));
        locations.put("Noble's Bar and Grill", new LatLng(39.27736983554085, -76.61453031346963));
        locations.put("The Admiral's Cup", new LatLng(39.2817796618453, -76.59335085949435));
        locations.put("Kong Pocha", new LatLng(39.31252219154175, -76.61723203065816));
        locations.put("Jong Kak", new LatLng(39.312455784156505, -76.61748952270764));
        locations.put("The Cheesecake Factory", new LatLng(39.28647488810012, -76.61014975764644));
        locations.put("The Bygone", new LatLng(39.28288859512139, -76.60211693250669));
        for (String location: locations.keySet()) {
            MarkerOptions marker = new MarkerOptions()
                    .title(location)
                    .position(locations.get(location));
            this.map.addMarker(marker);
            this.map.setOnMarkerClickListener(marker1 -> {
                onMarkerClick(marker1);
                return true;
            });
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent intent = new Intent(getActivity(), LocationActivity.class);
        intent.putExtra("name", marker.getTitle());
        intent.putExtra("username", getActivity().getIntent().getStringExtra("username"));
        intent.putExtra("location", marker.getPosition());
        startActivity(intent);
        return true;
    }
}