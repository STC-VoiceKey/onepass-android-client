package com.speechpro.onepass.core.rest;

import com.speechpro.onepass.core.rest.data.*;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author volobuev
 * @since 19.04.16
 */
public interface OnePassService {

    @POST("session")
    Call<SessionIdResponse> startSession(@Body StartSessionRequest session);

    @DELETE("session")
    Call<Void> closeSession(@Header("X-Session-Id") String sessionId);

    @GET("person/{id}")
    Call<Person> readPerson(@Header("X-Session-Id") String sessionId,
                            @Path("id") String id);

    @DELETE("person/{id}")
    Call<Void> deletePerson(@Header("X-Session-Id") String sessionId,
                            @Path("id") String id);

    @GET("registration/person/{id}")
    Call<RegistationSessionResponse> startRegistrationSession(@Header("X-Session-Id") String sessionId,
                                                              @Path("id") String id);

    @POST("registration/face/file")
    Call<Void> addFaceFile(@Header("X-Session-Id") String sessionId,
                           @Header("X-Transaction-Id") String transactionId,
                           @Body Data data);

    @POST("registration/voice/dynamic/file")
    Call<Void> addVoiceDynamicFile(@Header("X-Session-Id") String sessionId,
                                   @Header("X-Transaction-Id") String transactionId,
                                   @Body VoiceFile voiceFile);

    @POST("registration/voice/dynamic/sample")
    Call<Void> addVoiceDynamicSample(@Header("X-Session-Id") String sessionId,
                                     @Header("X-Transaction-Id") String transactionId,
                                     @Body VoiceSample sample);

    @GET("verification/person/{id}")
    Call<VerificationSessionResponse> startVerificationTransaction(@Header("X-Session-Id") String sessionId,
                                                                   @Path("id") String id);

    @POST("verification/face/file")
    Call<Void> addVerivicationFaceFile(@Header("X-Session-Id") String sessionId,
                                       @Header("X-Transaction-Id") String transactionId,
                                       @Body Data data);

    @POST("verification/voice/dynamic/sample")
    Call<Void> addDynamicVerificationVoiceSample(@Header("X-Session-Id") String sessionId,
                                                 @Header("X-Transaction-Id") String transactionId,
                                                 @Body VoiceSample sample);

    @POST("verification/video/dynamic/file")
    Call<Void> addDynamicVerificationVideo(@Header("X-Session-Id") String sessionId,
                                           @Header("X-Transaction-Id") String transactionId,
                                           @Body Video video);

    @GET("verification/result")
    Call<VerificationResponse> verify(@Header("X-Session-Id") String sessionId,
                                      @Header("X-Transaction-Id") String transactionId,
                                      @Query("closeSession") boolean closeFlag);

    @GET("verification/result")
    Call<VerificationScoresResponse> scoreVerify(@Header("X-Session-Id") String sessionId,
                                                 @Header("X-Transaction-Id") String transactionId,
                                                 @Query("closeSession") boolean closeFlag);

    @DELETE("verification")
    Call<Void> closeVerification(@Header("X-Session-Id") String sessionId,
                                 @Header("X-Transaction-Id") String transactionId);

}
