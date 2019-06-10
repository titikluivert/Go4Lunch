package com.ngtiofack.go4lunch.controler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jgabrielfreitas.core.BlurImageView;
import com.ngtiofack.go4lunch.BuildConfig;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.api.Go4LunchUserHelper;
import com.ngtiofack.go4lunch.controler.fragments.ListViewFragment;
import com.ngtiofack.go4lunch.controler.fragments.MapsViewFragment;
import com.ngtiofack.go4lunch.controler.fragments.WorkmatesFragment;
import com.ngtiofack.go4lunch.model.SaveCurrentLocation;
import com.ngtiofack.go4lunch.model.YourLunch;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ngtiofack.go4lunch.utils.RestaurantsUtils.getYourLunch;
import static com.ngtiofack.go4lunch.utils.UserIDUtils.saveUserId;
import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANT_IS_NOT_SELECTED;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MapsViewFragment.OnDataPass {

    private CurrentLocation currentLocation = new CurrentLocation();
    private SaveCurrentLocation currentLatLng;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.nav_view)
    NavigationView navigationView ;

    ImageView imageViewProfile;
    TextView textInputEditTextUsername;
    TextView textViewEmail;
    View headView;

    @BindView(R.id.relativProgressBar)
    RelativeLayout relProgress;

    //Drawer Layout
    @BindView(R.id.drawer_main_id)
    DrawerLayout drawerLayout;

    YourLunch mYourLunch;
    boolean bSingOut = false;
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    PlacesClient placesClient;

    // define an ActionBarDrawerToggle
    private ActionBarDrawerToggle mToggle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getApplicationContext().getString(R.string.im_hungry));
                selectedFragment = new MapsViewFragment();
                break;
            case R.id.navigation_dashboard:
                currentLatLng = currentLocation.getSaveLatLng(this);
                Objects.requireNonNull(getSupportActionBar()).setTitle(getApplicationContext().getString(R.string.im_hungry));
                selectedFragment = ListViewFragment.newInstance(String.valueOf(currentLatLng.getLatitude()), String.valueOf(currentLatLng.getLongitude()));
                break;
            case R.id.navigation_notifications:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getApplicationContext().getString(R.string.available_workmates));
                selectedFragment = new WorkmatesFragment();
                break;
        }
        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.configureToolbar();
        currentLatLng = currentLocation.getSaveLatLng(this);

        // Initialize Places.
        Places.initialize(getApplicationContext(), BuildConfig.ApiKeyGoogleID);
        //Create a new Places client instance.
        placesClient = Places.createClient(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapsViewFragment()).commit();


        navigationView.setNavigationItemSelectedListener(this);

        headView = navigationView.getHeaderView(0);
        imageViewProfile = headView.findViewById(R.id.user_item_image_drawer);
        textInputEditTextUsername = headView.findViewById(R.id.user_name_drawer);
        textViewEmail = headView.findViewById(R.id.user_email_drawer);

        BlurImageView blurImageView = headView.findViewById(R.id.BlurImageViewHeaderDrawer);
        blurImageView.setBlur(10);

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
        if (item.getItemId() == R.id.menu_activity_main_search) {
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            List<Place.Field> fields = Arrays.asList(Place.Field
                    .ID, Place.Field.NAME, Place.Field.TYPES);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(currentLatLng.getLatitude(), currentLatLng.getLongitude()),
                            new LatLng(currentLatLng.getLatitude() + 0.03, currentLatLng.getLongitude() + 0.03)))
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            return true;
        }
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_drawer_your_lunch:

                mYourLunch = getYourLunch(this);

                if (mYourLunch.getName().equals(RESTAURANT_IS_NOT_SELECTED)) {

                    Toast.makeText(this, this.getString(R.string.noRestaurantSelected), Toast.LENGTH_SHORT).show();
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
        bSingOut = true;
        showProgress(this.getString(R.string.sign_out));
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
        return aVoid -> {
            if (origin == SIGN_OUT_TASK) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            }
        };
    }

    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {

            //4 - We also delete user from firestore storage
            Go4LunchUserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());


        }
    }

    // 2 - Update : connected (is or not)
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
            Go4LunchUserHelper.createUser(uid, username, urlPicture, isConnected, getYourLunch(getApplicationContext())).addOnFailureListener(this.onFailureListener());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (Objects.requireNonNull(place.getTypes()).contains(Place.Type.RESTAURANT)) {
                    Log.i("AutoComplete", "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getTypes());
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("AutoComplete", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("AutoComplete", "is cancelled");
            }
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
        if (!bSingOut) {
            saveUserId(this, this.getCurrentUser().getUid());
        }
    }

}
