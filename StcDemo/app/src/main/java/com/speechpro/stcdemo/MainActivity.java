package com.speechpro.stcdemo;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.speechpro.onepass.framework.Framework;

import static com.speechpro.onepass.framework.util.Constants.*;

public class MainActivity extends AppCompatActivity {

    Framework framework;

    boolean isEnrollment;
    String  email;

    private MainFragment      mainFragment;
    private SettingsFragment  settingsFragment;
    private AboutFragment     aboutFragment;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainFragment = new MainFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        replaceFragment(mainFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            replaceFragment(settingsFragment);
        } else if (id == R.id.action_about) {
            replaceFragment(aboutFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ((requestCode == PERMISSIONS_RECORD_AUDIO ||
             requestCode == PERMISSIONS_CAMERA ||
             requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE ||
             requestCode == PERMISSIONS_READ_EXTERNAL_STORAGE) &&
            grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    SharedPreferences getSharedPref() {
        return sharedPref;
    }

    MainFragment getMainFragment() {
        return mainFragment;
    }

    SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    void replaceFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    protected void actionSnackbar(View view, int resId) {
        Snackbar.make(view, getString(resId), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
