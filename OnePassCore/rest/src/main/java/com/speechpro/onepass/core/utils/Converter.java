package com.speechpro.onepass.core.utils;

import com.speechpro.onepass.core.sessions.Model;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.core.exception.NotFoundException;
import com.speechpro.onepass.core.rest.data.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for convert dataset into requests.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class Converter {

    private Converter() {
    }

    /**
     * Converts person data into created general session.
     *
     * @param restAPI api for rest requests
     * @param person  person data
     * @return person session
     */
    public static PersonSession convertPerson(ITransport restAPI, String sessionId, Person person) {
        Set<Model> models = new HashSet<>();
        Set<DataModel> m = person.models;
        for (DataModel dataModel : m) {
            Model model = new Model(dataModel.id, dataModel.type, dataModel.samplesCount);
            models.add(model);
        }

        return new PersonSession(restAPI, sessionId, person.id, models, person.isFullEnroll);
    }

    /**
     * Converts person id to create person request.
     *
     * @param personId person id
     * @return request for creating person
     */
    public static CreatePersonRequest convertPerson(String personId) {
        return new CreatePersonRequest(personId);
    }

    /**
     * Converts needs information to the VerificationTransaction.
     *
     * @param restAPI server RESTful API
     * @param model   response from server with information about transaction
     * @return verification transaction
     */
    public static VerificationTransaction convertVerification(ITransport restAPI, VerificationSessionResponse model, String sessionId) {
        return new VerificationTransaction(restAPI, sessionId, model.getTransactionId(), model.getPassword());
    }


    /**
     * Converts verification result to boolean. If verification is successful then true, otherwise false.
     *
     * @param model response from server with verification result
     * @return verification result
     */
    public static boolean convertVerificationResult(VerificationResponse model) throws NotFoundException {
        return model.compileResult();
    }
}
