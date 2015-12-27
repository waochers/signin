package project.stutisrivastava.waochers.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.ui.HomeActivity;
import project.stutisrivastava.waochers.util.User;

public class LoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener{

    private String TAG = "LoginFragment";

    private SignInButton btnGoogleLogIn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private LoginButton btnFacebookLogin;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean loggingThroughFB;
    private boolean loggingThroughGoogle;
    private boolean loggingThroughNone;

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

            Log.e(TAG,"FB Login "+profile);

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.e(TAG, "Response : "+response.toString());
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
            loggingThroughFB = true;
            loggingThroughGoogle = true;
            loggingThroughNone = true;
        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    /**
     * For each method of this fragment, we will have two methods one that will handle all the FB login related
     * stuff for that method, and second will be handling Google Login.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        onCreateForFBLogin();
        onCreateForGoogleLogin();
    }

    private void onCreateForGoogleLogin() {
        Log.e(TAG, "onCreateForGoogleLogin");
    }

    private void onCreateForFBLogin() {
        Log.e(TAG,"onCreateForFBLogin");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.e(TAG,"onCurrentAccessTokenChanged");
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e(TAG,"onCurrentProfileChanged");
                handleFBSignIn(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_login, null);
    }

    /**
     * This method is used to initialize all the UI components of the activity
     **/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        onViewCreatedForFBLogin(view);
        onViewCreatedForGoogleLogin(view);
        onViewCreatedForNormalLogin(view);
        //initialize the forgot password button
    }

    private void onViewCreatedForNormalLogin(View view) {
        //initialize the text boxes and new user and sign in buttons.
    }

    private void onViewCreatedForGoogleLogin(View view) {
        Log.e(TAG,"onViewCreatedForGoogleLogin");
        btnGoogleLogIn = (SignInButton)view.findViewById(R.id.btnGoogleLogin);
        btnGoogleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogIn();
            }
        });
    }

    private void onViewCreatedForFBLogin(View view) {
        Log.e(TAG,"onViewCreatedForFBLogin");
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
        loggingThroughGoogle = false;
        loggingThroughNone = false;
        Log.e(TAG,"handleFBSignIn");
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
     * @param user stores the logged in users profile to verify if its a new user or an already signed in one.
     */
    private void registerOrSignIn(User user) {
        Log.e(TAG,"registerOrSignIn");
    }

    /**
     * This method called when user clicks on GoogleSignIn button and is then used to configure
     * GoogleSignInOptions and GoogleApiClient objects
     * which are then used to initiate the login request through the signInIntent
     */

    private void googleLogIn() {
        Log.e(TAG,"googleLogIn");
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
        Log.e(TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
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

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG,"Google Sign In : "+acct.getDisplayName()+", "+acct.getId()+acct.getEmail());
            User user = new User();
            //Initialize user information
            registerOrSignIn(user);
            loggingThroughFB = false;
            loggingThroughNone = false;
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
        Log.e(TAG,"goToHomeActivity");
        if(isLoginSuccessful) {
            Log.e(TAG, "Sign In Successful, change page");
            Intent intent = new Intent(getActivity().getBaseContext(),HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            Log.e(TAG, "Sign In UnSuccessful, no change in page");
            Toast.makeText(getActivity().getApplicationContext(), R.string.toast_unsuccessful_google_login, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        if(loggingThroughFB)
            onStopForFBLogin();
    }

    private void onStopForFBLogin() {
        Log.e(TAG, "onStopForFBLogin");
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if(loggingThroughFB)
            onResumeFBSignIn();
        /*User user = new User();
        registerOrSignIn(user);*/
        if(loggingThroughGoogle)
            onResumeGoogleSignIn();
    }

    private void onResumeGoogleSignIn() {
        Log.e(TAG, "onResumeGoogleSignIn");
    }

    private void onResumeFBSignIn() {
        Log.e(TAG, "onResumeFBSignIn");
        Profile profile = Profile.getCurrentProfile();
        handleFBSignIn(profile);
    }
}
