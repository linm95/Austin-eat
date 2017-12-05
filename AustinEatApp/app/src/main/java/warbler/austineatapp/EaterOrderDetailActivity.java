package warbler.austineatapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EaterOrderDetailActivity extends AppCompatActivity {

    private String id;
    private String deliverEmail;
    private Context context;
    private String tail = "/eater-order-detail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eater_order_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("orderID");
        deliverEmail = intent.getStringExtra("deliverEmail");

        context = this;

        EaterOrderDetailActivity.PullOrder pullOrder = new EaterOrderDetailActivity.PullOrder();
        pullOrder.execute();

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("DEBUG: confirmBtn is clicked");
                EaterOrderDetailActivity.ConfirmOrder confirmOrder = new EaterOrderDetailActivity.ConfirmOrder();
                confirmOrder.execute();
                finish();
                /*
                if(UserHelper.getCurrentUserProperty().equals("eater")){
                    CharSequence text = "Before pulling an order, please finish or cancel your ongoing order first!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                }
                else{
                    DiscoverDetailActivity.UserPullOrder userPullOrder = new DiscoverDetailActivity.UserPullOrder();
                    userPullOrder.execute();

                }
                */

            }
        });
    }

    private class PullOrder extends AsyncTask<Object, Void, OrderDetail> {

        @Override
        protected OrderDetail doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .add("deliverEmail", deliverEmail)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + tail)
                    .post(body)
                    .build();
            OrderDetail order = null;
            try{
                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                order = gson.fromJson(response.body().string(), OrderDetail.class);
            }catch(IOException e){
                e.printStackTrace();
            }
            return order;
        }

        @Override
        protected void onPostExecute(OrderDetail order){
            ImageView image = (ImageView) findViewById(R.id.detail_profile_image);
            TextView name = (TextView) findViewById(R.id.detail_profile_name);
            TextView location = (TextView) findViewById(R.id.detail_location);
            TextView deadline = (TextView) findViewById(R.id.detail_deadline);
            RatingBar star = (RatingBar) findViewById(R.id.detail_rating_bar);
            TextView rating = (TextView) findViewById(R.id.detail_rating);
            TextView restaurant = (TextView) findViewById(R.id.detail_restaurant);
            TextView food = (TextView) findViewById(R.id.detail_food_name);
            TextView note = (TextView) findViewById(R.id.detail_note);
            TextView status = (TextView) findViewById(R.id.order_status);

            Picasso.with(context).load(order.photoUrl).placeholder(R.mipmap.ic_launcher).into(image);
            name.setText("name: " + order.name);
            location.setText("Location: " + order.location);
            deadline.setText("Deadline: " + order.deadline);
            star.setRating(order.rating);
            rating.setText(order.rating + "/5.0");
            restaurant.setText("Restaurant: " + order.restaurant);
            food.setText("Food: " + order.food);
            note.setText("Note: " + order.note);
            status.setText("Status: " + order.status);
        }
    }
    private class ConfirmOrder extends AsyncTask<Object, Void, OrderDetail> {
        @Override
        protected OrderDetail doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .add("deliverEmail", deliverEmail)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + tail)
                    .post(body)
                    .build();
            try{
                Response response = client.newCall(request).execute();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
            /*
            OrderDetail order = null;
            try{
                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                order = gson.fromJson(response.body().string(), OrderDetail.class);
            }catch(IOException e){
                e.printStackTrace();
            }
            return order;
            */
        }
    }
}
