package project.stutisrivastava.waochers.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.activities.SampleActivityBase;
import project.stutisrivastava.waochers.receivers.Receiver;
import project.stutisrivastava.waochers.util.Alert;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.FetchLocation;
import project.stutisrivastava.waochers.util.SystemManager;

public class HomeActivity extends SampleActivityBase implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Receiver {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
   // public static final String MyPREFERENCES = "MyPrefs" ;
    public static Handler messageHandler = new MessageHandler();
    Button BLocation;
    Button BGps;
    ImageView img;
    private String TAG = "HomeActivity";
    private FetchLocation fetchLocationInstance;
    private ProgressDialog progressDialog;
    private String result_gps;
    private String result_place;
    SharedPreferences prefs;
    SharedPreferences mSharedPreferences;
    private Address locality_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);

        SystemManager.setCurrentActivity(this);
        SystemManager.setCurrentContext(getApplicationContext());

        super.onCreateDrawer();
        super.setDrawerContent();
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        img = (ImageView) findViewById(R.id.img1);
        BLocation = (Button) findViewById(R.id.butLocation);
        BGps = (Button) findViewById(R.id.butGps);
       // prefs=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        BLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                openAutocompleteActivity();

            }
        });
        BGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation();
            }
        });
    }

    private void fetchLocation() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Location..");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        fetchLocationInstance = FetchLocation.getInstance(new Messenger(messageHandler), this);
        fetchLocationInstance.buildGoogleApiClient();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homeactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        SystemManager.setCurrentActivity(this);
        SystemManager.setCurrentContext(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //SystemManager.setCurrentActivity(null);
        //SystemManager.setCurrentContext(getApplicationContext());
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Home Activity", "request code : " + requestCode + ", result code : " + resultCode);
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e(TAG, "Place Selected: " + place.getName());
                Log.i(TAG, "Place Selected: " + place.getAddress());
                Log.i(TAG, "Place Selected: " + place.getLocale());
                // Format the place's details and display them in the TextView.
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.ADDRESSKEY, "" + place.getName());
                editor.putString(Constants.IS_ADDRESS_SAVED, "true");
                editor.apply();
                editor.commit();
                Intent intent_result =new Intent(getApplicationContext(), MenuActivity.class);

                startActivity(intent_result);
                //Toast.makeText(getApplicationContext(), place.getAddress().toString(), Toast.LENGTH_LONG).show();
//                result_place = "" + place.getAddress();

                // Display attributions if required.
                CharSequence attributions = place.getAttributions();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {
            Log.e("Request Check Settings", "resultCode : " + resultCode);
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e("Request Check Settings", "Result is OK");
                    // All required changes were successfully made
                    if (FetchLocation.mGoogleApiClient.isConnected()) {
                        Log.e("Request Check Settings", "isconnected");
                        fetchLocationInstance.startLocationUpdates();
                    } else {
                        Log.e("Request Check Settings", "not connected");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e("Request Check Settings", "Not connected");
                    Message message = Message.obtain();
                    message.arg1 = 0;
                    messageHandler.sendMessage(message);
                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        result_gps = resultData.getString(Constants.RESULT_DATA_KEY);
        if (resultCode == Constants.SUCCESS_RESULT) {
            Log.e("HomeActivity", "Address received " + result_gps);
            Toast.makeText(SystemManager.getCurrentContext(), "Got Location. " + result_gps, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            try {
                locality_gps=getAddressForLocation(this,FetchLocation.mLastLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG,locality_gps.getLocality());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            Log.e(TAG,""+editor);
            Log.e(TAG,""+mSharedPreferences);
            editor.putString(Constants.ADDRESSKEY, "" + locality_gps.getSubLocality());
            editor.putString(Constants.IS_ADDRESS_SAVED, "true");
            editor.apply();
            editor.commit();
            Intent intent_result =new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent_result);
          //  Toast.makeText(getApplicationContext(), result_gps, Toast.LENGTH_LONG).show();
        } else if (resultCode == Constants.FAILURE_RESULT) {
            Log.e("HomeActivity", "Error : " + result_gps);
            Message message = Message.obtain();
            message.arg1 = 0;
            messageHandler.sendMessage(message);
            progressDialog.dismiss();
        }

    }
    public Address getAddressForLocation(Context context, Location location) throws IOException {

        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.e(TAG,latitude+","+longitude+","+location);
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            Log.e(TAG,""+addresses.get(0).getSubLocality());
            return addresses.get(0);
        } else {
            return null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocationInstance.startLocationUpdates();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            int state = message.arg1;
            switch (state) {
                case 1:
/*                    if(progressDialog!=null)
                        progressDialog.dismiss();*/
                    //Toast.makeText(SystemManager.getCurrentContext(),"Got Location.",Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    /*if(progressDialog!=null)
                        progressDialog.dismiss();*/

                    Alert.showInfo(SystemManager.getCurrentContext(), "No Internet!",
                            "Location could not be fetched. Please try again later.",
                            "Ok");
                    break;
            }
        }
    }


}
