package com.speechpro.onepass.core.utils;

import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.core.exception.NotFoundException;
import com.speechpro.onepass.core.rest.data.*;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.VerificationSession;

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
    public static PersonSession convertPerson(ITransport restAPI, Person person) {
        return new PersonSession(restAPI, person.id, person.isFullEnroll);
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
     * Converts needs information to the VerificationSession.
     *
     * @param restAPI server RESTful API
     * @param model   response from server with information about verification
     * @return verification session
     */
    public static VerificationSession convertVerification(ITransport restAPI, VerificationSessionResponse model) {
        return new VerificationSession(restAPI, model.getVerificationId(), model.getPassword());
    }


    /**
     * Converts verification result to boolean. If verification is successful then true, otherwise false.
     *
     * @param model response from server with verification result
     * @return verification result
     */
    public static boolean convertVerificationResult(VerificationResult model) throws NotFoundException {
        return model.compileResult();
    }
}
