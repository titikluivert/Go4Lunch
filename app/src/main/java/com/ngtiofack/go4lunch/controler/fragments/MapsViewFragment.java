package com.ngtiofack.go4lunch.controler.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.api.RestaurantHelper;
import com.ngtiofack.go4lunch.controler.activities.DetailedRestaurantActivity;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import com.ngtiofack.go4lunch.utils.RestaurantsServiceStreams;
import com.ngtiofack.go4lunch.utils.RestaurantsUtils;
import com.ngtiofack.go4lunch.utils.mainUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

import static com.ngtiofack.go4lunch.utils.mainUtils.PROXIMITY_RADIUS;
import static com.ngtiofack.go4lunch.utils.mainUtils.SET_INTERVAL;
import static com.ngtiofack.go4lunch.utils.mainUtils.TYPE;
import static com.ngtiofack.go4lunch.utils.mainUtils.bitmapDescriptorFromVector;

public class MapsViewFragment extends Fragment implements OnMapReadyCallback {

    private static final int MY_PERMISSION_REQUEST_CODE = 11;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude;
    private double longitude;
    private CurrentLocation currentLocation = new CurrentLocation();
    private GoogleMap mMap;
    private View mView;
    private Marker m;
    private OnDataPass dataPasser;
    private MarkerOptions markerOptions = new MarkerOptions();
    private List<String> reselected = new ArrayList<>();

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (m != null) {
                    m.remove();
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                checkIfARestaurantIsAlreadySelected();
                buildRetrofitAndGetResponse(location);
            }
        }
    };

    public MapsViewFragment() {
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                Objects.requireNonNull(getContext()), R.raw.style_google));
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(SET_INTERVAL); // 5 secondes interval
        locationRequest.setFastestInterval(SET_INTERVAL);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(mView.getContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                //mView.finish();
            }
        }
    }

    private void buildRetrofitAndGetResponse(final Location mLocation) {
        DisposableObserver<RestaurantsModel> disposable = RestaurantsServiceStreams.streamFetchRestaurantsItems(TYPE, latitude + "," + longitude, PROXIMITY_RADIUS).subscribeWith(new DisposableObserver<RestaurantsModel>() {

            @Override
            public void onNext(final RestaurantsModel results) {
                try {
                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < results.getResults().size(); i++) {

                        preparePlaceAndMarker(results,mLocation,i);
                        m = mMap.addMarker(markerOptions);
                        m.setTag(i);

                        /* For simplicity an anonymous marker click listener is specified.
                         Return true if the click event is consumed (and therefore no
                         info window is displayed) or false to produce default behavior.*/

                        placeRestaurantToMap(results);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                //  Log.e("", Objects.requireNonNull(getContext()).getString(R.string.there_is_an_error)+ e);
            }

            @Override
            public void onComplete() {
                //  Log.e("", Objects.requireNonNull(getContext()).getString(R.string.on_complete_is_running));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        currentLocation.saveLatLng(mView.getContext(), (float) latitude, (float) longitude);

    }

    private void placeRestaurantToMap(RestaurantsModel results){
        mMap.setOnMarkerClickListener(marker -> {
            String[] name_and_address, refPhotoHeightWidth;
            Integer clickCount = (Integer) marker.getTag();
            String photoUrl;
            boolean retValue = true;
            // Check if a click count was set, then display the click count.
            if (clickCount != null) {
                clickCount = clickCount + 1;
                marker.setTag(clickCount);
                name_and_address = marker.getTitle().split(":");
                refPhotoHeightWidth = marker.getSnippet().split(":");

                if (clickCount < results.getResults().size()) {
                    Intent myIntent = new Intent(getActivity(), DetailedRestaurantActivity.class);
                    myIntent.putExtra(getString(R.string.vicinity), name_and_address[1].trim());
                    myIntent.putExtra(getString(R.string.name_restaurant), name_and_address[0].trim());

                    if (refPhotoHeightWidth[0].isEmpty()) {
                        photoUrl = "";
                    } else {
                        photoUrl = refPhotoHeightWidth[0];
                        myIntent.putExtra(getString(R.string.photoHeight), refPhotoHeightWidth[1]);
                        myIntent.putExtra(getString(R.string.photoWidth), refPhotoHeightWidth[2]);
                    }
                    myIntent.putExtra(getString(R.string.photosReference), photoUrl);
                    myIntent.putExtra(getString(R.string.number_of_stars), RestaurantsUtils.getNumOfStars(Double.parseDouble(refPhotoHeightWidth[3])));

                    startActivity(myIntent);

                } else {
                    retValue =  false;

                }
            }
            return retValue;
        });
    }

    private void preparePlaceAndMarker (RestaurantsModel results, Location mLocation, int i){

        double lat = results.getResults().get(i).getGeometry().getLocation().getLat();
        double lng = results.getResults().get(i).getGeometry().getLocation().getLng();

        String placeName = results.getResults().get(i).getName();
        String vicinity = results.getResults().get(i).getVicinity();
        double rating = results.getResults().get(i).getRating() == null ? 0 : results.getResults().get(i).getRating();

        String photoRef;
        int photoHeight, photoWidth;
        if (results.getResults().get(i).getPhotos() != null) {
            photoRef = results.getResults().get(i).getPhotos().get(0).getPhotoReference();
            photoHeight = results.getResults().get(i).getPhotos().get(0).getHeight();
            photoWidth = results.getResults().get(i).getPhotos().get(0).getWidth();

        } else {
            photoRef = "";
            photoHeight = 0;
            photoWidth = 0;
        }

        LatLng latLng = new LatLng(lat, lng);
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title to the Marker
        markerOptions.title(placeName + " : " + vicinity);
        markerOptions.snippet(photoRef + ":" + photoHeight + ":" + photoWidth + ":" + rating);

        if (reselected.contains(placeName)) {
            markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_restaurant_map_marker_sel_));
        } else {
            markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_restaurant_map_marker_));
        }
        // stop progressBar
        passData(true);

        if (i == results.getResults().size() - 1) {
            //Place current location marker
            latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(Objects.requireNonNull(getContext()).getString(R.string.current_location));
            // markerOptions.snippet()
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        }
        //move map camera

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mainUtils.zoomLevel));
    }

    public interface OnDataPass {
        void onDataPass(boolean data);
    }

    private void passData(boolean data) {
        dataPasser.onDataPass(data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    private void checkIfARestaurantIsAlreadySelected() {


        RestaurantHelper.getRestaurantCollection().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {

                    Iterable<DataSnapshot> keys2 = key.getChildren();
                    for (DataSnapshot key0 : keys2) {
                        reselected.add(key.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }


        });

    }
}
