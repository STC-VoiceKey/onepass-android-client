package com.speechpro.onepass.framework.view.fragment;

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;

import static com.speechpro.onepass.framework.util.Constants.CANCEL_TIMEOUT;

/**
 * @author volobuev
 * @since 04.04.16
 */
public abstract class BaseFragment extends Fragment {

    private final Handler cancelHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            getActivity().finish();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        cancelHandler.sendEmptyMessageDelayed(0, CANCEL_TIMEOUT);
    }

    @Override
    public void onPause() {
        cancelHandler.removeMessages(0);
        super.onPause();
    }

    protected void updateCancelHandler(){
        if(isEnrollment()) {
            cancelHandler.removeMessages(0);
            cancelHandler.sendEmptyMessageDelayed(0, CANCEL_TIMEOUT);
        }
    }

    protected abstract boolean isEnrollment();

}
