package com.ngtiofack.go4lunch.controller.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controller.fragments.ListViewFragment;
import com.ngtiofack.go4lunch.controller.fragments.MapsViewFragment;
import com.ngtiofack.go4lunch.controller.fragments.WorkmatesFragment;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import com.ngtiofack.go4lunch.utils.Go4LunchUserHelper;
import com.ngtiofack.go4lunch.utils.SaveCurrentLocation;

import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    CurrentLocation currentLocation = new CurrentLocation();
    //Drawer Layout

    // Creating identifier to identify REST REQUEST (Update username)
    private static final int UPDATE_USERNAME = 30;
    ImageView imageViewProfile;
    TextView textInputEditTextUsername;
    TextView textViewEmail;
    View headView;
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;

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
                    selectedFragment = new MapsViewFragment();
                    break;
                case R.id.navigation_dashboard:
                    SaveCurrentLocation currentLatLng = currentLocation.getSaveLatLng(MainActivity.this);
                    selectedFragment = ListViewFragment.newInstance(String.valueOf(currentLatLng.getLatitude()), String.valueOf(currentLatLng.getLongitude()));
                    break;
                case R.id.navigation_notifications:
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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapsViewFragment()).commit();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headView = navigationView.getHeaderView(0);
        imageViewProfile = headView.findViewById(R.id.user_item_image_drawer);
        textInputEditTextUsername = headView.findViewById(R.id.user_name_drawer);
        textViewEmail = headView.findViewById(R.id.user_email_drawer);

        drawerLayout = findViewById(R.id.drawer_main_id);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // LinearLayout background = headView.findViewById(R.id.linLayoutHeader);
        // BlurImage.with(getApplicationContext()).load(R.drawable.restaurants_img).intensity(20).Async(true).into());

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

                break;
            case R.id.menu_drawer_settings:

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

        //AuthUI.getInstance().signOut(this.headView.addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK)))
        ;

    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        updateUsernameInFirebase();
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
    private void updateUserIsMentor() {
        if (this.getCurrentUser() != null) {
            Go4LunchUserHelper.updateisConnected(this.getCurrentUser().getUid(), true).addOnFailureListener(this.onFailureListener());
        }
    }

    // http request that creates a user in firestore

    private void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            boolean isConnected = this.isCurrentUserLogged();
            String restaurantSel = "";

            Go4LunchUserHelper.createUser(uid, username, urlPicture, isConnected, restaurantSel).addOnFailureListener(this.onFailureListener());
        }
    }
}
