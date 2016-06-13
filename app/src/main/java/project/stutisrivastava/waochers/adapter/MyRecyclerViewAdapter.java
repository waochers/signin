package project.stutisrivastava.waochers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.model.Shops;
import project.stutisrivastava.waochers.ui.ShopViewActivity;
import project.stutisrivastava.waochers.util.CustomVolleyRequest;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ImageLoader imageLoader;
    private Context context;
    private static String TAG = "MyRecyclerViewAdapter";
    NetworkImageView categoryIcon;
    private ArrayList<Shops> myDataset;
    private Shops shop;



    public MyRecyclerViewAdapter(ArrayList<Shops> myDataset, Context context) {
        //Getting all the values
        this.context = context;
        this.myDataset = myDataset;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_card_view_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         shop = myDataset.get(position);
        if (imageLoader == null) {
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        }
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(shop.getShopImage(), ImageLoader.getImageListener(holder.imageView, R.drawable.location, R.drawable.location));

        holder.imageView.setImageUrl(shop.getShopImage(), imageLoader);
        holder.shop_name.setText(shop.getShopName());
        holder.shop_address.setText(shop.getShopAddress());
        holder.shop_discount.setText(shop.getMinDiscount());

     holder.imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent=new Intent(context, ShopViewActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.startActivity(intent);
         }
     });


    }

    @Override
    public int getItemCount() {
        return myDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView imageView;
        public TextView shop_address;
        public TextView shop_discount;
        public TextView shop_name;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            shop_name = (TextView) itemView.findViewById(R.id.shopName);
            shop_address = (TextView) itemView.findViewById(R.id.shopAddress);
            shop_discount = (TextView) itemView.findViewById(R.id.shopDiscount);
        }
    }
}