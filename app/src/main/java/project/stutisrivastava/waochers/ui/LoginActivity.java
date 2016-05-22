package project.stutisrivastava.waochers.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.util.SystemManager;

public class LoginActivity extends BaseActivity{

    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemManager.setCurrentActivity(this);
        SystemManager.setCurrentContext(getApplicationContext());
        setContentView(R.layout.activity_login);
        //printHashKey();
    }

    /**
     * This method prints the hashkey for the application
     */

    public void printHashKey() {
        // Add code to print out the key hash
        Log.e("haraami","aaaaaaaaaaa");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "project.stutisrivastava.waochers",
                    PackageManager.GET_SIGNATURES);
            Log.e("haraami2","aaaaaaaaaaa");

            for (Signature signature : info.signatures) {
                Log.e("haraami3","aaaaaaaaaaa");

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
               // Log.e("YourKeyHash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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
        SystemManager.setCurrentActivity(null);
        //SystemManager.setCurrentContext(getApplicationContext());
    }
}
