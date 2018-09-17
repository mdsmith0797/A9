package com.se319s18a9.util3d;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.se319s18a9.util3d.Fragments.DashboardFragment;
import com.se319s18a9.util3d.Fragments.MapFragment;

/**
 * MainActivity:
 * -------------
 *  MainActivity is the root UI component for facilitating interactions with a logged-in user.
 *  When a user logs out, the MainActivity instance is popped from the stack, returning to the
 *  LoginActivity caller instance (recreating it as necessary).
 */
public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Start DashboardFragment instance
        Fragment startingFragment = new DashboardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_frameLayout_root, startingFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frameLayout_root);

        // Override the default back-press action (which is tied to MainActivity) to prevent
        // a user from unintentionally closing the MapFragment without saving.
        if (fragment instanceof MapFragment) {
            ((MapFragment) fragment).saveWithDialog(true);
        } else {
            super.onBackPressed();
        }
    }
}
