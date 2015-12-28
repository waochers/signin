package project.stutisrivastava.waochers.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by stutisrivastava on 28/12/15.
 */
public class SystemManager {

    private static Activity currentActivity;
    private static Context context;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
        context = activity.getApplicationContext();
    }

    public static void setCurrentContext(Context ctx){
        context = ctx;
    }

    public static Context getCurrentContext() {
        return context;
    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkinfo = cm.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isConnected()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
