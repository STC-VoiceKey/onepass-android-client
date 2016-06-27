package com.speechpro.onepass.core.rest;

import com.speechpro.onepass.core.rest.data.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author volobuev
 * @since 19.04.16
 */
public interface OnePassService {

    @POST("person")
    Call<Void> createPerson(@Body CreatePersonRequest person);

    @GET("person/{id}")
    Call<Person> readPerson(@Path("id") String id);

    @DELETE("person/{id}")
    Call<Void> deletePerson(@Path("id") String id);

    @POST("person/{id}/voice/dynamic/sample")
    Call<Void> addVoiceSample(@Path("id") String id, @Body VoiceSample sample);

    @POST("person/{id}/voice/dynamic/feature")
    Call<Void> addVoiceFeature(@Path("id") String id, @Body Data features);

    @DELETE("person/{id}/voice/dynamic")
    Call<Void> deleteVoice(@Path("id") String id);

    @POST("person/{id}/face/sample")
    Call<Void> addFaceSample(@Path("id") String id, @Body FaceSample sample);

    @POST("person/{id}/face/model")
    Call<Void> addFaceModel(@Path("id") String id, @Body FaceModel model);

    @DELETE("person/{id}/face")
    Call<Void> deleteFace(@Path("id") String id);

    @GET("verification/start/{id}")
    Call<VerificationSessionResponse> startVerificationSession(@Path("id") String id);

    @POST("verification/{id}/voice/dynamic/sample")
    Call<Void> addVerificationVoiceSample(@Path("id") String id, @Body VoiceSample sample);

    @POST("verification/{id}/voice/dynamic/feature")
    Call<Void> addVerificationVoiceFeature(@Path("id") String id, @Body Data features);

    @POST("verification/{id}/face/sample")
    Call<Void> addVerificationFaceSample(@Path("id") String id, @Body FaceSample sample);

    @POST("verification/{id}/face/model")
    Call<Void> addVerificationFaceModel(@Path("id") String id, @Body FaceModel model);

    @POST("verification/{id}/video/dynamic")
    Call<Void> addVerificationVideo(@Path("id") String id, @Body Video video);

    @GET("verification/{id}")
    Call<VerificationResult> verify(@Path("id") String id, @Query("close_session") boolean closeFlag);

    @DELETE("verification/{id}")
    Call<Void> closeVerification(@Path("id") String id);
}
