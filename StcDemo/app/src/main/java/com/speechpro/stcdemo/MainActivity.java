package com.speechpro.stcdemo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.speechpro.onepass.framework.Framework;
import com.speechpro.onepass.framework.permissions.RequestPermissions;

import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.speechpro.onepass.framework.util.Constants.ENROLL_REQUEST_CODE;
import static com.speechpro.onepass.framework.util.Constants.PERMISSIONS;

/**
 * @author volobuev
 * @since 07.10.16
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean isEnrollment;

    //Fragments
    private WelcomeFragment welcomeFragment;
    private LoginFragment loginFragment;
    private SettingsFragment settingsFragment;

    private Framework framework;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.a_main);

        setFullscreen();

        welcomeFragment = new WelcomeFragment();
        loginFragment = new LoginFragment();
        settingsFragment = new SettingsFragment();

        replaceFragment(welcomeFragment);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        replaceFragment(loginFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (welcomeFragment.isAdded()) {
//            welcomeFragment.runHandler();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissions.REQUEST_PERMISSION_ALL) {
            RequestPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public String getPref(String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(key, defaultValue);
    }

    public String getPref(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(key, null);
    }

    LoginFragment getLoginFragment() {
        return loginFragment;
    }

    WelcomeFragment getWelcomeFragment() {
        return welcomeFragment;
    }

    SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    void putPref(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    void replaceFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    void actionSnackbar(View view, int resId) {
        Snackbar.make(view, getString(resId), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENROLL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                String res = data.getStringExtra(ACTIVITY_RESULT);
//                if (res.equals(SUCCES)) {
//                    framework.startVerification(this, this.email);
//                }
            }
        }
    }

    private void setFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    void process() {
        String email = getPref(getString(R.string.email_pref));
        if (isEnrollment) {
            framework.startEnrollment(this, email);
        } else {
            framework.startVerification(this, email);
        }
    }

    /*
    * Method for hiding software keyboard.
    */
    void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void onTouchListener(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (view instanceof Button) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard();
                    return false;
                }
            });
        } else if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                onTouchListener(innerView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (getSettingsFragment().isVisible()) {
            replaceFragment(getLoginFragment());
        } else {
            moveTaskToBack(true);
        }
    }

}
