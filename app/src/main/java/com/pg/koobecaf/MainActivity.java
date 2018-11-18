package com.pg.koobecaf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Bundle args;
    private FragmentTransaction transaction;
    ContactsFragment contactFragment;
    AlbumFragment albumFragment;
    PostFragment postFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_comments:
                    if (isOnline()) {
                        args = new Bundle();
                        postFragment.setArguments(args);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmento, postFragment);
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_photo_libraries:
                    if (isOnline()) {
                        args = new Bundle();
                        albumFragment.setArguments(args);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmento, albumFragment);
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_contacts:
                    if (isOnline()) {
                        args = new Bundle();
                        contactFragment.setArguments(args);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmento, contactFragment);
                        transaction.commit();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactFragment = new ContactsFragment();
        albumFragment = new AlbumFragment();
        postFragment = new PostFragment();
        if (findViewById(R.id.fragmento) != null && isOnline()) {
            if (savedInstanceState != null) {
                return;
            }
            postFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmento, postFragment).commit();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(getBaseContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
