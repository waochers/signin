package project.stutisrivastava.waochers.adapter;

/**
 * Created by stutisrivastava on 31/10/15.
 * This adapter is for the categories loaded after login in HomeActivity
 */

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.ShopListActivity;
import project.stutisrivastava.waochers.util.Constants;
import project.stutisrivastava.waochers.util.SystemManager;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private static final String TAG = "CategoryAdapter";
    List<String> categories;

    public CategoryAdapter(List<String> categories){
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_card_view, viewGroup, false);
        CategoryViewHolder pvh = new CategoryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder categoryViewHolder, int i) {
        final int pos = i;
            categoryViewHolder.categoryName.setText(categories.get(i));
            categoryViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String categoryName = categories.get(pos);
                    Intent intent = new Intent(SystemManager.getCurrentContext(), ShopListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.CATEGORY, categoryName);
                    SystemManager.getCurrentContext().startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public String getItem(int pos){
        return categories.get(pos);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView categoryName;
        CategoryViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvCategory);
            categoryName = (TextView)itemView.findViewById(R.id.tv_card_category_name);
        }


    }

}
