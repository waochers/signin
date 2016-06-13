package project.stutisrivastava.waochers.adapter;

/**
 * Created by vardan on 6/6/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.stutisrivastava.waochers.R;
import project.stutisrivastava.waochers.model.Deal;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomListAdapter extends BaseAdapter  {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
    Deal deal=null;

    /*************  CustomAdapter Constructor *****************/
    public CustomListAdapter(Activity a, ArrayList d ){

        /********** Take passed values **********/
        activity = a;
        data=d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView textValidityDate;
        public TextView textUsers;
        public TextView textDiscountPercent;
        public TextView textDiscountItem;



    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_item_deal, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textValidityDate = (TextView) vi.findViewById(R.id.textValidityDate);
            holder.textUsers=(TextView)vi.findViewById(R.id.textUsers);
            holder.textDiscountItem=(TextView)vi.findViewById(R.id.textDealDescItem);
            holder.textDiscountPercent=(TextView)vi.findViewById(R.id.textDealDescPercentage);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.textValidityDate.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            deal=null;
            deal = ( Deal ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.textValidityDate.setText( deal.getValid_for_days() );
            holder.textUsers.setText( deal.getValid_for_days());
            holder.textDiscountItem.setText(deal.getItem_name());
            holder.textDiscountPercent.setText(deal.getDiscount_percentage()+"%");


            /******** Set Item Click Listner for LayoutInflater for each row *******/

           // vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

//    @Override
//    public void onClick(View v) {
//        Log.v("CustomAdapter", "=====Row button clicked=====");
//    }
//
//    /********* Called when Item click in ListView ************/
//    private class OnItemClickListener  implements View.OnClickListener {
//        private int mPosition;
//
//        OnItemClickListener(int position){
//            mPosition = position;
//        }
//
//        @Override
//        public void onClick(View arg0) {
//
////
////            CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;
////
////            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
////
////            sct.onItemClick(mPosition);
//        }
//    }
}
