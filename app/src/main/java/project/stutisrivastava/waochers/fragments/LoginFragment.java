package project.stutisrivastava.waochers.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.ui.HomeActivity;
import project.stutisrivastava.waochers.util.User;

public class LoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener{


    private SignInButton btnGoogleLogIn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private LoginButton btnFacebookLogin;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    public LoginFragment(){

    }

    /**
     * Callback object used to handle FB login
     */
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();

            Log.e("FB Login",""+profile);

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();

            handleFBSignIn(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                handleFBSignIn(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);


    }

    /**
     * This method is used to initialize all the UI components of the activity
     **/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnGoogleLogIn = (SignInButton)view.findViewById(R.id.btnGoogleLogin);
        btnGoogleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogIn();
            }
        });

        btnFacebookLogin = (LoginButton)view.findViewById(R.id.btnFBLogin);

        List permissions = new ArrayList();
        permissions.add("email");
        permissions.add("user_friends");
        permissions.add("public_profile");
        btnFacebookLogin.setReadPermissions(permissions);
        btnFacebookLogin.setFragment(this);
        btnFacebookLogin.registerCallback(callbackManager, callback);


    }



    /**
     * This method is called after a successful signIn from FB. It then uses
     * @param newProfile data to populate the User object and call goToHomeActvity with isLoginSuccessful as true
     */
    private void handleFBSignIn(Profile newProfile) {
        if(newProfile==null)
            return;
        Log.e("FB Sign In", newProfile.getFirstName() + " " + newProfile.getLastName() + " " + newProfile.getId());
        User user = new User();
        //Initialize user information
        registerOrSignIn(user);
        goToHomeActivity(true);
    }

    /**
     * Method used to check whether the user is already registered or is signing up for the first time.
     * @param user
     */
    private void registerOrSignIn(User user) {

    }

    /**
     * This method called when user clicks on GoogleSignIn button and is then used to configure
     * GoogleSignInOptions and GoogleApiClient objects
     * which are then used to initiate the login request through the signInIntent
     */

    private void googleLogIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getBaseContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else{
            //for fb login
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    /**
     * This method is called after SigninIntent returns a result to onActivityResult having
     * requestcode as RC_SIGN_IN for googleSign In. It then handles the result returned by
     * @param result If signin is successful we call goToHomeActivity method with parameter isLoginSuccessful as true.
     */

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google Sign In", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("Google Sign In",acct.getDisplayName()+", "+acct.getId()+acct.getEmail());
            User user = new User();
            //Initialize user information
            registerOrSignIn(user);
            goToHomeActivity(true);
        } else {
            // Signed out, show unauthenticated UI.
            goToHomeActivity(false);
        }
    }

    /**
     * This method handles successful or unsuccessful login. If
     * @param isLoginSuccessful is true we call the activity HomeActivity else a toast is displayed informing
     *                          the user of unsuccessful login.
     */

    private void goToHomeActivity(boolean isLoginSuccessful) {
        if(isLoginSuccessful) {
            Log.e("Google Sign In", "Sign In Successful, change page");
            Intent intent = new Intent(getActivity().getBaseContext(),HomeActivity.class);
            startActivity(intent);
        }
        else {
            Log.e("Google Sign In", "Sign In UnSuccessful, no change in page");
            Toast.makeText(getActivity().getApplicationContext(), R.string.toast_unsuccessful_google_login, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        User user = new User();
        registerOrSignIn(user);
        handleFBSignIn(profile);
    }

}
