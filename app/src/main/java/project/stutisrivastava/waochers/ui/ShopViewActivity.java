package project.stutisrivastava.waochers.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import project.stutisrivastava.waochers.adapter.CustomListAdapter;
import project.stutisrivastava.waochers.model.Deal;
import project.stutisrivastava.waochers.util.LearningToUseVolley;

public class ShopViewActivity extends SampleActivityBase {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView image;
    private static String TAG = "ShopViewActivity";
    private static final String PRICE_PERCENT = "price_percentage";
    private static final String DEAL_ID = "deal_id";
    private static final String DISCOUNT_PERCENT = "discount_percentage";
    private static final String ITEM_NAME = "item_name";
    private static final String IS_ACTIVE = "is_active";
    private static final String CATEGORY_ID = "category_id";
    private static final String VALID_FOR_DAYS = "valid_for_days";
    private static final String CATEGORY_NAME = "category_name";
    ListView listDeals;
    ArrayList<Deal> resultDeal;
    CustomListAdapter mAdapter;
    LearningToUseVolley helper = new LearningToUseVolley().getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_view);
        image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("ShopViewActivity");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.MyTheme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horFeatures);
        listDeals=(ListView)findViewById(R.id.listDeals);
        getData();
        //CustomListAdapter cAdapter=new CustomListAdapter(a,)
        LinearLayout topLinearLayout = new LinearLayout(this);
        topLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout innerLinearLayout = null;
        for (int i = 0; i < 15; i++) {
            innerLinearLayout = new LinearLayout(this);
            innerLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = new ImageView(this);
            imageView.setTag(i);
            imageView.setImageResource(R.drawable.parking);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 120));
            imageView.setPadding(15, 0, 15, 5);
            TextView text = new TextView(this);
            text.setText("Parking");
            text.setLayoutParams(new ViewGroup.LayoutParams(180, 300));
            innerLinearLayout.addView(imageView);
            innerLinearLayout.addView(text);
            topLinearLayout.addView(innerLinearLayout);
        }
        scrollView.addView(topLinearLayout);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }

                }
            }
        });
        setPalette();

    }

    private void getData() {
        //Showing a progress dialog while our app fetches the data from url
        final ProgressDialog loading = new ProgressDialog(this, R.style.MyTheme);
        loading.setIndeterminate(true);
        loading.setCanceledOnTouchOutside(false);
        loading.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_icon_drawable_animation, getTheme()));
        loading.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://stutisrivastv.pythonanywhere.com/Test1/customer/api/get_deals/11/1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getDataSet(response);
                loading.dismiss();

            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });

        helper.add(request);
    }

    private void getDataSet(JSONObject response) {
       resultDeal=new ArrayList<>();
        try {
           JSONObject objResult=response.getJSONObject("result");
            Log.e(TAG, "printR" + "" + objResult);
           //JSONObject objDeals=objResult.getJSON("deal");

            JSONArray arrDeals = objResult.getJSONArray("deal_list");
            for (int i = 0; i < arrDeals.length(); i++) {
                Log.e(TAG, "print1" + "" + arrDeals.getJSONObject(i));
                JSONObject objDeals = arrDeals.getJSONObject(i);
                Deal dealsObj =new Deal();
                dealsObj.setCategory_id(objDeals.getString(PRICE_PERCENT));
                dealsObj.setDeal_id(objDeals.getString(DEAL_ID));
                dealsObj.setDiscount_percentage(objDeals.getString(DISCOUNT_PERCENT));
                dealsObj.setItem_name(objDeals.getString(ITEM_NAME));
                dealsObj.setIs_active(objDeals.getString(IS_ACTIVE));
                dealsObj.setCategory_id(objDeals.getString(CATEGORY_ID));
                dealsObj.setValid_for_days(objDeals.getString(VALID_FOR_DAYS));
                dealsObj.setCategory_name(objDeals.getString(CATEGORY_NAME));
                resultDeal.add(dealsObj);
            }
            } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter=new CustomListAdapter(this, resultDeal);
        Log.e("blaaahhhh", "" + resultDeal.get(0).getDeal_id());
        listDeals.setAdapter(mAdapter);
        listDeals.setCacheColorHint(Color.TRANSPARENT);
        listDeals.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Module Item Trigger", "Module item was triggered");
                Toast.makeText(getApplicationContext(), "hello" + position, Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(getApplicationContext(), DealActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setPalette() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.primary_dark);
                int primary = getResources().getColor(R.color.primary);
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

    }

}
