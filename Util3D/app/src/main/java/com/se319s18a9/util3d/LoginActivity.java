package com.se319s18a9.util3d;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.se319s18a9.util3d.Fragments.LoginFragment;

/**
 * LoginActivity:
 * --------------
 *  LoginActivity is the root UI component for facilitating login-related interactions. Once a
 *  user logs into the application, the MainActivity takes over (leaving the LoginActivity on
 *  the stack). In the event that an active instance of LoginActivity dies, its recreation
 *  always starts an instance of LoginFragment.
 */
public class LoginActivity
        extends AppCompatActivity
        implements LoginFragment.OnSuccessfulLoginListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Start LoginFragment instance
        Fragment startingFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_login_frameLayout_root, startingFragment);
        fragmentTransaction.commit();
    }

    /**
     * onSuccessfulLogin():
     * --------------------
     *  When a user successfully logs into their account (requiring Firebase authentication),
     *  this callback method is triggered in order to create a new instance of MainActivity
     *  to handle all interactions with the logged-in user.
     */
    public void onSuccessfulLogin() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}