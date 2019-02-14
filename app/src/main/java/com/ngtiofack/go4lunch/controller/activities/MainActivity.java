package com.ngtiofack.go4lunch.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.ngtiofack.go4lunch.R;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;
    // Choose authentication providers
    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()); // SUPPORT GOOGLE


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout mButton_login = findViewById(R.id.relative_google);
        mButton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3 - Launch Sign-In Activity when user clicked on Login Button
                startSignInActivity();
            }
        });

        RelativeLayout mButton_login_fcb = findViewById(R.id.relative_fcb);
        mButton_login_fcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3 - Launch Sign-In Activity when user clicked on Login Button
                Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(myIntent);
            }
        });


    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }
    // --------------------
    // UTILS
    // --------------------
    // 3 - Method that handles response after SignIn Activity close

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                //showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.connection_succeed));
                Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                this.startActivity(myIntent);
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.connection_succeed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else { // ERRORS
                if (response == null) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_authentication_canceled, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    // showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_authentication_canceled));
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_no_internet, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    // showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_no_internet));
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_unknown_error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_unknown_error));
                }
            }
        }
    }

}

