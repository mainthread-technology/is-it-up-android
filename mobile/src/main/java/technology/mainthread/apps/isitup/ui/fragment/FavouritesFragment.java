package technology.mainthread.apps.isitup.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.data.db.AsyncFavourites;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.network.FavouritesChecker;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import technology.mainthread.apps.isitup.ui.activity.ResultActivity;
import technology.mainthread.apps.isitup.ui.adapter.FavouritesAdapter;
import technology.mainthread.apps.isitup.ui.listeners.ToolbarProvider;
import timber.log.Timber;

public class FavouritesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FavouritesAdapter.Listener {

    @Inject
    FavouritesChecker checker;
    @Inject
    @AsyncFavourites
    FavouritesTable favouritesTable;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.list_favourites)
    RecyclerView favouritesRecyclerView;

    private FavouritesAdapter adapter;
    private ToolbarProvider toolbarProvider;
    private Subscription refreshSubscription = Subscriptions.empty();

    private final Observer<Void> refreshSubscriber = new Observer<Void>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            swipeLayout.setRefreshing(false);

            if (e instanceof RetrofitError) {
                int message;

                switch (((RetrofitError) e).getKind()) {
                    case NETWORK:
                        message = R.string.error_network_message;
                        break;
                    case HTTP:
                        message = R.string.error_http_message;
                        break;
                    default:
                        Timber.e("Unknown retrofit error", e);
                        message = R.string.error_unknown_message;
                        break;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onNext(Void aVoid) {
            swipeLayout.setRefreshing(false);
            adapter.reloadData(favouritesTable.getAllFavourites());
        }
    };

    public static Fragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsItUpApp.get(getActivity()).inject(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            toolbarProvider = (ToolbarProvider) activity;
        } catch (Exception e) {
            throw new ClassCastException("Activity must implement ToolbarProvider.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        ButterKnife.bind(this, rootView);

        toolbarProvider.setToolbarTitle(R.string.title_favorites);

        adapter = new FavouritesAdapter(getResources(), favouritesTable.getAllFavourites(), this);
        favouritesRecyclerView.setHasFixedSize(true);
        favouritesRecyclerView.setAdapter(adapter);
        favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favouritesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.primary_light);
        return rootView;
    }

    @Override
    public void onPause() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
            swipeLayout.destroyDrawingCache();
            swipeLayout.clearAnimation();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        refreshSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        Observable<Void> refreshObservable = checker.refresh()
                .compose(this.<Void>bindToLifecycle())
                .compose(this.<Void>applySchedulers());
        refreshSubscription = refreshObservable.subscribe(refreshSubscriber);
    }

    @Override
    public void onItemClicked(IsItUpInfo item) {
        startActivity(ResultActivity.getResultActivityIntent(getActivity(), item.getDomain()));
    }
}
