package project.stutisrivastava.waochers.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.activities.SampleActivityBase;
import project.stutisrivastava.waochers.adapter.CategoryAdapter;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.LearningToUseVolley;

public class MenuActivity extends SampleActivityBase {
    public static final String TAG_IMAGE_URL = "category_image";
    public static final String TAG_NAME = "category_name";
    private static final String DATA_URL = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_area_shops/";
    private static final String TAG_IMAGE_ID = "category_id" ;
    SharedPreferences prefs;
    GridView gridView;
    LearningToUseVolley helper = new LearningToUseVolley().getInstance();
    ArrayList<String> images;
    ArrayList<String> names;
    ArrayList<String> ids;
    //Creating GridViewAdapter Object
    CategoryAdapter gridViewAdapter = new CategoryAdapter(this, images, names);
    ArrayList<String> finImage;
    ArrayList<String> finName;
    ArrayList<String> finIds;
    private String TAG = "MenuActivity";
    private String catName;
    private String catImage;
    private String locality;
    private String catId;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4db6ac")));
        super.onCreateDrawer();
        super.setDrawerContent();
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "Raleway-Bold.ttf");

        prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        locality = prefs.getString(Constants.ADDRESSKEY, null);
        Log.e(TAG, "" + prefs);
        Log.e(TAG, "" + locality);
        //Toast.makeText(getApplicationContext(), one, Toast.LENGTH_LONG).show();
        Log.e(TAG, "activity initialized");

        gridView = (GridView) findViewById(R.id.gvMenu);
        TextView tvYou=(TextView)findViewById(R.id.tvYou);
        TextView tvArea=(TextView)findViewById(R.id.tvArea);
        tvArea.setText(locality);
        tvYou.setTypeface(custom_font);
        getData();
        // gridview.setAdapter(new CategoryAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MenuActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG,""+ids.get(position)+names.get(position));
                categoryId=ids.get(position);
                Intent intentMenu = new Intent(getApplicationContext(), ShopListActivity.class);
                intentMenu.putExtra("catId",categoryId);
                startActivity(intentMenu);
            }
        });

    }

    private void getData() {
        //Showing a progress dialog while our app fetches the data from url
       //  loading = ProgressDialog.show(this, "Please wait...", "Fetching data...", false, false);
        final ProgressDialog loading = new ProgressDialog(this, R.style.MyTheme);
        loading.setIndeterminate(true);
        loading.setCanceledOnTouchOutside(false);
        loading.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation, getTheme()));
//        loading.setMessage("Some Text");
        loading.show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, DATA_URL+locality, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();

                showGrid(response);
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });

        helper.add(request);

    }

    private void showGrid(JSONObject response) {
        //Looping through all the elements of json array
        images = new ArrayList<>();
        names = new ArrayList<>();
        finName = new ArrayList<>();
        finImage = new ArrayList<>();
        ids=new ArrayList<>();
        finIds=new ArrayList<>();
        try {

            JSONArray arrResults = response.getJSONArray("results");

            for (int i = 0; i <arrResults.length(); i++) {
                Log.e(TAG, "print" + "" + arrResults.getJSONObject(i));
                JSONObject objResults = arrResults.getJSONObject(i);
                JSONArray arrCategories = objResults.getJSONArray("categories");
                Log.e(TAG,""+arrCategories.length());
                for (int j = 0; j <arrCategories.length(); j++) {
                    JSONObject objCategories = arrCategories.getJSONObject(j);
                    Log.e(TAG, "category:" + "" + objCategories.get(TAG_NAME));
                    catName = objCategories.getString(TAG_NAME);
                    catImage = objCategories.getString(TAG_IMAGE_URL);
                    catId=objCategories.getString(TAG_IMAGE_ID);
                    if (finName.size() <= 0) {
                        Log.e(TAG, "here3");
                        images.add(catImage);
                        names.add(catName);
                        ids.add(catId);
                        finName.add(catName);
                        finImage.add(catImage);
                        finIds.add(catId);
                    } else {

                        if(finName.contains(catName)){
                            Log.e(TAG, "here1");

                            Log.e(TAG, "duplicate");
                        }else{
                            Log.e(TAG, "here2");

                            images.add(catImage);
                            names.add(catName);
                            ids.add(catId);
                            finName.add(catName);
                            finImage.add(catImage);
                            finIds.add(catId);
                        }
                    }

                }
            }
            Log.e(TAG,""+ finName.size());
            Log.e(TAG,""+ names.size());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        CategoryAdapter gridViewAdapter = new CategoryAdapter(this, images, names);
        gridView.setAdapter(gridViewAdapter);
    }

//    protected void attachBaseContext(Context context) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
//    }
@Override
public void onBackPressed() {
    android.util.Log.e(TAG, "onBackPressed");

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
        drawer.closeDrawer(GravityCompat.START);
    }
}
}

