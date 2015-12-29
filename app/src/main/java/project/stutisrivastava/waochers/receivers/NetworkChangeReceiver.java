package project.stutisrivastava.waochers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.util.Alert;
import project.stutisrivastava.waochers.util.SystemManager;

/**
 * Created by stutisrivastava on 28/12/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        SystemManager.setCurrentContext(context);
        if(!checkInternet())
        {
            if(SystemManager.getCurrentActivity()!=null){
                Alert.showConfirmationDialog(context, SystemManager.getConfirmationListener(), context.getString(R.string.title_no_internet), context.getString(R.string.no_internet_message));
            }
        }

    }


    boolean checkInternet() {
        if (SystemManager.isNetworkConnected())
            return true;
        return false;
    }

}
