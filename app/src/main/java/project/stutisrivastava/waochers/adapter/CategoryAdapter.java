package project.stutisrivastava.waochers.adapter;

/**
 * Created by stutisrivastava on 31/10/15.
 * This adapter is for the categories loaded after login in HomeActivity
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.util.CustomVolleyRequest;

public class CategoryAdapter extends BaseAdapter {
    private static final String TAG = "CategoryAdapter";
    TextView categoryName;
    NetworkImageView categoryIcon;
    //Imageloader to load images
    private ImageLoader imageLoader;
    //Context
    private Context context;
    //Array List that would contain the urls and the titles for the images
    private ArrayList<String> images;
    private ArrayList<String> names;

    private LayoutInflater inflater;

    public CategoryAdapter(Context context, ArrayList<String> images, ArrayList<String> names) {
        //Getting all the values
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Creating a linear layout

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_category_card_view, null);
        if (imageLoader == null) {
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        }
        Log.e(TAG, "" + images.get(position));
        NetworkImageView networkImageView = (NetworkImageView) convertView.findViewById(R.id.category_icon);
        imageLoader.get(images.get(position), ImageLoader.getImageListener(networkImageView, R.drawable.uniform, R.drawable.uniform));
        networkImageView.setImageUrl(images.get(position), imageLoader);
        TextView textView = (TextView) convertView.findViewById(R.id.category_name);


        textView.setText(names.get(position));
        //Adding views to the layout


        //Returnint the layout
        return convertView;
    }
}
