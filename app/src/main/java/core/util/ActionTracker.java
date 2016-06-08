package core.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import core.base.BaseApplication;

@SuppressWarnings({"PointlessBooleanExpression", "ResultOfMethodCallIgnored"})
public final class ActionTracker {

    private static File action;
    private static FileWriter fw;

    public static void openActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.US);

            action = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/"
                    + BaseApplication.getContext().getPackageName()
                    .replace(".", "_"), "action_" + formatter.format(new Date(System.currentTimeMillis())) + ".txt");
            if (!action.exists())
                action.createNewFile();
            fw = new FileWriter(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void enterScreen(String name, Screen screen) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(name)) {
                String append = "";
                switch (screen) {
                    case ACTIVITY:
                        append = "> " + name + " > Visible\n";
                        break;
                    case FRAGMENT:
                        append = "   > " + name + " > Visible\n";

                }
                fw.append(append);
                fw.flush();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exitScreen(String name) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(name)) {
                fw.append("< ").append(name).append("\n");
                fw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void performAction(String action) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(action)) {
                fw.append("      > touch view: ").append(action).append("\n");
                fw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeWithCrashActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            if (fw != null) {
                fw.append(">>CRASHED<<");
                fw.flush();
                fw.close();
            }
            fw = null;
            action = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            if (fw != null) {
                fw.append(">>EXIT<<");
                fw.flush();
                fw.close();
            }
            fw = null;
            action = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Screen {
        ACTIVITY, FRAGMENT
    }
}
