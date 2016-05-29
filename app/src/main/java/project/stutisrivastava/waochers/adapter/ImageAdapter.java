package project.stutisrivastava.waochers.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import project.stutisrivastava.waochers.R;

/**
 * Created by vardan on 3/31/16.
 */
public class ImageAdapter extends BaseAdapter {
    String[] category = {"Pharma", "Services", "Clothing", "Groceries", "Order Online", "Electronics", "Parlours", "Gaming"};
    private Context mContext;
    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.pharma, R.drawable.services, R.drawable.clothing, R.drawable.groceries, R.drawable.order_online,
            R.drawable.electronics, R.drawable.parlour_icon, R.drawable.games
    };

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200 ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2,2,2,2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}
