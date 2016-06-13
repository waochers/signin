package project.stutisrivastava.waochers.util;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.receivers.AddressResultReceiver;
import project.stutisrivastava.waochers.receivers.Receiver;
import project.stutisrivastava.waochers.services.FetchAddressIntentService;


/**
 * Created by stuti on 27/4/15.
 */
public class FetchLocation implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static FetchLocation locationInstance;

    private static AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private boolean mAddressRequested;

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 8000;
    private static final String TAG = FetchLocation.class.getName();
    public static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location mLastLocation;
    private static Messenger messageHandler;

    public synchronized void buildGoogleApiClient() {
        Log.e(TAG, "in buildAPi client");
        mGoogleApiClient = new GoogleApiClient.Builder(SystemManager.getCurrentContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.e(TAG, "build");

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5 * 60 * 1000)        // 10 minutes, in milliseconds
                .setFastestInterval(1 * 60 * 1000);
        Log.e(TAG, "loc Request");

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.e(TAG, "Connected location" + mLastLocation);

        getLocationPermissions();

        // Check the location settings of the user and create the callback to react to the different possibilities
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
        result.setResultCallback(mResultCallbackFromSettings);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(SystemManager.getCurrentContext(), R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                Log.e("Geocoder","Not present");
                return;
            }

            if (mAddressRequested) {
                startIntentService();
            }
        }

    }


    private void getLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(SystemManager.getCurrentContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SystemManager.getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(SystemManager.getCurrentContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SystemManager.getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "on Location Changed " + mLastLocation);
        mLastLocation = location;
        SystemManager.setLocation(mLastLocation);
        if(mAddressRequested)
            startIntentService();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(SystemManager.getCurrentActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            sendMessage(0);

        }
    }



    public static FetchLocation getInstance(Messenger messenger, Receiver receiver) {
        mResultReceiver.setReceiver(receiver);
        messageHandler = messenger;

        if (locationInstance == null)
            locationInstance = new FetchLocation();
        return locationInstance;

    }


    // The callback for the management of the user settings regarding location
    private ResultCallback<LocationSettingsResult> mResultCallbackFromSettings = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            Log.e("onResult()", "" + status);
            //final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        Log.e("Resolution Required", "resolutn");
                        status.startResolutionForResult(SystemManager.getCurrentActivity(),
                                Constants.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                        Log.e("Resolution Required", "SendIntentException");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                    break;
            }
        }
    };

    public void startLocationUpdates() {
        Log.e("startLocationUpdates", "in method");

        Log.e("startLocationUpdates", "permission present");
        if (ActivityCompat.checkSelfPermission(SystemManager.getCurrentContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SystemManager.getCurrentContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermissions();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.e(TAG, "location " + mLastLocation);
        if (mLastLocation == null) {
            Log.e(TAG, "location is null");
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            else
                mGoogleApiClient.connect();
        } else {
            SystemManager.setLocation(mLastLocation);
        }
        if(mLastLocation!=null)
            sendMessage(1);
    }

    public void sendMessage(int locationReceived) {
        Log.e(TAG,"send msg");
        Message message = Message.obtain();
        message.arg1 = locationReceived;
        try {
            if(messageHandler!=null)
                messageHandler.send(message);
            if(locationReceived==1)
                getAddress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "msg send");
    }

    public void getAddress() {
        Log.e("getAddress","in getAddressMethod");
        mLastLocation = SystemManager.getmCurrentLocation();

        if (FetchLocation.mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    private void updateUIWidgets() {
        Log.e("Address : ", "Loading..");
    }

    protected void startIntentService() {
        Log.e("startIntentSrvice","AdressIntentStarted");
        Intent intent = new Intent(SystemManager.getCurrentContext(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        mAddressRequested = false;
        SystemManager.getCurrentActivity().startService(intent);
    }

}
