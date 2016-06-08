package core.util;

import android.app.Activity;
import android.util.Log;

import core.base.BaseActivity;
import core.base.BaseApplication;
import core.base.BaseMultipleFragment;
import core.base.BaseMultipleFragmentActivity;

@SuppressWarnings({"PointlessBooleanExpression", "unused", "ConstantConditions"})
public class DLog {

    private static boolean isValid(String str) {
        return !(str == null);
    }

    public static void v(String Tag, String log) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.v(Tag, log);
    }

    public static void v(String Tag, String log, Throwable e) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.v(Tag, log, e);
    }

    public static void d(String Tag, String log) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.d(Tag, log);
    }

    public static void d(String Tag, String log, Throwable e) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.d(Tag, log, e);
    }

    public static void e(String Tag, String log) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.e(Tag, log);
    }

    public static void e(String Tag, String log, Throwable e) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.e(Tag, log, e);
    }

    public static void i(String Tag, String log) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.i(Tag, log);
    }

    public static void i(String Tag, String log, Throwable e) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.i(Tag, log, e);
    }

    public static void w(String Tag, String log) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.w(Tag, log);
    }

    public static void w(String Tag, String log, Throwable e) {
        if (Constant.DEBUG && isValid(Tag) && isValid(log))
            Log.w(Tag, log, e);
    }

    public static void log(String log) {
        if (Constant.DEBUG && isValid(log)) {
            Activity current = BaseApplication.getActiveActivity();
            if (current != null) {
                if (current instanceof BaseActivity) {
                    Log.d(current.getClass().getSimpleName(), log);
                } else if (current instanceof BaseMultipleFragmentActivity) {
                    String tag = current.getClass().getSimpleName();
                    BaseMultipleFragment fragment = ((BaseMultipleFragmentActivity) current).getTopFragment(((BaseMultipleFragmentActivity) current).getMainContainerId());
                    if (fragment != null) {
                        tag += " >> " + fragment.getClass().getSimpleName();
                    }
                    Log.d(tag, log);
                }
            }
        }
    }
}
