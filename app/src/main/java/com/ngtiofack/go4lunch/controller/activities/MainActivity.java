package com.ngtiofack.go4lunch.controller.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controller.fragments.ListViewFragment;
import com.ngtiofack.go4lunch.controller.fragments.MapsViewFragment;
import com.ngtiofack.go4lunch.controller.fragments.WorkmatesFragment;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import com.ngtiofack.go4lunch.utils.SaveCurrentLocation;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CurrentLocation currentLocation = new CurrentLocation();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapsViewFragment()).commit();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_main_id);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
