package project.stutisrivastava.waochers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.util.LearningToUseVolley;

public class ConfirmRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "ConfirmRegistration";
    LearningToUseVolley helper = LearningToUseVolley.getInstance();
    private Bundle parameters;
    private EditText etOTP;
    private Button btnVerifyAndRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_registration);
        parameters = getIntent().getBundleExtra("params");
        if (parameters.getString("loginType").equalsIgnoreCase("normal")) {
            initialize("normal");
        } else if (parameters.getString("loginType").equalsIgnoreCase("normal")) {
            initialize("fb");
        }

    }

    private void initialize(String loginType) {
        if (loginType.equalsIgnoreCase("normal")) {
            etOTP = (EditText) findViewById(R.id.et_otp);
            /**
             * Enter press in this edit text should automatically take to next edit text
             */
            etOTP.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == 66) {
                        verifyAndRegisterNormal();
                    }
                    return false;
                }
            });

            btnVerifyAndRegister = (Button) findViewById(R.id.btnVerify);
            btnVerifyAndRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyAndRegisterNormal();
                }
            });
        } else if (loginType.equalsIgnoreCase("fb")) {
            verifyAndRegisterFb();

        }
    }

    public void verifyAndRegisterNormal() {
        String customer_otp = parameters.getString("customer_otp");
        String otp = etOTP.getEditableText().toString();
        if (!otp.equals(customer_otp)) {
            Log.e(TAG, "Invalid otp. Try again.");
            return;
        }
        Log.e(TAG, "OTP matches!");
        final Map<String, String> params = new HashMap<>();
        params.put("customer_unique_id", parameters.getString("customer_unique_id"));
        params.put("customer_email_id", parameters.getString("customer_email_id"));
        params.put("customer_phone_number", parameters.getString("customer_phone_number"));
        params.put("customer_name", parameters.getString("customer_name"));
        params.put("customer_password", parameters.getString("customer_password"));
        Log.e(TAG, "" + params);
        String url = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/customer_registration";
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Response: " + response.toString());
                        try {
                            if (response.getString("id") != null) {
                                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intentLogin);
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
    public void verifyAndRegisterFb() {
    }

}
