package technology.mainthread.apps.isitup.data.network;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import javax.inject.Inject;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import technology.mainthread.apps.isitup.BuildConfig;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.background.service.IsItUpDashClockService;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.db.SyncFavourites;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import timber.log.Timber;

public class FavouritesChecker {

    private final Context context;
    private final Resources resources;
    private final FavouritesTable favouritesTable;
    private final Tracker tracker;

    @Inject
    public FavouritesChecker(Context context,
                             Resources resources,
                             @SyncFavourites FavouritesTable favouritesTable,
                             Tracker tracker) {
        this.context = context;
        this.resources = resources;
        this.favouritesTable = favouritesTable;
        this.tracker = tracker;
    }

    public Observable<Void> refresh() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                Timber.d("Creating request");
                IsItUpRequest request = createRequest(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Timber.e(cause, "Request failed");
                        subscriber.onError(cause);
                        return cause;
                    }
                });

                Timber.d("Checking all favourites");

                List<IsItUpInfo> allFavourites = favouritesTable.getAllFavourites();

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("background")
                        .setAction("refresh")
                        .setLabel(String.format("syncing %d items", allFavourites.size()))
                        .build());

                for (final IsItUpInfo favourite : allFavourites) {
                    if (subscriber.isUnsubscribed()) {
                        break;
                    }
                    Timber.d("Checking favourite %s", favourite.getDomain());
                    IsItUpInfo info = request.checkSite(favourite.getDomain());
                    if (info != null) {
                        favouritesTable.update(favourite.getId(), info);
                    }
                }
                IsItUpDashClockService.updateDashClock(context);

                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    private IsItUpRequest createRequest(ErrorHandler errorHandler) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(resources.getString(R.string.endpoint))
                .setErrorHandler(errorHandler)
                .build();
        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return restAdapter.create(IsItUpRequest.class);
    }

}
