package project.stutisrivastava.waochers.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


/**
 * Created by stutisrivastava on 3/25/16.
 */
public class AddressResultReceiver extends ResultReceiver {

    private static final String TAG = "AddressResultReceiver";
    private Receiver mReceiver;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        Log.e(TAG, "onReceiveResult");
        // Display the address string
        // or an error message sent from the intent service.
        if (mReceiver != null) {
            Log.e(TAG, "sending to activity");
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
