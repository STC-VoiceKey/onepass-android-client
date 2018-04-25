package com.speechpro.onepass.framework.model.tasks;

import android.util.Pair;

import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;


public class VerificationResultTask extends ExceptionAsyncTask<Void, Void, Pair<Boolean, String>>{

    private final VerificationTransaction verificationtransaction;

    public VerificationResultTask(VerificationTransaction verificationtransaction) {
        super();
        this.verificationtransaction = verificationtransaction;
    }

    @Override
    protected Pair<Boolean, String> doInBackground(Void... params) {
            return verificationtransaction.verifyWithMessage(true);
    }
}