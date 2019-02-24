package com.ngtiofack.go4lunch.controller.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.utils.Go4LunchUserHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    // User info

    //FOR DESIGN


    // Choose authentication providers
    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()// SUPPORT GOOGLE
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog);

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
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

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
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
                onLoginSuccess();
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.connection_succeed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else { // ERRORS
                if (response == null) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_authentication_canceled, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    // showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_no_internet, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    // showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_unknown_error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_unknown_error));
                }
            }
        }
    }


    private void onLoginSuccess() {
        createUserInFirestore();
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }


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

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };

    }
}

