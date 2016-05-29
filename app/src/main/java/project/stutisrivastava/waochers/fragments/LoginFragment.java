package project.stutisrivastava.waochers.fragments;

/**
 * Created by vardan on 4/19/16.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.ui.ForgotPasswordActivity;
import project.stutisrivastava.waochers.ui.HomeActivity;
import project.stutisrivastava.waochers.ui.LoginActivity;
import project.stutisrivastava.waochers.ui.MenuActivity;
import project.stutisrivastava.waochers.ui.RegisterActivity;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.LearningToUseVolley;
import project.stutisrivastava.waochers.util.SystemManager;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RC_SIGN_IN = 9001;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    public static Context myContext;
    public static String fbUserId;
    public static String fbUserName;
    public static String fbUserEmail;
    LearningToUseVolley helper = LearningToUseVolley.getInstance();
    RegisterActivity regActivity = new RegisterActivity();
    private SharedPreferences mSharedPreferences;
    private String TAG = "LoginFragment";
    private SignInButton btnGoogleLogIn;
    private GoogleApiClient mGoogleApiClient;
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
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    /**
     * Callback object used to handle FB login
     */
    private FacebookCallback<LoginResult> callback;
    private String login1Method;
    private Boolean check = false;

    public LoginFragment() {
        Log.e(TAG, "LoginFragmentConstructor");
    }

    /**
     * For each method of this fragment, we will have two methods one that will handle all the FB login related
     * stuff for that method, and second will be handling Google Login.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        myContext = getActivity().getBaseContext();
        mSharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                Activity.MODE_PRIVATE);
        getLoginMethod();
        if (isLoggedIn) {
            goToHomeActivity(true);
        }
        onCreateForFBLogin();
        onCreateForGoogleLogin();
    }

    public void getUserInfo() {
        Log.e(TAG, "getUSerInfo()");
        Log.e("valuessss", "" + mSharedPreferences.getString(Constants.USERNAME, null));
        Log.e("valuessss", "" + mSharedPreferences.getString(Constants.USEREMAIL1, null));

        mUser = new User();
        mUser.setId(mSharedPreferences.getString(Constants.USERID, null));
        mUser.setName(mSharedPreferences.getString(Constants.USERNAME, null));
        mUser.setEmail(mSharedPreferences.getString(Constants.USEREMAIL, null));
        mUser.setPhoneNumber(mSharedPreferences.getString(Constants.USERPHONE, null));
    }

    private void onCreateForGoogleLogin() {
        Log.e(TAG, "onCreateForGoogleLogin");
        if (getActivity() instanceof LoginActivity) {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            gso = loginActivity.getGso();
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
                                handleFBSignIn(response);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
                handleFBSignIn(profile, true);
            }

            @Override
            public void onCancel() {
                setValues(false, false, false);
            }

            @Override
            public void onError(FacebookException e) {
                setValues(false, false, false);
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
                handleFBSignIn(newProfile, true);

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
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.editemailorphone);
        populateAutoComplete();

        mPasswordView = (EditText) view.findViewById(R.id.editPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mProgressView = view.findViewById(R.id.login_progress);
       // ImageView logo=(ImageView)view.findViewById(R.id.logo);
        /**
         * To inform user that only email or 10 digit phone number is to be entered.
         */
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    Toast.makeText(getContext(), R.string.text_enter_registered_email_or_phone, Toast.LENGTH_LONG).show();
            }
        });
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        mEmailView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    mPasswordView.requestFocus();
                }
                return false;
            }
        });
        /**
         * Enter press in this edit text should start sign in process.
         */
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    if (validateInformation())
                        attemptLogin();
                }
                return false;
            }
        });
        btnSignIn = (Button) view.findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInformation())
                    attemptLogin();
            }
        });
        btnForgotPassword = (Button) view.findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
        btnNewUser = (Button) view.findViewById(R.id.btnNewUser);
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String emailorphone = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        final Map<String, String> pars = new HashMap<>();

        if (emailorphone.contains("@")) {
            pars.put("customer_email_id", emailorphone);
        } else
            pars.put("customer_phone_number", emailorphone);
        pars.put("customer_password", password);
        String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/customer_login";
        final ProgressDialog loading = new ProgressDialog(getActivity(), R.style.MyTheme);
        loading.setIndeterminate(true);
        loading.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation, getActivity().getTheme()));
        loading.show();
        Log.e(TAG, "parameters=" + new JSONObject(pars));
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(pars), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Response: " + response.toString());
                        try {
                            if (!response.getString("success").equals("OK"))
                                Toast.makeText(getActivity().getApplicationContext(), response.getString("error_message"), Toast.LENGTH_LONG).show();
                            else {
                                mUser = new User();
                                mUser.setId(response.getString("customer_id"));
                                mUser.setEmail(response.getString("customer_email_id"));
                                mUser.setPhoneNumber(response.getString("customer_phone_number"));
                                mUser.setName(response.getString("customer_name"));
                                setValues(false, false, true);
                                //SystemManager.saveInSharedPref(Constants.NORMALLOGIN, mUser, mSharedPreferences);
                                SharedPreferences.Editor editorDetails = mSharedPreferences.edit();
                                editorDetails.putString(Constants.USERNAME, mUser.getName());
                                editorDetails.putString(Constants.USEREMAIL1, mUser.getEmail());
                                Log.e("finalvalues", "" + mUser.getName() + "" + mUser.getEmail());
                                editorDetails.apply();
                                loading.dismiss();
                                goToHomeActivity(true);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        //SystemManager.saveInSharedPref(Constants.NORMALLOGIN,customer,sharedPreference);
        String msg = request.getUrl() + ", " + request;
        Log.e(TAG, msg);
        helper.add(request);
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            // getLoaderManager().initLoader(0, null, LoginActivity.class);
        } else if (Build.VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (getActivity().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, "Contacts permissions are needed for providing email completions", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private boolean validateInformation() {
        Log.e(TAG, "validate Info");
        String emailOrPhone = mEmailView.getEditableText().toString();
        if (SystemManager.isValidEmailOrPhone(emailOrPhone)) {
            if (emailOrPhone.contains("@")) {
                mUserEmail = emailOrPhone;
                mUserPhoneNumber = null;
            } else {
                mUserEmail = null;
                mUserPhoneNumber = emailOrPhone;
            }
        } else
            return false;
        mPassword = mPasswordView.getEditableText().toString();
        if (mPassword.isEmpty()) {
            Toast.makeText(getContext(), R.string.text_enter_password, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void registerNewUser() {
        Log.e(TAG, "register new user");
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void forgotPassword() {
        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
        String userEmailOrPhone = mEmailView.getEditableText().toString();
        if (!userEmailOrPhone.isEmpty()) {
            if (SystemManager.isValidEmailOrPhone(userEmailOrPhone)) {
                if (userEmailOrPhone.contains("@"))
                    intent.putExtra(Constants.USEREMAIL, userEmailOrPhone);
                else
                    intent.putExtra(Constants.USERPHONE, userEmailOrPhone);
            }
        }
        startActivity(intent);
    }

    private void signIn() {
        Log.e(TAG, "signIn");
        User user = new User();
        user.setEmail(mUserEmail);
        user.setPhoneNumber(mUserPhoneNumber);

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
    private void handleFBSignIn(Profile newProfile, Boolean check) {

        this.check = check;
        Log.e(TAG, "handleFBSignIn");
        if (newProfile == null) {
            Log.e(TAG, "handleFBSignIn having profile : " + null);
            goToHomeActivity(false);
            return;
        }
        setValues(true, false, false);
        Log.e("FB Sign In", newProfile.getFirstName() + " " + newProfile.getLastName() + " " + newProfile.getId());
        mUser = new User();
        //Initialize user information
        mUser.setName(newProfile.getFirstName() + " " + newProfile.getLastName());
        mUser.setId("f" + newProfile.getId());

        fbUserId = mUser.getId();
        fbUserName = mUser.getName();
        if (check == false) {
            fbUserEmail = mSharedPreferences.getString(Constants.USEREMAIL1, null);
        } else {
            fbUserEmail = mSharedPreferences.getString(Constants.USEREMAIL, null);
        }
        mUser.setEmail(fbUserEmail);
        regActivity.notRegistered("fb");
        goToHomeActivity(true);
    }

    private void handleFBSignIn(GraphResponse response) {
        Log.e(TAG, "Handle FB Sign In graph response");
        FacebookRequestError error = response.getError();
        if (error == null) {
            JSONObject jsonObj = response.getJSONObject();
            try {
                mUser = new User();
                mUser.setId("f" + jsonObj.getString(Constants.GRAPH_ID));
                mUser.setName(jsonObj.getString(Constants.GRAPH_NAME));
                String email = jsonObj.getString(Constants.GRAPH_EMAIL);
                if (email != null) {
                    mUser.setEmail(email);

                }
                mUser.setPhoneNumber(null);
                setValues(true, false, false);
                fbUserId = mUser.getId();
                fbUserName = mUser.getName();
                fbUserEmail = mUser.getEmail();
                SharedPreferences.Editor editor1 = mSharedPreferences.edit();
                editor1.putString(Constants.USEREMAIL, fbUserEmail);
                editor1.apply();
                regActivity.notRegistered("fb");

                goToHomeActivity(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            goToHomeActivity(false);
        }
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
            Log.e("mhere", "1");
            Log.e("data:", "" + data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("reslut:", "" + result);
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

            setValues(false, true, true);
            goToHomeActivity(true);
        } else {
            // Signed out, show unauthenticated UI.
            goToHomeActivity(false);
        }
    }

    /**
     * i
     * This method handles successful or unsuccessful login. If
     *
     * @param isLoginSuccessful is true we call the activity HomeActivity else a toast is displayed informing
     *                          the user of unsuccessful login.
     */

    private void goToHomeActivity(boolean isLoginSuccessful) {
        Log.e(TAG, "goToHomeActivity");
        if (isLoginSuccessful) {
            Log.e(TAG, "Sign In Successful, change page");
            if (!isLoggedIn)
                setLoginMethod();
            Intent intent;
            if (getActivity() != null)
                intent = new Intent(getActivity(), HomeActivity.class);
            else
                getUserInfo();
            mSharedPreferences = SystemManager.getCurrentContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                    Activity.MODE_PRIVATE);
            Log.e("checker", mSharedPreferences.getString(Constants.IS_ADDRESS_SAVED, null));
            if (mSharedPreferences.getString(Constants.IS_ADDRESS_SAVED, null).equalsIgnoreCase("true")) {
                intent = new Intent(SystemManager.getCurrentContext(), MenuActivity.class);
                Toast.makeText(SystemManager.getCurrentContext(), "Address saved", Toast.LENGTH_LONG).show();
            } else {
                intent = new Intent(SystemManager.getCurrentContext(), HomeActivity.class);
                Toast.makeText(SystemManager.getCurrentContext(), "Address not saved", Toast.LENGTH_LONG).show();
            }

            Toast.makeText(SystemManager.getCurrentContext(), "Welcome " + mUser.getName() + ". Happy Discounting :)", Toast.LENGTH_LONG).show();


            if (isAdded()) {
                try {
                    startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    Log.e(TAG, "No activity attached.");
                }
            }
        } else {
            Log.e(TAG, "Sign In UnSuccessful, no change in page");
            Toast.makeText(SystemManager.getCurrentContext(), R.string.toast_unsuccessful_google_login, Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        if (loggingThroughFB)
            onStopForFBLogin();
        if (loggingThroughGoogle)
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
        // getLoginMethod();
        if (isLoggedIn)
            goToHomeActivity(true);
        if (loggingThroughFB && !loggingThroughGoogle && !loggingThroughNone)
            onResumeFBSignIn();
    }


    private void onResumeFBSignIn() {
        Log.e(TAG, "onResumeFBSignIn");
        //getUserInfo();
        Profile profile = Profile.getCurrentProfile();
        if (profile != null)
            handleFBSignIn(profile, true);
        getUserInfo();
    }


    /**
     * To identify which method we are using to login, we set the value in shared pref as Google,FB or Normal.
     */
    private void setLoginMethod() {
        Log.e(TAG, "set login method");
        String loginMethod;
        if (loggingThroughFB)
            loginMethod = Constants.FBLOGIN;
        else if (loggingThroughGoogle)
            loginMethod = Constants.GOOGLELOGIN;
        else
            loginMethod = Constants.NORMALLOGIN;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.LOGINMETHOD, loginMethod);

        editor.apply();
    }

    /**
     * Get value from shared preference and identify the method of login.
     */
    private void getLoginMethod() {
        String login1Method = mSharedPreferences.getString(Constants.LOGINMETHOD, null);
        //login1Method=""+loggingThroughGoogle;
        Log.e(TAG, "login method is " + login1Method);
        isLoggedIn = true;
        if (login1Method == null) {
            Log.e(TAG, "Not logged in");
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(Constants.IS_ADDRESS_SAVED, "false");
            editor.apply();
            editor.commit();
            isLoggedIn = false;
            setValues(true, true, true);
            return;
        } else {
            getUserInfo();
            if (login1Method.equals(Constants.FBLOGIN)) {
                setValues(true, false, false);
            } else if (login1Method.equals(Constants.GOOGLELOGIN)) {
                setValues(false, true, false);
            } else if (login1Method.equals(Constants.NORMALLOGIN)) {
                setValues(false, false, true);
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity().getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getActivity().getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }
}
