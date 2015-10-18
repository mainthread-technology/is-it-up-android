package technology.mainthread.apps.isitup.data.network;

import android.content.res.Resources;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import technology.mainthread.apps.isitup.BuildConfig;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.data.StethoUtil;

@Module
public class NetworkModule {

    private final Resources resources;

    public NetworkModule(Resources resources) {
        this.resources = resources;
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClient() {
        final OkHttpClient client = new OkHttpClient();

        // stetho interceptor
        StethoUtil.addStethoInterceptor(client);

        return client;
    }

    @Provides
    @Singleton
    IsItUpRequest isItUpRequest(OkHttpClient okHttpClient) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(resources.getString(R.string.endpoint))
                .setLogLevel(BuildConfig.DEBUG
                        ? RestAdapter.LogLevel.FULL
                        : RestAdapter.LogLevel.NONE)
                .build();

        return restAdapter.create(IsItUpRequest.class);
    }

}
