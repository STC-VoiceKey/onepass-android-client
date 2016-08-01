package com.speechpro.onepass.framework.presenter.episode;

/**
 * @author volobuev
 * @since 22.04.16
 */
public class Episode {
    private final int stage;
    private final int enrollPhrases;
    private final int phraseDynamic;

    public Episode(int stage, int enrollPhrases, int phraseDynamic) {
        this.stage = stage;
        this.enrollPhrases = enrollPhrases;
        this.phraseDynamic = phraseDynamic;
    }

    public int getStage() {
        return stage;
    }

    public int getEnrollPhrases() {
        return enrollPhrases;
    }

    public int getPhraseDynamic() {
        return phraseDynamic;
    }
}
