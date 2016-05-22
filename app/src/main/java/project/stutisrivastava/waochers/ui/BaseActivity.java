package project.stutisrivastava.waochers.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.util.Alert;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.SystemManager;

/**
 * Created by stutisrivastava on 27/12/15.
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "BaseActivity";

    GoogleSignInOptions gso;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;
    private String loginMethod;
    private User mUser;

    private boolean isSigningOut;
    private TextView tvHeaderUserName;
    private TextView tvHeaderEmailOrPhone;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void onCreateDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        tvHeaderUserName = (TextView) header.findViewById(R.id.username);
        //
        // tvHeaderEmailOrPhone = (TextView) header.findViewById(R.id.email);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setDrawerContent(){
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);

        mUser = new User();
        mUser.setId(mSharedPreferences.getString(Constants.USERID, null));
        mUser.setName(mSharedPreferences.getString(Constants.USERNAME, null));
        mUser.setEmail(mSharedPreferences.getString(Constants.USEREMAIL, null));
        mUser.setPhoneNumber(mSharedPreferences.getString(Constants.USERPHONE, null));

        if(mUser.getId()!=null){
            tvHeaderUserName.setText(mUser.getName());
            if(mUser.getPhoneNumber()!=null)
                tvHeaderEmailOrPhone.setText(mUser.getPhoneNumber());
            else if(mUser.getEmail()!=null)
                tvHeaderEmailOrPhone.setText(mUser.getEmail());
            else
                tvHeaderEmailOrPhone.setText(null);
        }
    }

    public GoogleSignInOptions getGso() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();
        // [END configure_signin]
        return gso;
    }

    public GoogleApiClient getGoogleApiClient(){
            getGso();
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
        mGoogleApiClient.connect();
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "onConnected");
        if (SystemManager.getCurrentActivity() instanceof LoginActivity)
            return;
        if(isSigningOut)
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        hideProgressDialog();
                        goToLoginActivity();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        if (SystemManager.getCurrentActivity() instanceof LoginActivity)
            return;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.e(TAG, "onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_get_coupons) {
            // Handle the camera action
        } else if (id == R.id.nav_my_coupons) {

        } else if (id == R.id.nav_my_shops) {

        } else if (id == R.id.nav_report_issue) {

        } else if (id == R.id.nav_about_us) {

        }else if(id==R.id.nav_sign_out){
            initiateSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initiateSignOut() {
        Log.e(TAG, "initiateSignOut");
        isSigningOut = true;
        if(loginMethod.equals(Constants.FBLOGIN)) {
            //getUserProfile
            signOutFromFB();
        }
        else if(loginMethod.equals(Constants.GOOGLELOGIN)) {
            //getUserProfile
            signOutFromGoogle();
        }
        else if(loginMethod.equals(Constants.NORMALLOGIN)) {
            //getUserProfile
            signOutFromNormalLogin();
        }
    }

    private void signOutFromGoogle() {
        Log.e(TAG, "signOutFromGoogle");
        mGoogleApiClient = getGoogleApiClient();
        if(mGoogleApiClient.isConnected()){
            Log.e(TAG, "Connected");
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            hideProgressDialog();
                            goToLoginActivity();
                        }
                    });
        }
        else Log.e(TAG, "Not Connected");
        showProgressDialog(getString(R.string.signing_out));
    }

    private void signOutFromNormalLogin() {
        Log.e(TAG, "signOutFromNormalLogin");
        goToLoginActivity();
    }

    private void signOutFromFB() {
        Log.e(TAG, "signOutFromFB");
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        goToLoginActivity();
    }


    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        if(!SystemManager.isNetworkConnected()){
            Alert.showConfirmationDialog(this,SystemManager.getNetworkConfirmationListener(),getString(R.string.title_no_internet),getString(R.string.no_internet_message));
        }
        if (SystemManager.getCurrentActivity() instanceof LoginActivity)
            return;

        if(mSharedPreferences==null)
            mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                    Activity.MODE_PRIVATE);
        loginMethod = mSharedPreferences.getString(Constants.LOGINMETHOD, null);
        Log.e(TAG, "onResume login method is " + loginMethod);
        if(loginMethod==null){
            goToLoginActivity();
            finish();
        }

    }

    private void goToLoginActivity() {
        Log.e(TAG, "goToLoginActivity");
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.LOGINMETHOD, null);
        editor.apply();
        Toast.makeText(getApplicationContext(), getString(R.string.text_log_in_again), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}
