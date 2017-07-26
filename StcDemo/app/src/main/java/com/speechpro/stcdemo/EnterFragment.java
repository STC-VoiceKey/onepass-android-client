package com.speechpro.stcdemo;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author volobuev
 * @since 24.06.16
 */
public class EnterFragment extends Fragment {

    private final static String TAG = EnterFragment.class.getName();

    protected String url;

    protected View           view;
    protected EditText       emailEdit;
    protected TextView       warning;
    protected Button         loginButton;
    protected Button         enrollButton;
    protected View           layout_warning;
    protected ImageView      logo;
    protected ImageView      settings;

    protected MainActivity   mMainActivity;

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity = (MainActivity) getActivity();
    }

    protected void toast(int msgRes) {
        mMainActivity.actionSnackbar(view, msgRes);
    }

    protected boolean isValidEmail(String email) {
        layout_warning.setVisibility(View.INVISIBLE);
        if (email != null && !email.isEmpty() && isEmailValid(email)) {
            return true;
        }
        Log.e(TAG, "Invalid email address");

        return false;
    }

    protected void showWarning(boolean flag) {
        if (flag) {
            warning.setText(getString(R.string.text_invalid_email));
            layout_warning.setVisibility(View.VISIBLE);
        } else {
            layout_warning.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
