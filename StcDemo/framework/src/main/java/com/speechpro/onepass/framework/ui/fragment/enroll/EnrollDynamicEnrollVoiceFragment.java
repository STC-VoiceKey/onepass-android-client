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
public class EnrollDynamicEnrollVoiceFragment extends EnrollVoiceFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        TextView num0 = (TextView) view.findViewById(R.id.num0);
        TextView num1 = (TextView) view.findViewById(R.id.num1);
        TextView num2 = (TextView) view.findViewById(R.id.num2);
        TextView num3 = (TextView) view.findViewById(R.id.num3);
        TextView num4 = (TextView) view.findViewById(R.id.num4);
        TextView num5 = (TextView) view.findViewById(R.id.num5);
        TextView num6 = (TextView) view.findViewById(R.id.num6);
        TextView num7 = (TextView) view.findViewById(R.id.num7);
        TextView num8 = (TextView) view.findViewById(R.id.num8);
        TextView num9 = (TextView) view.findViewById(R.id.num9);

        if (mEpisode != null) {
            mEpisodeText.setText(getString(mEpisode.getStage()));
            setNumbers(mEpisode.getEnrollPhrases(),
                    num0,
                    num1,
                    num2,
                    num3,
                    num4,
                    num5,
                    num6,
                    num7,
                    num8,
                    num9);
        }

        return view;
    }

    private void setNumbers(String mPassphrase, TextView... mTextViews) {
        char[] mPass = mPassphrase.toCharArray();
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setText("" + mPass[i]);
        }
    }

    @Override
    public void stop(byte[] result) {
        super.stop(result);
        try {
            mPresenter.processDynamicAudio(result);
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

