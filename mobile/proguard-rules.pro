# Retrofit, OkHttp, Gson
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn rx.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Application classes that will be serialized/deserialized over Gson
-keep interface technology.mainthread.apps.isitup.data.network.IsItUpRequest { *; }
-keep class technology.mainthread.apps.isitup.data.network.IsItUpRequest { *; }
-keep class technology.mainthread.apps.isitup.data.vo.IsItUpInfo { *; }

-keep class com.google.android.apps.dashclock.** {*;}

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# RxAndroid
-dontwarn rx.internal.util.**

# Google Play Services
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Support libraries
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }