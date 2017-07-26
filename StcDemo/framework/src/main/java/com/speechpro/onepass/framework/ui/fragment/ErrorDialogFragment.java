package com.speechpro.onepass.framework.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.speechpro.onepass.framework.R;

/**
 * Created by grigal on 19.05.2017.
 */

public class ErrorDialogFragment extends DialogFragment {

    public static final String TITLE_ERROR = "title_error";
    public static final String DESCRIPTION_ERROR = "description_error";

    private static final String TAG = ErrorDialogFragment.class.getSimpleName();

    private Fragment fragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        fragment = getTargetFragment();
        fragment.onPause();

        final View view = inflater.inflate(R.layout.f_error, container, false);
        TextView titleText = (TextView) view.findViewById(R.id.title_text);
        TextView errorDescriptionText = (TextView) view.findViewById(R.id.error_text);
        String title = getArguments().getString(TITLE_ERROR);
        if (title != null) {
            titleText.setText(title);
        }
        String error = getArguments().getString(DESCRIPTION_ERROR);
        if (error != null) {
            errorDescriptionText.setText(error);
        }

        Button retryButton = (Button) view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onResume();
                dismiss();
            }
        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                getActivity().finish();
            }
        };

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

}
