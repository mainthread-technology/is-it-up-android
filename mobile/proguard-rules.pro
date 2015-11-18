# Retrofit, OkHttp, Gson
-keepattributes *Annotation*
-keepattributes Signature

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
-keep class technology.mainthread.apps.isitup.model.IsItUpInfo { *; }

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

# RxJava
-dontwarn rx.**
-keep class rx.internal.util.** { *; }

# Google Play Services - ApacheHttpClient warnings
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Support libraries
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }