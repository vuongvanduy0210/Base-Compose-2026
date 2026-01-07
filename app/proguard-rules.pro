# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }
-keep public class com.android.installreferrer.** { *; }

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-dontwarn net.premiumads.**
-dontwarn net.premiumads.sdk.**
-keepclassmembers class net.premiumads.** { public *; }
-keep public class net.premiumads.**
-keep class net.premiumads.sdk.** { *;}
-keep class net.premiumads.sdk.admob.** { *;}
-keep public class com.google.android.gms.** { public protected *; }
-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe

# Giữ tất cả các class implement Serializable (nếu truyền qua Bundle, Intent)
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class com.google.android.gms.** { public protected *; }
-keep class java.time.** { *; }

# Keep annotations used by Parcelize
#-keep @kotlinx.parcelize.Parcelize class * { *; }
-keep class kotlinx.parcelize.** { *; }

# Keep Navigation Safe Args generated classes
-keep class * extends androidx.navigation.NavArgs { *; }

# Giữ lại tất cả class implement Parcelable (CREATOR cần thiết cho Intent)
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class com.google.gson.** { *; }

-keepclassmembers class * extends android.os.AsyncTask {
	protected void onPreExecute();
	protected *** doInBackground(...);
	protected void onPostExecute(...);
}
-keep public class * implements java.lang.reflect.Type

-keep class java.**,javax.**,com.sun.**,android.** {
   static final %                *;
   static final java.lang.String *;
  *;
}

-keep class <1> {
  <init>(...);
}

-keep class com.vio.basemvvm.data.remote.dto.** { *; }

# Nếu dùng Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep class com.vio.basemvvm.data.remote.dto.**$$serializer { *; }
-keepclasseswithmembers class com.vio.basemvvm.data.remote.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keep class kotlin.collections.** { *; }
# sdk
-keep class com.bun.miitmdid.** { *; }
-keep interface com.bun.supplier.** { *; }
# asus
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep class com.asus.msa.sdid.** { *; }
-keep class kotlin.jvm.internal.Intrinsics{ *; }
-keep class kotlin.collections.**{ *; }
-keep class kotlin.jvm.internal.** { *; }

-dontwarn com.applovin.**
-dontwarn com.google.ads.mediation.applovin.AppLovinMediationAdapter
-keep class com.applovin.** { *; }
-keep class com.google.ads.mediation.applovin.** { *; }
-keep class com.applovin.mediation.** { *; }

# Keep LogSessionId class and related classes
-keep class android.media.metrics.LogSessionId { *; }
-keep class android.media.metrics.** { *; }

# Keep Media3 classes that use reflection
-keep class androidx.media3.** { *; }
-dontwarn android.media.metrics.**

# Alternative: If you want to be more specific
-keepclassmembers class androidx.media3.transformer.DefaultAssetLoaderFactory {
    *;
}