package technology.mainthread.apps.isitup.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.data.db.AsyncFavourites;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.network.IsItUpRequest;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import technology.mainthread.apps.isitup.data.vo.enumeration.StatusCode;
import technology.mainthread.apps.isitup.ui.listeners.ToolbarProvider;
import timber.log.Timber;

public class ResultFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String PARAM_DOMAIN = "param_domain";

    @Inject
    IsItUpRequest request;
    @Inject
    @AsyncFavourites
    FavouritesTable favouritesTable;

    @Bind(R.id.progress)
    View progress;
    @Bind(R.id.txt_domain)
    TextView domainText;
    @Bind(R.id.txt_status)
    TextView statusText;
    @Bind(R.id.txt_details)
    TextView detailsText;
    @Bind(R.id.txt_ip_address)
    TextView ipAddressText;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.btn_save_delete)
    Button saveDeleteButton;
    @Bind(R.id.txt_last_checked)
    TextView lastChecked;

    private IsItUpInfo isItUpInfo;
    private ShareActionProvider shareActionProvider;
    private boolean siteSaved;
    private Subscription checkSiteSubscription = Subscriptions.empty();
    private ToolbarProvider toolbarProvider;

    private Observer<IsItUpInfo> checkSiteSubscriber = new Observer<IsItUpInfo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            progress.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.VISIBLE);
            swipeLayout.setRefreshing(false);

            if (siteSaved) {
                // if in db show saved check
                String domain = getArguments().getString(PARAM_DOMAIN);
                ResultFragment.this.isItUpInfo = favouritesTable.getFavourite(domain);
                setData();
            } else if (e instanceof RetrofitError) {
                switch (((RetrofitError) e).getKind()) {
                    case NETWORK:
                        showError(R.string.error_network_message, R.string.error_network_detail);
                        break;
                    case HTTP:
                        showError(R.string.error_http_message, R.string.error_http_detail);
                        break;
                    default:
                        Timber.e(e, "Unknown retrofit error");
                        showError(R.string.error_unknown_message, R.string.error_unknown_detail);
                        break;
                }
            }
        }

        @Override
        public void onNext(IsItUpInfo isItUpInfo) {
            ResultFragment.this.isItUpInfo = isItUpInfo;
            progress.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.VISIBLE);
            swipeLayout.setRefreshing(false);

            setData();

            if (siteSaved) {
                // update site if saved
                favouritesTable.update(isItUpInfo);
            }
        }
    };

    public static Fragment newInstance(String domain) {
        Fragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_DOMAIN, domain);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsItUpApp.get(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, rootView);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.primary_light);

        String domain = getArguments().getString(PARAM_DOMAIN);
        siteSaved = favouritesTable.hasSite(domain);
        updateSaveDeleteButtonView();

        checkSite(domain);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        checkSiteSubscription.unsubscribe();
        super.onDestroyView();
    }

    private void checkSite(String domain) {
        Observable<IsItUpInfo> checkSiteObservable = request.checkSiteObservable(domain)
                .compose(this.<IsItUpInfo>bindToLifecycle())
                .compose(this.<IsItUpInfo>applySchedulers());
        checkSiteSubscription = checkSiteObservable.subscribe(checkSiteSubscriber);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_result, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareMessage(getShareMessage(isItUpInfo));
    }

    @Override
    public void onRefresh() {
        checkSite(getArguments().getString(PARAM_DOMAIN));
    }

    @OnClick(R.id.btn_save_delete)
    void onSaveDeleteClicked() {
        if (siteSaved) {
            favouritesTable.delete(getArguments().getString(PARAM_DOMAIN));
            siteSaved = false;
        } else if (isItUpInfo != null) {
            favouritesTable.add(isItUpInfo);
            siteSaved = true;
        }
        updateSaveDeleteButtonView();
    }

    @OnClick(R.id.btn_visit)
    void onVisitClicked() {
        if (isItUpInfo != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + isItUpInfo.getDomain()));
            startActivity(browserIntent);
        }
    }

    private void updateSaveDeleteButtonView() {
        saveDeleteButton.setText(siteSaved ? R.string.delete : R.string.save);
        saveDeleteButton.setBackgroundResource(siteSaved ? R.drawable.button_background_red : R.drawable.button_background_green);
    }

    private void setData() {
        if (isItUpInfo != null) {
            domainText.setText(isItUpInfo.getDomain());
            statusText.setText(getStatusText(StatusCode.fromCode(isItUpInfo.getStatusCodeInteger())));
            ipAddressText.setText(isItUpInfo.getResponseIp());
            if (isItUpInfo.getLastChecked() != 0) {
                // if we are offline show the last checked time
                lastChecked.setVisibility(View.VISIBLE);
                CharSequence lastCheckedTime = DateUtils.getRelativeTimeSpanString(isItUpInfo.getLastChecked());
                lastChecked.setText(getString(R.string.result_last_checked, lastCheckedTime));
            } else {
                lastChecked.setVisibility(View.GONE);
            }
            setShareMessage(getShareMessage(isItUpInfo));

            if (isItUpInfo.getStatusCode() == StatusCode.UP) {
                int colorGreen = getResources().getColor(R.color.primary);
                domainText.setTextColor(colorGreen);
                statusText.setTextColor(colorGreen);
                ipAddressText.setTextColor(getResources().getColor(R.color.accent));
                detailsText.setText(getString(R.string.result_detail,
                        (int) (isItUpInfo.getResponseTime() * 1000),
                        isItUpInfo.getResponseCode()));
                detailsText.setVisibility(View.VISIBLE);
                toolbarProvider.getToolbar().setBackgroundColor(colorGreen);
            } else {
                setErrorColours();
            }
            setLollipopBars(isItUpInfo.getStatusCode() == StatusCode.UP);
        }
    }

    private void setErrorColours() {
        int colorRed = getResources().getColor(R.color.second_primary);
        domainText.setTextColor(colorRed);
        statusText.setTextColor(colorRed);
        ipAddressText.setTextColor(getResources().getColor(R.color.second_accent));
        toolbarProvider.getToolbar().setBackgroundColor(colorRed);
    }

    private void showError(@StringRes int messageResId, @StringRes int detailResId) {
        domainText.setText(getArguments().getString(PARAM_DOMAIN));
        statusText.setText(messageResId);
        detailsText.setText(detailResId);
        setErrorColours();
        setLollipopBars(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setLollipopBars(boolean isUp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int primaryDark = getResources().getColor(isUp ? R.color.primary_dark : R.color.second_primary_dark);
            getActivity().getWindow().setStatusBarColor(primaryDark);
        }
    }

    private void setShareMessage(String message) {
        if (shareActionProvider != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            shareActionProvider.setShareIntent(intent);
        }
    }

    private int getStatusText(StatusCode statusCode) {
        int resId;
        switch (statusCode) {
            case UP:
                resId = R.string.status_up;
                break;
            case DOWN:
                resId = R.string.status_down;
                break;
            case INVALID_DOMAIN:
                resId = R.string.status_invalid_domain;
                break;
            default:
                resId = R.string.status_error;
                break;
        }

        return resId;
    }

    private String getShareMessage(IsItUpInfo response) {
        String message = getString(R.string.share_message_default);
        if (response != null) {
            switch (StatusCode.fromCode(response.getStatusCodeInteger())) {
                case UP:
                    message = getString(R.string.share_message_up, response.getDomain());
                    break;
                case DOWN:
                    message = getString(R.string.share_message_down, response.getDomain());
                    break;
                default:
                    break;
            }
        }
        return message;
    }
}
