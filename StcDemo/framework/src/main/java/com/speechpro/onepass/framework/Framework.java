package com.speechpro.onepass.framework;

import android.app.Activity;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.Model;
import com.speechpro.onepass.framework.util.Constants;
import com.speechpro.onepass.framework.view.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.view.activity.VerificationActivity;

/**
 * @author volobuev
 * @since 21.04.16
 */
public final class Framework {

    private IModel model;
    private static String url;

    public Framework(String url) {
        this.url = url;
        this.model = new Model(url);
    }

    public void startEnrollment(Activity activity, String userId){
        activity.startActivity(EnrollmentActivity.getCallingIntent(activity, userId, url));
    }

    public static void startVerification(Activity activity, String userId){
        activity.startActivity(VerificationActivity.getCallingIntent(activity, userId, url));
    }

    public boolean isEnrolled(String userId) throws Exception {
        if (userId != null) {
            PersonSession session;
            try {
                session = model.readPerson(userId);
            } catch (CoreException e) {
                throw new Exception(e);
            }
            if (session != null){
                if(!session.isFullEnroll()){
                    model.deletePerson(userId);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public  boolean delete(String userId) throws Exception {
        PersonSession session;
        try {
            session = model.readPerson(userId);
        } catch (CoreException e) {
            throw new Exception(e);
        }
        if (session != null){
            model.deletePerson(userId);
            return true;
        }
        return false;
    }
}
