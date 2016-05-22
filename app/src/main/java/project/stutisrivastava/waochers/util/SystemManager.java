package project.stutisrivastava.waochers.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.database.DatabaseFields;
import project.stutisrivastava.waochers.database.UserDatabase;
import project.stutisrivastava.waochers.listeners.ConfirmationListener;
import project.stutisrivastava.waochers.model.User;

/**
 * Created by stutisrivastava on 28/12/15.
 */
public class SystemManager {

    private static final String TAG = "SysMngr";
    private static Activity currentActivity;
    private static Context context;
    private static UserDatabase databaseManager;
    private static ConfirmationListener OTPRetryConfirmationListener;

    public static Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    private static Location mCurrentLocation;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    public static void setCurrentContext(Context ctx){
        context = ctx;
    }

    public static Context getCurrentContext() {
        return context;
    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkinfo = cm.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isConnected()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static ConfirmationListener getNetworkConfirmationListener() {
        return networkConfirmationListener;
    }

    private static ConfirmationListener networkConfirmationListener = new ConfirmationListener() {
        @Override
        public void onConfirmationSet(boolean ret) {
            if(ret){
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getCurrentContext().startActivity(intent);
            }else{
                getCurrentActivity().finish();
            }
        }
    };

    public static void createDatabaseManager(Context context){
        databaseManager = new UserDatabase(context);
    }

    public static UserDatabase getDatabaseManager(){
        if(databaseManager==null)
            createDatabaseManager(getCurrentContext());
        return databaseManager;
    }

    public static boolean isValidUserName(String userName) {
        Log.e("SysMngr","username : "+userName);
        if(userName.isEmpty()){
            Toast.makeText(getCurrentContext(), R.string.blank_name_error, Toast.LENGTH_LONG).show();
            return false;
        }
        Pattern testPattern= Pattern.compile("^[a-zA-Z ]{2,30}$");
        Matcher teststring= testPattern.matcher(userName);
        if(!teststring.matches()) {
            Toast.makeText(getCurrentContext(), R.string.invalid_name_error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean isValidEmailOrPhone(String emailOrPhone) {
        if(emailOrPhone.contains("@")){
            if(!emailOrPhone.contains(".")){
                Toast.makeText(getCurrentContext(), R.string.text_enter_valid_email, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else{
            Pattern testPattern= Pattern.compile("^[7-9][0-9]{9}");
            Matcher teststring= testPattern.matcher(emailOrPhone);

            if(!teststring.matches()){
                Toast.makeText(getCurrentContext(), R.string.text_enter_valid_email_or_phone, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPAssword(String password, String confirmPassword) {
        if(password.length()<6) {
            Toast.makeText(getCurrentContext(), R.string.text_enter_valid_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if(!password.equals(confirmPassword)) {
            Toast.makeText(getCurrentContext(), R.string.text_pswd_cnfrm_pswd, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static String createRegisterQuery(User mUser) {
        StringBuilder values = new StringBuilder();
        values.append("'"+mUser.getId()+"','"
                +mUser.getName()+"',");
        if(mUser.getEmail()==null)
            values.append("null,");
        else
            values.append("'"+mUser.getEmail()+"',");
        if(mUser.getPhoneNumber()==null)
            values.append("null,");
        else
            values.append("'" +mUser.getPhoneNumber()+"',");
        if(mUser.getPassword()==null)
            values.append("null");
        else
            values.append("'" +mUser.getPassword()+"'");
        String query = "INSERT INTO "+ DatabaseFields.TABLE_USER+"("
                +DatabaseFields.KEY_CUSTOMER_NO+","
                +DatabaseFields.KEY_CUSTOMER_NAME+","
                +DatabaseFields.KEY_CUSTOMER_EMAIL+","
                +DatabaseFields.KEY_CUSTOMER_PHONE+","
                +DatabaseFields.KEY_CUSTOMER_PASSWORD
                +") VALUES ("+values +")";
        return query;
    }

    public static Cursor execRawQuery(String query) {
        Cursor result = null;
        databaseManager=getDatabaseManager();
        try {
            databaseManager.openDatabase();
            result = databaseManager.executeRawQuery(query);
            //databaseManager.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public static void execSQLQuery(String query) {
        UserDatabase databaseManager=SystemManager.getDatabaseManager();
        try {
            databaseManager.openDatabase();
            databaseManager.executeSQLQuery(query);
            databaseManager.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Cursor isRegistered(User user) {
        String query;
        Cursor result=null;
        if(user.getEmail()!=null){
            query = "SELECT * FROM "+DatabaseFields.TABLE_USER +" WHERE "
                    +DatabaseFields.KEY_CUSTOMER_EMAIL+" = '"+user.getEmail()+"'";
            result = execRawQuery(query);
            if(result==null||(result!=null&&result.getCount()==0))
                return null;
        }
        if(user.getPhoneNumber()!=null) {
            query = "SELECT * FROM " + DatabaseFields.TABLE_USER + " WHERE "
                    + DatabaseFields.KEY_CUSTOMER_PHONE + " = '" + user.getPhoneNumber()+"'";
            result = execRawQuery(query);
            if (result==null||(result != null && result.getCount() == 0))
                return null;
        }
        if(user.getId().contains("g")||user.getId().contains("f")){
            query = "SELECT * FROM "+ DatabaseFields.TABLE_USER+" WHERE "+
                    DatabaseFields.KEY_CUSTOMER_NO+" = '"+ user.getId()+"'";
            result = execRawQuery(query);
            if (result==null||(result != null && result.getCount() == 0))
                return null;
        }
        Log.e(TAG,"Result is : "+getValues(result));
        return result;
    }

    private static String getValues(Cursor result) {
        result.moveToFirst();
        String vals = result.getString(0)+", "
                +result.getString(1)+", "
                +result.getString(2)+", "
                +result.getString(3)+", "
                +result.getString(4);
        return vals;
    }

    public static void closeDB() {
        try {
            databaseManager.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveInSharedPref(String loginMethod, User user, SharedPreferences sharedPreference) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        if(loginMethod!=null)
            editor.putString(Constants.LOGINMETHOD,loginMethod);
        editor.putString(Constants.USERID, user.getId());
        editor.putString(Constants.USERNAME, user.getName());
        editor.putString(Constants.USEREMAIL, user.getEmail());
        editor.putString(Constants.USERPHONE, user.getPhoneNumber());
        editor.putString(Constants.USERPASSWORD, user.getPassword());
        editor.apply();
    }

    public static boolean updateDB(String tableUser, ContentValues contentValues, String selection, String[] selectionArgs) {

        try {
            databaseManager.openDatabase();
            databaseManager.update(DatabaseFields.TABLE_USER, contentValues, selection,
                    selectionArgs);
            databaseManager.closeDatabase();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void confirmationToTurnGPSOn() {
        ConfirmationListener gpsListener = new ConfirmationListener() {
            @Override
            public void onConfirmationSet(boolean ret) {
                if(ret) {
                    turnGPSOn();
                }else {
                    currentActivity.finish();
                }
            }
        };
        Alert.showConfirmationDialog(currentActivity, gpsListener, "Location Disabled", "Click OK to enable Location Services");
    }

    private static void turnGPSOn() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        Context ctx = getCurrentContext();
        ctx.sendBroadcast(intent);

        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static void setLocation(Location loc){
        double currLatitude = loc.getLatitude();
        double currLongitude =loc.getLongitude();
        mCurrentLocation = loc;
        Log.e(TAG, "lat " + currLatitude);
        Log.e(TAG, "long " + currLongitude);
//        Toast.makeText(context,"Lat : "+currLatitude+" Long : "+currLongitude,Toast.LENGTH_LONG).show();
    }

}
