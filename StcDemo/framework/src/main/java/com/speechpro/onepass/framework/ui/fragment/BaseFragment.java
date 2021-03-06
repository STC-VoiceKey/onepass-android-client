package com.speechpro.onepass.framework.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;

import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;

import static com.speechpro.onepass.framework.util.Constants.CANCEL_TIMEOUT;

/**
 * @author volobuev
 * @since 04.04.16
 */
public abstract class BaseFragment extends Fragment {

    @SuppressLint("HandlerLeak")
    private final Handler cancelHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (getActivity() != null)
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

    public void removeCancelHandler() {
        cancelHandler.removeMessages(0);
    }

    void updateCancelHandler() {
        if (isEnrollment()) {
            cancelHandler.removeMessages(0);
            cancelHandler.sendEmptyMessageDelayed(0, CANCEL_TIMEOUT);
        }
    }
    
    private boolean isEnrollment() {
        return getActivity() instanceof EnrollmentActivity;
    }

}
