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
import android.widget.Toast;

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
    private String email;
    private String tail = "/discover-detail";
    private String pullTail = "/pull-order";
    private Context context;

    private OrderDetail order;
    private boolean pullSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("orderID");
        email = intent.getStringExtra("deliverEmail");
        context = this;
        PullOrder pullOrder = new PullOrder();
        pullOrder.execute();
        Button button = findViewById(R.id.detail_pull);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("DEBUG: userProperty is " + UserHelper.getCurrentUserProperty());
                if(UserHelper.getCurrentUserProperty().equals("eater")){
                    CharSequence text = "Before pulling an order, please finish or cancel your ongoing order first!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                }
                else{
                    UserPullOrder userPullOrder = new UserPullOrder();
                    userPullOrder.execute();
                    /*
                    CharSequence text = "Pull order successfully";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                    */

                }

            }
        });
        button = findViewById(R.id.detail_on_map);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DiscoverDetailOnMap.class);
                intent.putExtra("resLat", order.resLat);
                intent.putExtra("resLon", order.resLon);
                intent.putExtra("destLat", order.destLat);
                intent.putExtra("destLon", order.destLon);
                startActivity(intent);
            }
        });
    }

    private class UserPullOrder extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .add("deliverEmail", email)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + pullTail)
                    .post(body)
                    .build();
            try{
                client.newCall(request).execute();
                pullSuccessful = true;
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object ret) {
            System.out.println("DEBUG: return response: " + ret);
            if(ret!=null && ret.equals("HasPulled")){
                CharSequence text = "You've pulled this order";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
            }
            else if(ret!=null && ret.equals("HasConfirmed")){
                CharSequence text = "This order has been confirmed by others, please pull other orders";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
            }
            else if(pullSuccessful){
                CharSequence text = "Pull order successfully";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
                UserHelper.setCurrentUserProperty("deliver");
            }
            pullSuccessful = false;

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
            TextView createTime = findViewById(R.id.detail_creation_time);

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
            createTime.setText("Creation Time: " + order.creationTime);
        }
    }
}
