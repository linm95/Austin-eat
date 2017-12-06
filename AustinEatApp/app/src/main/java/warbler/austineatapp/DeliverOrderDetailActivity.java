package warbler.austineatapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DeliverOrderDetailActivity extends AppCompatActivity {


    public static class ResultDialogFragment extends DialogFragment {
        public String message;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                            //NavUtils.navigateUpTo(getActivity(), upIntent);
                            getActivity().finish();
                        }
                    });
            return builder.create();
        }
    }


    private String id;
    private Context context;
    private String eaterID;

    // Send bird
    final String appId = "D4B1661C-35A0-49B1-9D04-BD721ED6DD74";

    // Each tail represents different handler
    private String tail = "/deliver-order-detail";
    private String cancelTail = "/deliver-cancel-order";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_order_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("orderID");
        context = this;

        DeliverOrderDetailActivity.PullOrders pullOrder = new DeliverOrderDetailActivity.PullOrders();
        pullOrder.execute();

        // The deliver cancels this order
        /*
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("DEBUG: cancelBtn is clicked");
                DeliverOrderDetailActivity.CancelOrder cancelOrder = new DeliverOrderDetailActivity.CancelOrder();
                cancelOrder.execute();
                //Intent upIntent = NavUtils.getParentActivityIntent((Activity) context);
                //NavUtils.navigateUpTo((Activity)context, upIntent);
                returnToParent();
            }

        });
        */

    }



    public void cancelBtnClick(View view){
        System.out.println("DEBUG: cancelBtn is clicked");
        DeliverOrderDetailActivity.CancelOrder cancelOrder = new DeliverOrderDetailActivity.CancelOrder();
        cancelOrder.execute();
        //finish();
        //Intent upIntent = NavUtils.getParentActivityIntent(this);
        //NavUtils.navigateUpTo(this, upIntent);
    }

    public void completeBtnClick(View view){
        System.out.println("DEBUG: completeBtn is clicked");
        Intent intent = new Intent(this, ScanQrcodeActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private class PullOrders extends AsyncTask<Object, Void, OrderDetail> {

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
            eaterID = order.email;
        }
    }
    // Cancel Order Function
    private class CancelOrder extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
                    .add("deliverEmail", UserHelper.getCurrentUserEmail())
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + cancelTail)
                    .post(body)
                    .build();
            try{
                Response response = client.newCall(request).execute();
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Boolean successful) {
            String message = "Cancel this order sucessfully.";
            DeliverOrderDetailActivity.ResultDialogFragment dialogFragment = new DeliverOrderDetailActivity.ResultDialogFragment();
            dialogFragment.message = message;
            dialogFragment.show(getSupportFragmentManager(), "result");
        }
    }

    public void startMessaging(View view) {
        String[] targetIDs = {eaterID};
        Intent intent = new Intent(this, SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingStartArgs(appId,
                UserHelper.getCurrentUserEmail(), UserHelper.getFirstName(), targetIDs);
        intent.putExtras(args);

        startActivity(intent);
    }
}
