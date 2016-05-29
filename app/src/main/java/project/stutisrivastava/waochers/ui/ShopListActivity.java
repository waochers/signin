package project.stutisrivastava.waochers.ui;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.MotionEvent;

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
import project.stutisrivastava.waochers.util.LearningToUseVolley;

public class ShopListActivity extends SampleActivityBase {
    private static final String TAG_SHOP_NAME = "shop_name";
    private static final String TAG_SHOP_ADDRESS = "shop_address";
    private static final String TAG_SHOP_IMAGE = "shop_image";
    private static final String TAG_SHOP_MIN_DISCOUNT = "min_discount";
    private static String TAG = "ShopListActivity";
    LearningToUseVolley helper = new LearningToUseVolley().getInstance();
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String catName;
    private JSONObject response;
    private ArrayList<Shops> results;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4db6ac")));
//        View decorView = getWindow().getDecorView();
//// Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        super.onCreateDrawer();
        super.setDrawerContent();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        getData();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.e(TAG, " Clicked on Item ");


            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
//        mRecyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private ArrayList<Shops> getData() {
        //Showing a progress dialog while our app fetches the data from url
        final ProgressDialog loading = new ProgressDialog(this, R.style.MyTheme);
        loading.setIndeterminate(true);
        loading.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation, getTheme()));
        loading.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_shops/1/area/saket", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                Log.e(TAG, "" + response);
                results = getDataSet(response);
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

/*
@Override
protected void onResume() {
super.onResume();
((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
.MyClickListener() {

public void onItemClick(int position, View v) {
}
});
}
*/

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
        Log.e("fyj", "" + results.get(0).getShopName());

        Log.e("gf", "" + results.get(1).getShopName());

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
