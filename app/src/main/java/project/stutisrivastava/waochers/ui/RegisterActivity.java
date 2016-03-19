package project.stutisrivastava.waochers.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.SystemManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private CheckBox chkbocxTermsConditions;
    private Button btnRegister;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        etName = (EditText)findViewById(R.id.et_register_name);
        /**
         * Enter press in this edit text should automatically take to next edit text - etPassword.
         */
        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 66) {
                    etEmailOrPhone.requestFocus();
                }
                return false;
            }
        });

        etEmailOrPhone = (EditText)findViewById(R.id.et_register_email_or_phone);
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

        etPassword = (EditText)findViewById(R.id.et_register_password);
        /**
         * Enter press in this edit text should automatically take to next edit text
         */
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 66) {
                    etConfirmPassword.requestFocus();
                }
                return false;
            }
        });

        etConfirmPassword = (EditText)findViewById(R.id.et_register_confirm_password);
        /**
         * Enter press in this edit text should automatically take to next edit text
         */
        etEmailOrPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 66) {
                    chkbocxTermsConditions.requestFocus();
                }
                return false;
            }
        });
        chkbocxTermsConditions = (CheckBox)findViewById(R.id.chkboxtc);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid())
                    register();
            }
        });
    }

    private boolean isValid() {
        Log.e(TAG, "isValidMethod");
        userName = etName.getEditableText().toString();
        if(!SystemManager.isValidUserName(userName))
            return false;
        String emailOrPhone = etEmailOrPhone.getEditableText().toString();
        if(SystemManager.isValidEmailOrPhone(emailOrPhone)){
            if(emailOrPhone.contains("@"))
                email = emailOrPhone;
            else
                phone = emailOrPhone;
        }
        else
            return false;
        password = etPassword.getEditableText().toString();
        String confirmPassword = etConfirmPassword.getEditableText().toString();
        if(!SystemManager.isValidPAssword(password,confirmPassword))
            return false;
        if(!chkbocxTermsConditions.isChecked()){
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
        SharedPreferences sharedPreference=getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Cursor result = SystemManager.isRegistered(user);
        if(result!=null){
            if(email!=null)
                Toast.makeText(getBaseContext(), R.string.text_email_exists,Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getBaseContext(), R.string.text_mobile_number_exists,Toast.LENGTH_LONG).show();
        }else{
            String query = SystemManager.createRegisterQuery(user);
            SystemManager.execSQLQuery(query);
            SystemManager.saveInSharedPref(Constants.NORMALLOGIN,user,sharedPreference);
            Toast.makeText(getBaseContext(), R.string.text_logged_in,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            finish();
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
