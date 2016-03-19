package project.stutisrivastava.waochers.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.database.DatabaseFields;
import project.stutisrivastava.waochers.database.UserDatabase;
import project.stutisrivastava.waochers.model.User;
import project.stutisrivastava.waochers.util.Alert;
import project.stutisrivastava.waochers.util.SystemManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailOrPhone;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnGenerateOTPResetPswd;
    private boolean otpGenerated = false;
    private String mEmailOrPhone;
    private int mOTP;
    private LinearLayout llOTP;
    private EditText etOTP;
    private Button btnResendOTP;
    private static int numberOfOTPTries;
    private static int numberOfResetTries;
    private String mPassword;
    private String mConfirmPassword;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemManager.setCurrentActivity(this);
        SystemManager.setCurrentContext(getApplicationContext());
        setContentView(R.layout.activity_forgot_password);
        initialize();
        getNumberOfTries();
    }

    private void getNumberOfTries() {
        //get from server. Reinitialize after few hours. right now it will reinitialize everytime app is started.
    }

    private void initialize() {
        etEmailOrPhone = (EditText)findViewById(R.id.et_forgot_password_email_or_phone);
        etEmailOrPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    Toast.makeText(ForgotPasswordActivity.this, R.string.text_enter_registered_email_or_phone, Toast.LENGTH_LONG).show();
            }
        });
        etEmailOrPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etNewPassword.requestFocus();
                }
                return false;
            }
        });
        etNewPassword = (EditText)findViewById(R.id.et_forgot_password_new_password);
        etNewPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    etConfirmPassword.requestFocus();
                }
                return false;
            }
        });
        etConfirmPassword = (EditText)findViewById(R.id.et_forgot_password_confirm_password);
        etConfirmPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    btnGenerateOTPResetPswd.requestFocus();
                }
                return false;
            }
        });
        btnGenerateOTPResetPswd = (Button)findViewById(R.id.btnResetPassword);
        btnGenerateOTPResetPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndGenerateOTP();
            }
        });
        llOTP = (LinearLayout)findViewById(R.id.ll_otp);
        etOTP = (EditText)findViewById(R.id.et_forgot_password_otp);
        etOTP.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 66) {
                    btnGenerateOTPResetPswd.requestFocus();
                }
                return false;
            }
        });
        btnResendOTP = (Button)findViewById(R.id.btnResendOTP);
        btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberOfOTPTries<3)
                    generateOTP();
                else {
                    Alert.showInfo(getApplicationContext(), getString(R.string.title_exceeded_number_of_tries), getString(R.string.msg_exceeded_number_of_tries), getString(R.string.text_btn_ok));
                }
            }
        });
    }

    private void validateAndGenerateOTP() {
        if (!otpGenerated) {
            if (userExists()) {
                mPassword = etNewPassword.getEditableText().toString();
                mConfirmPassword=etNewPassword.getEditableText().toString();
                if(SystemManager.isValidPAssword(mPassword,mConfirmPassword)) {
                    etEmailOrPhone.setEnabled(false);
                    etNewPassword.setEnabled(false);
                    etConfirmPassword.setEnabled(false);
                    mUser.setPassword(mPassword);
                    btnGenerateOTPResetPswd.setText(R.string.text_btn_change_password);
                    generateOTP();
                    llOTP.setVisibility(View.VISIBLE);
                }
            } else{
                Toast.makeText(ForgotPasswordActivity.this,getString(R.string.text_not_yet_registered),Toast.LENGTH_LONG).show();
            }
        }else{
            if(numberOfResetTries<3)
                resetPassword();
            else
                Alert.showInfo(getApplicationContext(), getString(R.string.title_exceeded_number_of_tries), getString(R.string.msg_exceeded_number_of_tries), getString(R.string.text_btn_ok));
        }
    }

    private void resetPassword() {
        try {
            int otpEntered = Integer.parseInt(etOTP.getEditableText().toString());
           // numberOfResetTries++;
            if(otpEntered!=mOTP)
                Toast.makeText(this,R.string.msg_invalid_otp,Toast.LENGTH_LONG).show();
            else{
                updateDB();
            }
        }catch (NumberFormatException e){
            Toast.makeText(this,R.string.msg_invalid_otp,Toast.LENGTH_LONG).show();
        }
    }

    private void updateDB() {
        ContentValues contentValues = new ContentValues();
        String[] selectionArgs =new String[1];
        String selection;
        if(mUser.getEmail()!=null) {
            selection = DatabaseFields.KEY_CUSTOMER_EMAIL + " = ?"; // where ID column = rowId (that is, selectionArgs)
            selectionArgs[0] =mUser.getEmail();
        }
        else{
            selection = DatabaseFields.KEY_CUSTOMER_PHONE + " = ?";
            selectionArgs[0] =mUser.getPhoneNumber();
        }
        contentValues.put(DatabaseFields.KEY_CUSTOMER_PASSWORD, mUser.getPassword());

        boolean isUpdateSuccessful = SystemManager.updateDB(DatabaseFields.TABLE_USER, contentValues, selection,
                selectionArgs);

        if(isUpdateSuccessful){
            Toast.makeText(this,getString(R.string.msg_password_reset_successful),Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(this,getString(R.string.msg_password_reset_unsuccessful),Toast.LENGTH_LONG).show();
        finish();
    }

    private void generateOTP() {
       // numberOfOTPTries++;
        otpGenerated=true;
        mOTP =  (int) Math.floor(Math.random() * (9999 - 1000 + 1)) + 1000;
        Toast.makeText(ForgotPasswordActivity.this, "Your OTP is : "+mOTP, Toast.LENGTH_LONG).show();
    }

    private boolean userExists(){
        mEmailOrPhone = etEmailOrPhone.getEditableText().toString();
        if(!SystemManager.isValidEmailOrPhone(mEmailOrPhone))
            return false;
        mUser = new User();
        if(mEmailOrPhone.contains("@")){
            mUser.setPhoneNumber(null);
            mUser.setEmail(mEmailOrPhone);
        }else{
            mUser.setPhoneNumber(mEmailOrPhone);
            mUser.setEmail(null);
        }
        Cursor result = SystemManager.isRegistered(mUser);
        if(result==null)
            return false;
        return true;
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
