package project.stutisrivastava.waochers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import project.stutisrivastava.waochers.util.SystemManager;


public class UserDatabase implements DatabaseFields {
	private static final String TAG = UserDatabase.class.getName();
	private Context appContext;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase sqliteDatabase;
    public void update(ContentValues values,int id) {
        int result = sqliteDatabase.update(TABLE_USER, values, KEY_CUSTOMER_NO + " = " + id, null);
        Log.e(TAG, "result = " + result);
    }

	public void update(String tableUser, ContentValues contentValues, String selection, String[] selectionArgs) {
		sqliteDatabase.update(tableUser, contentValues, selection, selectionArgs);
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_USER + " ("
                    + KEY_CUSTOMER_NO + " TEXT PRIMARY KEY, "
					+ KEY_CUSTOMER_EMAIL + " TEXT, "
					+ KEY_CUSTOMER_PHONE + " TEXT, "
                    + KEY_CUSTOMER_NAME +  " TEXT_NOT_NULL,"
					+ KEY_CUSTOMER_PASSWORD +  " TEXT" + ");");

		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
			onCreate(db);
		}
	}
	public UserDatabase(Context context) {
		appContext = context;
	}
	/*
	 * Function opens a database with writable permission.
	 */
	public UserDatabase openDatabase() throws Exception {
		if(appContext==null)
			appContext = SystemManager.getCurrentContext();
		dbHelper = new DatabaseHelper(appContext);
		sqliteDatabase = dbHelper.getWritableDatabase();
		return this;
	}
	/*
	 * Function closes the opened database.
	 */
	public void closeDatabase() throws Exception {
		dbHelper.close();
	}
	/*
	 * Use this function to execute queries that return some data. Such as
	 * SELECT.
	 */
	public Cursor executeRawQuery(String query) throws Exception {
		Log.e(TAG, query + ";");
		return sqliteDatabase.rawQuery(query, null);
	}
	/*
	 * Use this function to execute queries that do not return any data. Such as
	 * INSERT, UPDATE, DELETE etc.
	 */
	public void executeSQLQuery(String query) {
		// TODO Auto-generated method stub
		Log.e(TAG, query + ";");
        sqliteDatabase.execSQL(query + ";");
		Log.e(TAG, "saved");
	}
}