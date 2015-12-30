package project.stutisrivastava.waochers.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.database.DatabaseFields;
import project.stutisrivastava.waochers.database.UserDatabase;
import project.stutisrivastava.waochers.ui.HomeActivity;
import project.stutisrivastava.waochers.ui.LoginActivity;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.util.SystemManager;

public class LoginFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
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
    private boolean isLoggedIn;
    private GoogleSignInOptions gso;
    private ProgressDialog mProgressDialog;
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private Button btnSignIn;
    private Button btnForgotPassword;
    private Button btnNewUser;
    private String mUserEmail;
    private String mUserPhoneNumber;
    private String mPassword;
    private User mUser;

    public LoginFragment() {
        Log.e(TAG, "LoginFragmentConstructor");
    }

    /**
     * Callback object used to handle FB login
     */
    private FacebookCallback<LoginResult> callback;

    /**
     * For each method of this fragment, we will have two methods one that will handle all the FB login related
     * stuff for that method, and second will be handling Google Login.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mSharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        getLoginMethod();
        if(isLoggedIn)
            goToHomeActivity(true);
        onCreateForFBLogin();
        onCreateForGoogleLogin();
    }

    private void onCreateForGoogleLogin() {
        Log.e(TAG, "onCreateForGoogleLogin");
        if(getActivity() instanceof LoginActivity){
            LoginActivity loginActivity = (LoginActivity)getActivity();
            gso=loginActivity.getGso();
            mGoogleApiClient = loginActivity.getGoogleApiClient();
        }
    }

    private void onCreateForFBLogin() {
        Log.e(TAG, "onCreateForFBLogin");

        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                Log.e(TAG, "FB Login " + profile);

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.e(TAG, "Response : " + response.toString());
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
                setValues(false,false,false);
            }

            @Override
            public void onError(FacebookException e) {
                setValues(false,false,false);
            }
        };


        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.e(TAG, "onCurrentAccessTokenChanged");
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e(TAG, "onCurrentProfileChanged");
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
        etEmailOrPhone = (EditText)view.findViewById(R.id.editEmail);
        /**
         * To inform user that only email or 10 digit phone number is to be entered.
         */
        etEmailOrPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    Toast.makeText(getContext(), R.string.text_enter_registered_email_or_phone, Toast.LENGTH_LONG).show();
            }
        });
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        etEmailOrPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 66) {
                    etPassword.requestFocus();
                }
                return false;
            }
        });
        etPassword = (EditText)view.findViewById(R.id.editPassword);
        /**
         * Enter press in this edit text should start sign in process.
         */
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()== 66) {
                    if(validateInformation())
                        signIn();
                }
                return false;
            }
        });
        btnSignIn = (Button)view.findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInformation())
                    signIn();
            }
        });
        btnForgotPassword = (Button)view.findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
        btnNewUser = (Button)view.findViewById(R.id.btnNewUser);
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private boolean validateInformation() {
        String emailOrPhone = etEmailOrPhone.getEditableText().toString();
        if(emailOrPhone.contains("@")){
            if(emailOrPhone.contains(".")){
                mUserEmail = emailOrPhone;
                mUserPhoneNumber =null;
            }else {
                Toast.makeText(getContext(), R.string.text_enter_valid_email, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        mPassword = etPassword.getEditableText().toString();
        if(mPassword.isEmpty()){
            Toast.makeText(getContext(), R.string.text_enter_password, Toast.LENGTH_LONG).show();
            return false;
        }else if(mUserEmail !=null)
                return true;

        if(emailOrPhone.matches("\"^\\d{10}$\"")){
            mUserPhoneNumber = emailOrPhone;
            mUserEmail =null;
            return true;
        }else
            Toast.makeText(getContext(), R.string.text_enter_valid_phone,Toast.LENGTH_LONG).show();
        return false;
    }

    private void registerNewUser() {

    }

    private void forgotPassword() {

    }

    private void signIn() {
        String query;
        if(mUserEmail !=null){
             query = "SELECT * FROM "+ DatabaseFields.TABLE_USER+" WHERE "+
                     DatabaseFields.KEY_CUSTOMER_EMAIL+" = '"+ mUserEmail +"'";
         }else{
             query = "SELECT * FROM "+ DatabaseFields.TABLE_USER+" WHERE "+
                     DatabaseFields.KEY_CUSTOMER_PHONE+" = "+ mUserPhoneNumber +"";
         }
        UserDatabase databaseManager = SystemManager.getDatabaseManager();
        try {
            databaseManager.openDatabase();
            Cursor result = databaseManager.executeRawQuery(query);
            if(result!=null){
                result.moveToFirst();
                String password = result.getString(result.getColumnIndex(DatabaseFields.KEY_CUSTOMER_PASSWORD));
                if(mPassword.equals(password)){
                    mUser = new User();
                    mUser.setId(result.getString(result.getColumnIndex(DatabaseFields.KEY_CUSTOMER_NO)));
                    mUser.setEmail(mUserEmail);
                    mUser.setPhoneNumber(mUserPhoneNumber);
                    mUser.setName(result.getString(result.getColumnIndex(DatabaseFields.KEY_CUSTOMER_NAME)));
                }else{
                    Toast.makeText(getContext(), R.string.text_invalid_password,Toast.LENGTH_LONG).show();
                }
            }else
                Toast.makeText(getContext(), R.string.text_not_yet_registered,Toast.LENGTH_LONG).show();
            databaseManager.closeDatabase();
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.text_sqlite_exception,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onViewCreatedForGoogleLogin(View view) {
        Log.e(TAG, "onViewCreatedForGoogleLogin");

        btnGoogleLogIn = (SignInButton) view.findViewById(R.id.btnGoogleLogin);
        btnGoogleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogIn();
            }
        });
        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.

        btnGoogleLogIn.setSize(SignInButton.SIZE_WIDE);  //cant have int. will hv to use stndrd sizes
        btnGoogleLogIn.setScopes(gso.getScopeArray());
        // [END customize_button]
    }

    private void onViewCreatedForFBLogin(View view) {
        Log.e(TAG, "onViewCreatedForFBLogin");
        btnFacebookLogin = (LoginButton) view.findViewById(R.id.btnFBLogin);
        List permissions = new ArrayList();
        permissions.add("email");
        permissions.add("user_friends");
        permissions.add("public_profile");
        btnFacebookLogin.setReadPermissions(permissions);
        btnFacebookLogin.setFragment(this);
        btnFacebookLogin.registerCallback(callbackManager, callback);
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValues(true, false, false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        onStartForGoogleLogin();
    }

    private void onStartForGoogleLogin() {
        Log.e(TAG, "onStartForGoogleLogin");
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }



    /**
     * This method is called after a successful signIn from FB. It then uses
     *
     * @param newProfile data to populate the User object and call goToHomeActvity with isLoginSuccessful as true
     */
    private void handleFBSignIn(Profile newProfile) {
        if(newProfile==null)
            goToHomeActivity(false);
        setValues(true,false,false);
        Log.e(TAG, "handleFBSignIn");
        if (newProfile == null)
            return;
        Log.e("FB Sign In", newProfile.getFirstName() + " " + newProfile.getLastName() + " " + newProfile.getId());
        mUser = new User();
        //Initialize user information
        mUser.setName(newProfile.getFirstName() + " " + newProfile.getLastName());
        mUser.setId("f" + newProfile.getId());
        mUser.setEmail(null);
        registerOrSignIn();
        goToHomeActivity(true);
    }

    /**
     * Method used to check whether the user is already registered or is signing up for the first time.
     *mUser stores the logged in users profile to verify if its a new user or an already signed in one.
     */
    private void registerOrSignIn() {
        Log.e(TAG, "registerOrSignIn");
        Log.e(TAG, "ID : " + mUser.getId());
        Log.e(TAG, "Name : " + mUser.getName());
        Log.e(TAG, "Email : " + mUser.getEmail());
        String query = "SELECT * FROM "+ DatabaseFields.TABLE_USER+" WHERE "+
                DatabaseFields.KEY_CUSTOMER_NO+" = "+ mUser.getId();
        UserDatabase databaseManager=SystemManager.getDatabaseManager();
        try {
            databaseManager.openDatabase();
            Cursor result = databaseManager.executeRawQuery(query);
            if(result!=null){
              result.moveToFirst();
              mUser.setPhoneNumber(result.getString(result.getColumnIndex(DatabaseFields.KEY_CUSTOMER_PHONE)));
              mUser.setEmail(result.getString(result.getColumnIndex(DatabaseFields.KEY_CUSTOMER_EMAIL)));
            }else{
                query = "INSERT INTO "+DatabaseFields.TABLE_USER+"("
                        +DatabaseFields.KEY_CUSTOMER_NO+","
                        +DatabaseFields.KEY_CUSTOMER_NAME+","
                        +DatabaseFields.KEY_CUSTOMER_EMAIL+","
                        +DatabaseFields.KEY_CUSTOMER_PHONE+","
                        +DatabaseFields.KEY_CUSTOMER_PASSWORD
                        +") VALUES ("
                        +mUser.getId()+","
                        +mUser.getName()+","
                        +mUser.getEmail()+","
                        +mUser.getPhoneNumber()+","
                        +"null,"
                        +")";
            }
            databaseManager.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.USERID,mUser.getId());
        editor.putString(Constants.USERNAME,mUser.getName());
        editor.putString(Constants.USEREMAIL,mUser.getEmail());
        editor.putString(Constants.USERPHONE,mUser.getPhoneNumber());
        editor.apply();
    }

    /**
     * This method called when user clicks on GoogleSignIn button and is then used to configure
     * GoogleSignInOptions and GoogleApiClient objects
     * which are then used to initiate the login request through the signInIntent
     */

    private void googleLogIn() {
        Log.e(TAG, "googleLogIn");
        setValues(false, true, false);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            //for fb login
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * This method is called after SigninIntent returns a result to onActivityResult having
     * requestcode as RC_SIGN_IN for googleSign In. It then handles the result returned by
     *
     * @param result If signin is successful we call goToHomeActivity method with parameter isLoginSuccessful as true.
     */

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "Google Sign In : " + acct.getDisplayName() + ", " + acct.getId() + acct.getEmail());
            mUser = new User();
            //Initialize user information
            mUser.setName(acct.getDisplayName());
            mUser.setId("g" + acct.getId());
            mUser.setEmail(acct.getEmail());
            registerOrSignIn();
            setValues(false, true, true);
            goToHomeActivity(true);
        } else {
            // Signed out, show unauthenticated UI.
            goToHomeActivity(false);
        }
    }

    /**
     * This method handles successful or unsuccessful login. If
     *
     * @param isLoginSuccessful is true we call the activity HomeActivity else a toast is displayed informing
     *                          the user of unsuccessful login.
     */

    private void goToHomeActivity(boolean isLoginSuccessful) {
        Log.e(TAG, "goToHomeActivity");
        if (isLoginSuccessful) {
            Log.e(TAG, "Sign In Successful, change page");
            setLoginMethod();
            Intent intent = new Intent(getActivity().getBaseContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            Log.e(TAG, "Sign In UnSuccessful, no change in page");
            Toast.makeText(getActivity().getApplicationContext(), R.string.toast_unsuccessful_google_login, Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        if (loggingThroughFB)
            onStopForFBLogin();
        if(loggingThroughGoogle)
            onStopForGoogle();
    }

    private void onStopForGoogle() {
        mGoogleApiClient.disconnect();
    }

    private void onStopForFBLogin() {
        Log.e(TAG, "onStopForFBLogin");
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        mSharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        getLoginMethod();
        if(isLoggedIn)
            goToHomeActivity(true);
        if (loggingThroughFB && !loggingThroughGoogle && !loggingThroughNone)
            onResumeFBSignIn();
    }


    private void onResumeFBSignIn() {
        Log.e(TAG, "onResumeFBSignIn");

        Profile profile = Profile.getCurrentProfile();
        if(profile!=null)
            handleFBSignIn(profile);
    }


    /**
     * To identify which method we are using to login, we set the value in shared pref as Google,FB or Normal.
     */
    private void setLoginMethod() {
        String loginMethod;
        if (loggingThroughFB)
            loginMethod = Constants.FBLOGIN;
        else if (loggingThroughGoogle)
            loginMethod = Constants.GOOGLELOGIN;
        else
            loginMethod = Constants.NORMALLOGIN;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.LOGINMETHOD, loginMethod);
        editor.putString(Constants.USERID,mUser.getId());
        editor.putString(Constants.USERNAME,mUser.getName());
        editor.putString(Constants.USEREMAIL,mUser.getEmail());
        editor.putString(Constants.USERPHONE,mUser.getPhoneNumber());
        editor.apply();
    }

    /**
     * Get value from shared preference and identify the method of login.
     */
    private void getLoginMethod(){
        String loginMethod = mSharedPreferences.getString(Constants.LOGINMETHOD, null);
        isLoggedIn = true;
        if(loginMethod==null){
            Log.e(TAG,"Not logged in");
            isLoggedIn=false;
            setValues(true, true, true);
            return;
        }
        if(loginMethod.equals(Constants.FBLOGIN)) {
            //getUserProfile
            setValues(true, false, false);
        }
        else if(loginMethod.equals(Constants.GOOGLELOGIN)) {
            //getUserProfile
            setValues(false, true, false);
        }
        else if(loginMethod.equals(Constants.NORMALLOGIN)) {
            //getUserProfile
            setValues(false, false, true);
        }

    }

    /**
     * @param fb     boolean flag for fblogin
     * @param google boolean flag for gogole login
     * @param none   boolean flag for normal login
     */
    private void setValues(boolean fb, boolean google, boolean none) {
        loggingThroughFB = fb;
        loggingThroughGoogle = google;
        loggingThroughNone = none;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}
