package com.speechpro.onepass.core.rest.api;

import android.util.Log;

import com.speechpro.onepass.core.exception.BadRequestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.ForbiddenException;
import com.speechpro.onepass.core.exception.InternalServerException;
import com.speechpro.onepass.core.exception.NotFoundException;
import com.speechpro.onepass.core.exception.ServiceUnavailableException;
import com.speechpro.onepass.core.rest.OnePassService;
import com.speechpro.onepass.core.rest.data.Data;
import com.speechpro.onepass.core.rest.data.Person;
import com.speechpro.onepass.core.rest.data.SessionIdResponse;
import com.speechpro.onepass.core.rest.data.RegistationSessionResponse;
import com.speechpro.onepass.core.rest.data.StartSessionRequest;
import com.speechpro.onepass.core.rest.data.VerificationResponse;
import com.speechpro.onepass.core.rest.data.VerificationScoresResponse;
import com.speechpro.onepass.core.rest.data.VerificationSessionResponse;
import com.speechpro.onepass.core.rest.data.Video;
import com.speechpro.onepass.core.rest.data.VoiceFile;
import com.speechpro.onepass.core.rest.data.VoiceSample;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.ScoreVerify;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.core.utils.Converter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author volobuev
 * @since 19.04.16
 */
public class RetroRestAPI implements ITransport {

    private final OnePassService service;

    public RetroRestAPI(String url) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        long t1 = System.nanoTime();
                        Log.d("OkHttp", String.format("Sending request %s %s on %s%n%s",
                                request.method(), request.url(), chain.connection(), request.headers()));

                        Log.d("OkHttp", bodyToString(request));

                        okhttp3.Response response = chain.proceed(request);

                        long t2 = System.nanoTime();
                        Log.d("OkHttp", String.format("Received response for %s in %.1fms%n%s",
                                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

                        String bodyString = response.body().string();
                        Log.d("OkHttp", bodyString.toString());

                        return response.newBuilder()
                                .body(ResponseBody.create(response.body().contentType(), bodyString))
                                .build();

                    }
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(OnePassService.class);
    }

    @Override
    public String startSession(String username,
                               String password,
                               int domainId) throws CoreException {
        retrofit2.Response response = null;
        StartSessionRequest startSessionRequest = new StartSessionRequest(username, password, domainId);

        try {
            response = service.startSession(startSessionRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.processCode(response);
        return ((SessionIdResponse) response.body()).sessionId;
    }

    @Override
    public void closeSession(String sessionId) throws CoreException {
        processResponse(service.closeSession(sessionId));;
    }

    @Override
    public RegistrationTransaction startRegistrationTransaction(String sessionId,
                                                                String personId) throws CoreException {
        retrofit2.Response response = null;

        try {
            response = service.startRegistrationSession(sessionId, personId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.processCode(response);
        String transactionId = ((RegistationSessionResponse) response.body()).transactionId;
        return new RegistrationTransaction(this, sessionId, transactionId, personId, false);
    }

    @Override
    public PersonSession readPerson(String sessionId,
                                    String personId) throws CoreException {
        retrofit2.Response response = null;

        try {
            response = service.readPerson(sessionId, personId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.processCode(response);
        Person person = (Person) response.body();
        return person != null ? Converter.convertPerson(this, sessionId, person) : null;
    }

    @Override
    public void deletePerson(String sessionId,
                             String personId) throws CoreException {
        processResponse(service.deletePerson(sessionId, personId));
    }

    @Override
    public void addVoiceDynamicFile(String sessionId,
                                    String transactionId,
                                    byte[] voiceFile,
                                    String passphrase,
                                    int gender,
                                    int channel) throws CoreException {
        processResponse(service.addVoiceDynamicFile(sessionId,
                transactionId,
                new VoiceFile(voiceFile, passphrase, gender, channel)));


    }

    @Override
    public void addVoiceDynamicSample(String sessionId,
                                      String transactionId,
                                      byte[] voiceSample,
                                      String passphrase,
                                      int gender,
                                      int samplingRate) throws CoreException {
        processResponse(service.addVoiceDynamicSample(sessionId,
                transactionId,
                new VoiceSample(voiceSample, passphrase, gender, samplingRate)));
    }

    @Override
    public void addFaceFile(String sessionId,
                            String transactionId,
                            byte[] faceModel) throws CoreException {
        processResponse(service.addFaceFile(sessionId, transactionId, new Data(faceModel)));
    }

    @Override
    public VerificationTransaction startVerificationTransaction(String sessionId,
                                                                String personId) throws CoreException {
        retrofit2.Response response = null;

        try {
            response = service.startVerificationTransaction(sessionId, personId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.processCode(response);
        return Converter.convertVerification(this, (VerificationSessionResponse) response.body(), sessionId);
    }

    @Override
    public void addDynamicVerificationVoiceSample(String sessionId,
                                                  VerificationTransaction session,
                                                  byte[] voiceSample,
                                                  int gender,
                                                  int samplingRate) throws CoreException {
        String transactionId = session.getTransactionId();
        String passphrase = session.getPassphrase();
        processResponse(service.addDynamicVerificationVoiceSample(sessionId,
                transactionId,
                new VoiceSample(voiceSample, passphrase, gender)));
    }

    @Override
    public void addDynamicVerificationVideo(String sessionId,
                                            String transactionId,
                                            String passphrase,
                                            byte[] video) throws CoreException {
        processResponse(service.addDynamicVerificationVideo(sessionId,
                transactionId,
                new Video(video, passphrase)));
    }

    @Override
    public boolean verify(String sessionId,
                          String transactionId,
                          boolean closeFlag) throws CoreException {
        retrofit2.Response response = null;

        try {
            response = service.verify(sessionId, transactionId, closeFlag).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.processCode(response);
        return Converter.convertVerificationResult((VerificationResponse) response.body());
    }

    @Override
    public ScoreVerify scoreVerify(String sessionId,
                                   String transactionId,
                                   boolean closeFlag) throws CoreException {
        retrofit2.Response response = null;

        try {
            response = service.scoreVerify(sessionId, transactionId, closeFlag).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VerificationScoresResponse scores = (VerificationScoresResponse) response.body();

        this.processCode(response);
        return new ScoreVerify(scores.face, scores.dynamicVoice, scores.staticVoice, scores.fused, scores.liveness);
    }

    public void closeVerification(String sessionId,
                                  String transactionId) throws CoreException {
        processResponse(service.closeVerification(sessionId, transactionId));
    }

    private <T> void processResponse(Call<T> call) throws CoreException {
        try {
            retrofit2.Response e = call.execute();
            this.processCode(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processCode(retrofit2.Response response) throws CoreException {
        if (response == null) {
            throw new ServiceUnavailableException("OnePass service is unavailable", (String) null);
        } else {
            int code = response.code();
            String message = response.raw().message();
            String reason = null;

            try {
                ResponseBody error = response.errorBody();
                if (error != null) {
                    reason = error.string();
                }
            } catch (IOException e) {
                ;
            }

            switch (code) {
                case 400:
                    throw new BadRequestException(message, reason);
                case 403:
                    throw new ForbiddenException(message, reason);
                case 404:
                    throw new NotFoundException(message, reason);
                case 500:
                    throw new InternalServerException(message, reason);
                case 503:
                    throw new ServiceUnavailableException(message, reason);
                default:
            }
        }
    }

    private String bodyToString(Request request) {
        try {
            Request npe = request.newBuilder().build();
            Buffer buffer = new Buffer();
            npe.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException var4) {
            return "parse error";
        } catch (NullPointerException var5) {
            return "no body";
        }
    }
}
