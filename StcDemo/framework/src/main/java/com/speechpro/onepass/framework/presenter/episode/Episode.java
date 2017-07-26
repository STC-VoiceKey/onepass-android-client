package com.speechpro.onepass.framework.presenter.episode;

import android.content.Context;

import com.speechpro.onepass.framework.util.NumberUtils;

/**
 * @author volobuev
 * @since 22.04.16
 */
public class Episode {

    private final int stage;
    private String enrollPhrases;

    public Episode(int stage, String enrollPhrases) {
        this.stage = stage;
        this.enrollPhrases = enrollPhrases;
    }

    public Episode(int stage) {
        this.stage = stage;
        this.enrollPhrases = NumberUtils.createRandomValues(10);
    }

    public int getStage() {
        return stage;
    }

    public String getEnrollPhrases() {
        return enrollPhrases;
    }

    public String getPhraseDynamic(Context ctx) {
        return NumberUtils.convertPhraseDecimalToString(ctx, this.enrollPhrases);
    }

}


