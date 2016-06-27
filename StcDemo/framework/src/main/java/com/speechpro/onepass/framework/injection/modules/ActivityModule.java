package com.speechpro.onepass.framework.injection.modules;

import android.app.Activity;
import com.speechpro.onepass.framework.injection.PerActivity;
import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity state and expose it to the graph.
 *
 * @author volobuev
 * @since 14.01.2016
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides @PerActivity Activity activity() {
        return this.activity;
    }
}
