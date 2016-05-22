package project.stutisrivastava.waochers.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.activities.SampleActivityBase;
import project.stutisrivastava.waochers.fragments.LoginFragment;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.LearningToUseVolley;
import project.stutisrivastava.waochers.util.SystemManager;

public class RegisterActivity extends AppCompatActivity {

    LearningToUseVolley helper = LearningToUseVolley.getInstance();
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private CheckBox chkbocxTermsConditions;
    private Button btnRegister;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private String TAG = "RegisterActivity";
    private String uniqueKeyId;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Context mContext = this;
       // mSharedPreferences = LoginFragment.myContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
                initialize();
    }

    private void initialize() {
        etName = (EditText) findViewById(R.id.et_register_name);
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etEmail.requestFocus();
                }
                return false;
            }
        });

        etEmail = (EditText) findViewById(R.id.et_register_email);
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        etEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etPhone.requestFocus();
                }
                return false;
            }
        });

        etPhone = (EditText) findViewById(R.id.et_register_phone);
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        etPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etPassword.requestFocus();
                }
                return false;
            }
        });

        etPassword = (EditText) findViewById(R.id.et_register_password);
        /**
         * Enter press in this edit text should automatically take to next edit text
         */
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etConfirmPassword.requestFocus();
                }
                return false;
            }
        });

        etConfirmPassword = (EditText) findViewById(R.id.et_register_confirm_password);
        /**
         * Enter press in this edit text should automatically take to next edit text
         */
        etConfirmPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    chkbocxTermsConditions.requestFocus();
                }
                return false;
            }
        });
        chkbocxTermsConditions = (CheckBox) findViewById(R.id.chkboxtc);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid())
                    notRegistered("normal");
            }
        });
    }

    public boolean notRegistered(String loginType) {
        if (loginType.equalsIgnoreCase("normal")) {
            uniqueKeyId = 'n' + email.replace(".", "") + phone;
            String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_customer/" + uniqueKeyId;
            //String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/registration_api/tbl_customer.json";
            final Map<String, String> pars = new HashMap<>();
            pars.put("customer_unique_id", uniqueKeyId);
            Log.e(TAG, "parameters=" + new JSONObject(pars));
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.GET, url, new JSONObject(pars), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Response: " + response.toString());
                            try {
                                if (response.getString("success").equals("OK"))
                                    register();
                                else
                                    Toast.makeText(RegisterActivity.this, response.getString("error_message"), Toast.LENGTH_LONG).show();
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
        } else if (loginType.equalsIgnoreCase("fb")) {
            String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_customer/" + LoginFragment.fbUserId;
            //String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/registration_api/tbl_customer.json";
            final Map<String, String> pars = new HashMap<>();
            pars.put("customer_unique_id", LoginFragment.fbUserId);
            Log.e(TAG, "parameters=" + new JSONObject(pars));
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.GET, url, new JSONObject(pars), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Response: " + response.toString());
                            try {
                                if (response.getString("success").equals("OK"))
                                    registerfb();
                                else{
                                    SharedPreferences.Editor editorDetails = SampleActivityBase.mSharedPreferences.edit();
                                    editorDetails.putString(Constants.USERNAME, LoginFragment.fbUserName );
                                    editorDetails.putString(Constants.USEREMAIL, LoginFragment.fbUserEmail);
                                    Log.e("finalvalues1", "" + LoginFragment.fbUserName + "" + LoginFragment.fbUserEmail);
                                    editorDetails.apply();
                                    LoginActivity log= new LoginActivity();
                                    log.finish();
                                }
                                    //Toast.makeText(LoginFragment.myContext, response.getString("error_message"), Toast.LENGTH_LONG).show();
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

        return true;
    }


    private boolean isValid() {
        Log.e(TAG, "isValidMethod");
        userName = etName.getEditableText().toString();
        if (!SystemManager.isValidUserName(userName))
            return false;
        email = etEmail.getEditableText().toString();
        if (!SystemManager.isValidEmailOrPhone(email)) {
            return false;
        }
        phone = etPhone.getEditableText().toString();
        if (!SystemManager.isValidEmailOrPhone(phone)) {
            return false;
        }
        uniqueKeyId = 'n' + email.replace(".", "") + phone;
        password = etPassword.getEditableText().toString();
        String confirmPassword = etConfirmPassword.getEditableText().toString();
        if (!SystemManager.isValidPAssword(password, confirmPassword))
            return false;
        if (!chkbocxTermsConditions.isChecked()) {
            Toast.makeText(getApplicationContext(), R.string.text_agree_to_tnc, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void register() {
        Log.e(TAG, "register");
        User user = new User();
        user.setName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(password);


        final Bundle parameters = new Bundle();
        parameters.putString("customer_unique_id", uniqueKeyId);
        parameters.putString("customer_email_id", email);
        parameters.putString("customer_phone_number", phone);
        parameters.putString("customer_name", userName);
        parameters.putString("customer_password", password);
        parameters.putString("loginType", "normal");
        Log.e(TAG, "" + parameters);
        String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_otp/" + phone;
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Response: " + response.toString());
                        try {
                            String otp = response.getString("otp");
                            parameters.putString("customer_otp", otp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(RegisterActivity.this, ConfirmRegistrationActivity.class);
                        intent.putExtra("params", parameters);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        //SystemManager.saveInSharedPref(Constants.NORMALLOGIN,customer,sharedPreference);
        helper.add(request);
        //Toast.makeText(getBaseContext(), R.string.text_logged_in,Toast.LENGTH_LONG).show();

    }

    private void registerfb() {
        Log.e(TAG, "registerfb");
        if (LoginFragment.fbUserEmail == null) {
            Toast.makeText(getApplicationContext(), R.string.plaese_login_to_continue, Toast.LENGTH_LONG).show();
        } else {
            String fbId = LoginFragment.fbUserId;
            String fbName = LoginFragment.fbUserName;
            String fbEmail = LoginFragment.fbUserEmail;
            final Bundle parameters = new Bundle();
            parameters.putString("customer_unique_id", fbId);
            parameters.putString("customer_email_id", fbEmail);
            parameters.putString("customer_name", fbName);
            parameters.putString("loginType", "fb");

            Log.e(TAG, "" + "parameters:" + parameters);

            Log.e(TAG, "finally registering");
            final Map<String, String> params = new HashMap<>();
            params.put("customer_unique_id", parameters.getString("customer_unique_id"));
            params.put("customer_email_id", parameters.getString("customer_email_id"));

            params.put("customer_name", parameters.getString("customer_name"));

            Log.e(TAG, "" + params);
            String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/customer_registration";
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Response: " + response.toString());
                            try {
                                if (response.getString("id") != null) {
                                    SharedPreferences.Editor editorDetails = mSharedPreferences.edit();
                                    editorDetails.putString(Constants.USERNAME, parameters.getString("customer_unique_id"));
                                    editorDetails.putString(Constants.USEREMAIL, parameters.getString("customer_email_id"));
                                    editorDetails.apply();
                                    LoginFragment frag=new LoginFragment();
                                    frag.getUserInfo();
                                    Intent intentLogin = new Intent(LoginFragment.myContext, LoginActivity.class);
                                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    LoginFragment.myContext.startActivity(intentLogin);
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
            helper.add(request);
            //Toast.makeText(getBaseContext(), R.string.text_logged_in,Toast.LENGTH_LONG).show();

        }
        //Toast.makeText(getBaseContext(), R.string.text_logged_in,Toast.LENGTH_LONG).show();
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
