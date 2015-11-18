package technology.mainthread.apps.isitup.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({StatusCode.ERROR, StatusCode.UP, StatusCode.DOWN, StatusCode.INVALID_DOMAIN})
public @interface StatusCode {
    int ERROR = 0;
    int UP = 1;
    int DOWN = 2;
    int INVALID_DOMAIN = 3;
}
