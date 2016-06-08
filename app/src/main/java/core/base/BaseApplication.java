package core.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;
import com.example.commonframe.R;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import core.util.Constant;
import core.util.LocalReporter;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

@SuppressWarnings("unused")
@ReportsCrashes(customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT}, mode = ReportingInteractionMode.SILENT)
public class BaseApplication extends Application {
    private static Context mContext;
    private static AppCompatActivity mActiveActivity;
//    private static RefWatcher mRefWatcher;

    public static Context getContext() {
        return mContext;
    }

    public static AppCompatActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(AppCompatActivity active) {
        mActiveActivity = active;
    }

//    public static RefWatcher getRefWatcher() {
//        return mRefWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initACRA();
//        initLeakDetection();
    }

//    private void initLeakDetection() {
//        mRefWatcher = LeakCanary.install(this);
//    }

    private void initACRA() {
        if (Constant.DEBUG) {
            ACRA.init(this);
            ACRA.getErrorReporter().setReportSender(new LocalReporter());
        }
    }

    protected void initActiveAndroidDB() {
        ActiveAndroid.initialize(this);
    }

    protected void initImageLoader(ImageLoaderConfiguration config) {
        if (config == null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .cacheInMemory(Constant.MEMORY_CACHE)
                    .cacheOnDisk(Constant.DISC_CACHE).resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.loading)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            config = new ImageLoaderConfiguration.Builder(
                    mContext).memoryCache(new WeakMemoryCache())
                    .memoryCache(new LruMemoryCache(Constant.LRU_CACHE_SIZE))
                    .memoryCacheSize(Constant.MEMORY_CACHE_SIZE)
                    .diskCacheSize(Constant.DISC_CACHE_SIZE)
                    .diskCacheFileCount(Constant.DISC_CACHE_COUNT)
                    .denyCacheImageMultipleSizesInMemory().threadPoolSize(10)
                    .threadPriority(Thread.MAX_PRIORITY)
                    .defaultDisplayImageOptions(options).build();
        }
        ImageLoader.getInstance().init(config);
    }
}
