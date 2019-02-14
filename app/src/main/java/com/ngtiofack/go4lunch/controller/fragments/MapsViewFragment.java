package com.ngtiofack.go4lunch.controller.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.Utils;
import com.ngtiofack.go4lunch.utils.services.RetrofitMapsServices;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.ngtiofack.go4lunch.utils.Utils.PROXIMITY_RADIUS;
import static com.ngtiofack.go4lunch.utils.Utils.TYPE;

public class MapsViewFragment extends Fragment implements OnMapReadyCallback {


    public static final int MY_PERMISSION_REQUEST_CODE = 11;
    FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private View mView;
    private Marker mCurrLocationMarker;

    double latitude;
    double longitude;

    public MapsViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView mapView = mView.findViewById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mView.getContext());
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(120000); // two minute interval
        locationRequest.setFastestInterval(120000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }


    }

    LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {

            for (Location location : locationResult.getLocations()) {

                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                build_retrofit_and_get_response(location);

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(mView.getContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    //finish();
                }
                break;
        }
    }


    private void build_retrofit_and_get_response(final Location mLocation) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMapsServices service = retrofit.create(RetrofitMapsServices.class);

        Call<RestaurantsModel> call = service.getNearbyPlaces(TYPE, latitude + "," + longitude, PROXIMITY_RADIUS);

        call.enqueue(new Callback<RestaurantsModel>() {

            @Override
            public void onResponse(retrofit.Response<RestaurantsModel> response, Retrofit retrofit) {
                try {
                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {

                        double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        // Position of Marker on Map
                        markerOptions.position(latLng);
                        // Adding Title to the Marker
                        markerOptions.title(placeName + " : " + vicinity);
                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerOptions);
                        // Adding colour to the marker
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        // move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                        if (i == response.body().getResults().size()-1)
                        {
                            //Place current location marker
                            LatLng yourLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                            //MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(yourLocation);
                            markerOptions.title("Current Position");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            mCurrLocationMarker = mMap.addMarker(markerOptions);
                            //move map camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, Utils.zoomLevel));
                        }
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }
}
