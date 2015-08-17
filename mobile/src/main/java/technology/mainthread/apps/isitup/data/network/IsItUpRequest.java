package technology.mainthread.apps.isitup.data.network;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import rx.Observable;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;

public interface IsItUpRequest {

    @Headers({"User-Agent: Is it up? For Android v2"})
    @GET("/{site}.json")
    Observable<IsItUpInfo> checkSiteObservable(@Path("site") String site);

    @Headers({"User-Agent: Is it up? For Android v2"})
    @GET("/{site}.json")
    IsItUpInfo checkSite(@Path("site") String site);
}
