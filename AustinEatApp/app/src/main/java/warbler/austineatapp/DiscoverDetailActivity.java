package warbler.austineatapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscoverDetailActivity extends AppCompatActivity {

    private String id;
    private String tail = "/discover-detail";
    private String pullTail = "/pull-order";
    private Context context;
    private OrderDetail order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("orderID");
        context = this;
        PullOrder pullOrder = new PullOrder();
        pullOrder.execute();
        Button button = findViewById(R.id.detail_pull);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                UserPullOrder userPullOrder = new UserPullOrder();
                userPullOrder.execute();
            }
        });
    }

    private class UserPullOrder extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + pullTail)
                    .post(body)
                    .build();
            try{
                client.newCall(request).execute();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
    private class PullOrder extends AsyncTask<Object, Void, OrderDetail> {

        @Override
        protected OrderDetail doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + tail)
                    .post(body)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                //Log.d("Response", response.body().string());
                Gson gson = new Gson();
                order = gson.fromJson(response.body().string(), OrderDetail.class);
            }catch(IOException e){
                e.printStackTrace();
            }
            return order;
        }

        @Override
        protected void onPostExecute(OrderDetail order){
            ImageView image = findViewById(R.id.detail_profile_image);
            TextView name = findViewById(R.id.detail_profile_name);
            TextView location = findViewById(R.id.detail_location);
            TextView deadline = findViewById(R.id.detail_deadline);
            RatingBar star = findViewById(R.id.detail_rating_bar);
            TextView rating = findViewById(R.id.detail_rating);
            TextView restaurant = findViewById(R.id.detail_restaurant);
            TextView food = findViewById(R.id.detail_food_name);
            TextView note = findViewById(R.id.detail_note);
            TextView price = findViewById(R.id.detail_price);

            Picasso.with(context).load(order.photoUrl).placeholder(R.mipmap.ic_launcher).into(image);
            name.setText("Name: " + order.name);
            location.setText("Destination: " + order.location);
            deadline.setText("Deadline: " + order.deadline);
            star.setRating(order.rating);
            rating.setText(order.rating + "/5.0");
            restaurant.setText("Restaurant: " + order.restaurant);
            food.setText("Food: " + order.food);
            note.setText("Note: " + order.note);
            price.setText("Money you get: " + order.price);
        }
    }
}
