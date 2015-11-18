package technology.mainthread.apps.isitup.util;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxSchedulerHelper {

    private RxSchedulerHelper() {
    }

    // http://blog.danlew.net/2015/03/02/dont-break-the-chain/
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
