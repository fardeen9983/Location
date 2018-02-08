package com.example.android.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LUCIFER on 12-01-2018.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12;
    private final String TAG = getClass().getSimpleName();
    private Location currentLocation;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionGranted;
    private GoogleMap mGoogleMap;
    private LatLng current;
    private ImageView gps;
    private AutoCompleteTextView searchBar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final float DEFAULT_ZOOM = 15;
    private GoogleApiClient googleApiClient;
    private PlaceInfo placeInfo;
    private final LatLngBounds LATLNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        searchBar = findViewById(R.id.searchBar);
        gps = findViewById(R.id.ic_gps);
        locationPermissionGranted = false;
        permissionCheck();
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

    }

    private void init() {
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, LATLNG_BOUNDS, null);
        searchBar.setOnItemClickListener(adapterClickListener);
        searchBar.setAdapter(placeAutocompleteAdapter);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard();
                    geolocation();

                }
                return false;
            }
        });
    }

    private void geolocation() {
        String searchLocation = searchBar.getText().toString();
        //searchBar.setText("fuck you Debby");
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {

        }
        if (list.size() > 0) {
            Address address = list.get(0);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }
    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        current = latLng;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
        if (!title.equals("Current Location"))
            mGoogleMap.addMarker(new MarkerOptions().position(current).title(title));
    }

    public void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            currentLocation = task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "Current Location");
                        } else
                            Toast.makeText(getApplicationContext(), "No option", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Security Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void permissionCheck() {

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initMap();
            } else
                ActivityCompat.requestPermissions(this, new String[]{COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    initMap();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap == null)
            mGoogleMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();
            init();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.setMyLocationEnabled();
            //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        }
    }

    public void hideKeyboard() {
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
    }

    private AdapterView.OnItemClickListener adapterClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
            placeBufferPendingResult.setResultCallback(placeBufferResultCallback);
        }
    };

    private ResultCallback<PlaceBuffer> placeBufferResultCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "Didn't get the places" + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                placeInfo = new PlaceInfo();
                placeInfo.setLatLng(place.getLatLng());
                placeInfo.setViewPort(place.getViewport());
                placeInfo.setId(place.getId());
                placeInfo.setName(place.getName().toString());
                placeInfo.setPhoneNo(place.getPhoneNumber().toString());
                placeInfo.setPriceLevel(place.getPriceLevel());
                placeInfo.setRating(place.getRating());
                placeInfo.setWebsite(place.getWebsiteUri());
                placeInfo.setAddress(place.getAddress().toString());
                Log.v(TAG,"DOne Successfully");

            } catch (NullPointerException e) {
                Log.d(TAG, "Null pointer");
            }
            moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName().toString());
            places.release();
        }
    };
}
