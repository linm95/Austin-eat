package warbler.austineatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by linm9 on 11/25/2017.
 */

public class OrderAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Order> mDataSource;

    public OrderAdapter(Context context, ArrayList<Order> items) {
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
        View rowView = mInflater.inflate(R.layout.discover_single_row, parent, false);
        ImageView imageView = rowView.findViewById(R.id.profile_image);
        TextView name = rowView.findViewById(R.id.profile_name);
        TextView restaurant = rowView.findViewById(R.id.restaurant_name);
        //TextView food = (TextView) rowView.findViewById(R.id.food_name);
        TextView location = rowView.findViewById(R.id.location_name);
        TextView deadline = rowView.findViewById(R.id.deadline);
        RatingBar star = rowView.findViewById(R.id.ratingBar);
        TextView distance = rowView.findViewById(R.id.distance_display);
        TextView time = rowView.findViewById(R.id.time_display);

        Order order = (Order)getItem(position);
        Picasso.with(mContext).load(order.photoUrl).placeholder(R.mipmap.ic_launcher).into(imageView);
        name.setText(order.name);
        restaurant.setText("Restaurant: " + order.restaurant);
        //food.setText("Food: " + order.food);
        location.setText("Location: " + order.location);
        deadline.setText("Deadline: " + order.deadline);
        star.setRating(order.rating);
        distance.setText(String.format("%.2f", order.distance) + " miles away");
        time.setText(String.format("%.1f", order.time) + " mins ago");
        return rowView;
    }
}
