package project.stutisrivastava.waochers.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.activities.SampleActivityBase;
import project.stutisrivastava.waochers.adapter.MyRecyclerViewAdapter;
import project.stutisrivastava.waochers.model.Shops;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.LearningToUseVolley;

public class ShopListActivity extends SampleActivityBase {
    private static final String TAG_SHOP_NAME = "shop_name";
    private static final String TAG_SHOP_ADDRESS = "shop_address";
    private static final String TAG_SHOP_IMAGE = "shop_image";
    private static final String TAG_SHOP_MIN_DISCOUNT = "min_discount";
    private static String TAG = "ShopListActivity";
    LearningToUseVolley helper = new LearningToUseVolley().getInstance();
    SharedPreferences prefs;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String catName;
    private JSONObject response;
    private ArrayList<Shops> results;
    private String DATA_URL = "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_shops/1/area/";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String categoryId;
    private String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4db6ac")));
        super.onCreateDrawer();
        super.setDrawerContent();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        locality = prefs.getString(Constants.ADDRESSKEY, null);
        Intent shopIntent = getIntent();
        categoryId = shopIntent.getStringExtra("catId");
        getData(categoryId, locality);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private ArrayList<Shops> getData(String categoryId, String locality) {
        //Showing a progress dialog while our app fetches the data from url
        final ProgressDialog loading = new ProgressDialog(this, R.style.MyTheme);
        loading.setIndeterminate(true);
        loading.setCanceledOnTouchOutside(false);
        loading.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation, getTheme()));
        loading.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_shops/" + categoryId + "/area/" + locality, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "" + response);
                results = getDataSet(response);
                loading.dismiss();
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });

        helper.add(request);
        return results;
    }


    private ArrayList<Shops> getDataSet(JSONObject response) {
        results = new ArrayList<>();
        try {

            JSONArray arrResults = response.getJSONArray("results");
            for (int i = 0; i < arrResults.length(); i++) {
                Log.e(TAG, "print" + "" + arrResults.getJSONObject(i));
                JSONObject objResults = arrResults.getJSONObject(i);
                Log.e(TAG, "category:" + "" + objResults.getString(TAG_SHOP_NAME));
                Shops shopObj = new Shops();
                shopObj.setShopName(objResults.getString(TAG_SHOP_NAME));
                shopObj.setShopAddress(objResults.getString(TAG_SHOP_ADDRESS));
                shopObj.setShopImage(objResults.getString(TAG_SHOP_IMAGE));
                shopObj.setMinDiscount(objResults.getString(TAG_SHOP_MIN_DISCOUNT));
                results.add(shopObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new MyRecyclerViewAdapter(results, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);


        return results;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ShopList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://project.stutisrivastava.waochers.ui/http/host/path")
        );
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ShopList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://project.stutisrivastava.waochers.ui/http/host/path")
        );

    }

    @Override
    public void onBackPressed() {
        android.util.Log.e(TAG, "onBackPressed");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intentBack = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intentBack);
            finish();
        }
    }

}
