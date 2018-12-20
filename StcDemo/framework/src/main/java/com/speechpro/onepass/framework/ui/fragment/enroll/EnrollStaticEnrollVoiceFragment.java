package com.speechpro.onepass.framework.ui.fragment.enroll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;

/**
 * @author Alexander Grigal
 */
public class EnrollStaticEnrollVoiceFragment extends EnrollVoiceFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        TextView titleText = (TextView) view.findViewById(R.id.title_text);
        titleText.setText(R.string.pronounce_password_phrase);

        if (mEpisode != null) {
            mEpisodeText.setText(getString(mEpisode.getStage()));
        }

        return view;
    }

    @Override
    public void stop(final byte[] result) {
        super.stop(result);
        try {
            mPresenter.processStaticAudio(result);
            mActivity.nextEpisode();
        } catch (CoreException ex) {
            mPresenter.releaseRecorder();
            final String descriptionError = ((RestException) ex).reason;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorDialogFragment(descriptionError);
                    mProgressBar.setVisibility(View.GONE);
                    mMain.setVisibility(View.VISIBLE);
                    mRecButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
