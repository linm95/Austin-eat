package warbler.austineatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by AhPan on 11/30/17.
 */

public class EaterOrderAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Order> mDataSource;

    public EaterOrderAdapter(Context context, ArrayList<Order> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.eater_single_row, parent, false);
        //rowView.getParent().requestDisallowInterceptTouchEvent(true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.profile_image);
        TextView deliverName = (TextView) rowView.findViewById(R.id.profile_name);
        TextView restaurant = (TextView) rowView.findViewById(R.id.restaurant_name);
        //TextView food = (TextView) rowView.findViewById(R.id.food_name);
        TextView location = (TextView) rowView.findViewById(R.id.location_name);
        TextView deadline = (TextView) rowView.findViewById(R.id.deadline);
        RatingBar star = (RatingBar) rowView.findViewById(R.id.ratingBar);
        TextView distance = (TextView) rowView.findViewById(R.id.distance_display);
        TextView time = (TextView) rowView.findViewById(R.id.time_display);
        TextView status = (TextView) rowView.findViewById(R.id.order_status);
        //Button statusBtn = (Button) rowView.findViewById(R.id.StatusBtn);
        TextView price = (TextView) rowView.findViewById(R.id.price);

        Order order = (Order)getItem(position);
        System.out.println("DEBUG: Order: " + order.restaurant);
        Picasso.with(mContext).load(order.photoUrl).placeholder(R.mipmap.ic_launcher).into(imageView);
        deliverName.setText(order.name);
        restaurant.setText("Restaurant: " + order.restaurant);
        //food.setText("Food: " + order.food);
        location.setText("Location: " + order.location);
        deadline.setText("Deadline: " + order.deadline);
        star.setRating(order.rating);
        distance.setText(order.distance + " miles away");
        time.setText(order.time + " mins ago");
        status.setText("Status: " + order.status);
        price.setText("Price: " + order.price);
        //statusBtn.setText(order.status);
        return rowView;
    }
}
