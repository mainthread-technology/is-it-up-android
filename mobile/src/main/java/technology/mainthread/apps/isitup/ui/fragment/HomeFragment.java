package technology.mainthread.apps.isitup.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.ui.activity.ResultActivity;
import technology.mainthread.apps.isitup.ui.listeners.KeyboardManager;
import technology.mainthread.apps.isitup.ui.listeners.ToolbarProvider;

public class HomeFragment extends BaseFragment implements TextView.OnEditorActionListener {

    private ToolbarProvider toolbarProvider;
    private KeyboardManager keyboardManager;

    @Bind(R.id.site_edit_text)
    EditText siteEditTxt;

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            toolbarProvider = (ToolbarProvider) activity;
        } catch (Exception e) {
            throw new ClassCastException("Activity must implement ToolbarProvider.");
        }

        try {
            keyboardManager = (KeyboardManager) activity;
        } catch (Exception e) {
            throw new ClassCastException("Activity must implement KeyboardManager.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        toolbarProvider.setToolbarTitle(R.string.app_name);
        siteEditTxt.setOnEditorActionListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        siteEditTxt.requestFocus();
        keyboardManager.showKeyboard(siteEditTxt);
    }

    @Override
    public void onPause() {
        keyboardManager.hideKeyboard();
        super.onPause();
    }

    @OnClick(R.id.request_button)
    void onRequestButtonClicked() {
        checkSite();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            checkSite();
            return true;
        }
        return false;
    }

    private void checkSite() {
        keyboardManager.hideKeyboard();
        startActivity(ResultActivity.getResultActivityIntent(
                getActivity(),
                siteEditTxt.getText().toString()
        ));
    }
}
