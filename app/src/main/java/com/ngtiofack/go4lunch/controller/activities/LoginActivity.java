package com.ngtiofack.go4lunch.controller.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.jgabrielfreitas.core.BlurImageView;
import com.ngtiofack.go4lunch.R;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends BaseActivity {

    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;
    // User info

    //FOR DESIGN

    // Choose authentication providers
    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()// SUPPORT GOOGLE
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                showProgress("Signing in...");

            }
        });
        BlurImageView blurImageView = findViewById(R.id.BlurImageView);
        blurImageView.setBlur(5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources() .getColor(android.R.color.transparent));
        }

    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
        showProgress("Signing in...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

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
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

}


