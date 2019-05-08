package com.ngtiofack.go4lunch.controler.activities;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.libraries.places.compat.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
//import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
/*import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;*/
import com.jgabrielfreitas.core.BlurImageView;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.api.Go4LunchUserHelper;
import com.ngtiofack.go4lunch.controler.fragments.ListViewFragment;
import com.ngtiofack.go4lunch.controler.fragments.MapsViewFragment;
import com.ngtiofack.go4lunch.controler.fragments.WorkmatesFragment;
import com.ngtiofack.go4lunch.model.SaveCurrentLocation;
import com.ngtiofack.go4lunch.model.YourLunch;
import com.ngtiofack.go4lunch.utils.CurrentLocation;

import java.util.Objects;

import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANTISNOTSELECTED;
import static com.ngtiofack.go4lunch.utils.mainUtils.getRestaurantSelected;
import static com.ngtiofack.go4lunch.utils.mainUtils.getYourLunch;
import static com.ngtiofack.go4lunch.utils.mainUtils.saveUserId;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MapsViewFragment.OnDataPass {

    CurrentLocation currentLocation = new CurrentLocation();


    // Creating identifier to identify REST REQUEST (Update username)
    private static final int UPDATE_USERNAME = 30;

    String TAG = "PlaceSearch";
    ImageView imageViewProfile;
    TextView textInputEditTextUsername;
    TextView textViewEmail;
    View headView;
    RelativeLayout relProgress;
    YourLunch mYourLunch;
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;

    // Define a Place ID.
   /* String placeId = "INSERT_PLACE_ID_HERE";
    PlacesClient placesClient;
    // Specify the fields to return.
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
    // Construct a request object, passing the place ID and fields array.
    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
            .build();*/

    //Drawer Layout
    private DrawerLayout drawerLayout;
    // define an ActionBarDrawerToggle
    private ActionBarDrawerToggle mToggle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("I'm Hungry!");
                    selectedFragment = new MapsViewFragment();
                    break;
                case R.id.navigation_dashboard:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("I'm Hungry!");
                    SaveCurrentLocation currentLatLng = currentLocation.getSaveLatLng(MainActivity.this);
                    selectedFragment = ListViewFragment.newInstance(String.valueOf(currentLatLng.getLatitude()), String.valueOf(currentLatLng.getLongitude()));
                    break;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle("Available workmates");
                    selectedFragment = new WorkmatesFragment();
                    break;
            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureToolbar();


      /* // Initialize Places.
        Place.initialize(getApplicationContext(), "AIzaSyCXBKZ5tT07uT8XdXsUuAMsVkV-Uxs70E8");
        // Create a new Places client instance.
        placesClient = Places.createClient(this);*/

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapsViewFragment()).commit();

        relProgress = findViewById(R.id.relativProgressBar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headView = navigationView.getHeaderView(0);
        imageViewProfile = headView.findViewById(R.id.user_item_image_drawer);
        textInputEditTextUsername = headView.findViewById(R.id.user_name_drawer);
        textViewEmail = headView.findViewById(R.id.user_email_drawer);

        BlurImageView blurImageView = headView.findViewById(R.id.BlurImageViewHeaderDrawer);
        blurImageView.setBlur(10);

        drawerLayout = findViewById(R.id.drawer_main_id);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.updateUIWhenCreating();
        this.createUserInFirestore();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //2 - Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        switch (item.getItemId()) {

            case R.id.menu_activity_main_search:

                // Add a listener to handle the response.
              /*  placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    Log.i(TAG, "Place found: " + place.getName());
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });*/


                return true;
            default:
                if (mToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_drawer_your_lunch:

                mYourLunch = getYourLunch(this);

                if (mYourLunch.getName().equals(RESTAURANTISNOTSELECTED)) {

                    Toast.makeText(this, "No restaurant was selected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent myIntent = new Intent(this, DetailedRestaurantActivity.class);
                    myIntent.putExtra(getString(R.string.name_restaurant), mYourLunch.getName());
                    myIntent.putExtra(getString(R.string.vicinity), mYourLunch.getVicinity());
                    myIntent.putExtra(getString(R.string.photoHeight), mYourLunch.getPhotoHeight());
                    myIntent.putExtra(getString(R.string.photoWidth), mYourLunch.getPhotoWidth());
                    myIntent.putExtra(getString(R.string.photosReference), mYourLunch.getPhotoUrlRef());
                    myIntent.putExtra(getString(R.string.number_of_stars), mYourLunch.getRatingStars());
                    startActivity(myIntent);
                }
                break;
            case R.id.menu_drawer_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            case R.id.menu_drawer_logout:
                signOutUserFromGo4Lunch();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Update UI when activity is creating
    private void updateUIWhenCreating() {

        if (getCurrentUser() != null) {

            //Get picture URL from Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_email_not_found) : getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_user_name_not_found) : getCurrentUser().getDisplayName();

            //Update views with data
            textInputEditTextUsername.setText(username);
            textViewEmail.setText(email);
        }
    }

    private void signOutUserFromGo4Lunch() {
        showProgress("Signing out...");
        updateConnected(false);
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));

    }

    private void configureToolbar() {
        // Get the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(myIntent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {

            //4 - We also delete user from firestore storage
            Go4LunchUserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());


        }
    }

    // 3 - Update User Username
    private void updateUsernameInFirebase() {

        //this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals("user not found")) {
                Go4LunchUserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    // 2 - Update User Mentor (is or not)
    private void updateConnected(boolean connectionStatus) {
        if (this.getCurrentUser() != null) {
            Go4LunchUserHelper.updateIsConnected(this.getCurrentUser().getUid(), connectionStatus).addOnFailureListener(this.onFailureListener());
        }
    }
    // http request that creates a user in firestore

    private void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            boolean isConnected = this.isCurrentUserLogged();

            Go4LunchUserHelper.createUser(uid, username, urlPicture, isConnected,getYourLunch(getApplicationContext())).addOnFailureListener(this.onFailureListener());
        }
    }

    @Override
    public void onDataPass(boolean data) {
        if (data) {
            relProgress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUserId(this, this.getCurrentUser().getUid());
    }
}
