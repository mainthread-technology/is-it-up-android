package technology.mainthread.apps.isitup.view.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.view.listeners.ToolbarProvider;

public abstract class BaseActivity extends AppCompatActivity implements ToolbarProvider {

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setToolbarTitle(int resId) {
        mToolbar.setTitle(resId);
    }
}
