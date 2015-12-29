package project.stutisrivastava.waochers.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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
        setContentView(R.layout.activity_login);
        SystemManager.setCurrentActivity(this);
        SystemManager.setCurrentContext(getApplicationContext());
        //printHashKey();
    }

    /**
     * This method prints the hashkey for the application
     */

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "project.stutisrivastava.waochers",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
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
        SystemManager.setCurrentContext(null);
    }
}
