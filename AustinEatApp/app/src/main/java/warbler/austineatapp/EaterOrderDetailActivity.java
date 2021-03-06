package warbler.austineatapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
    private String deliverEmail;
    private Context context;
    private String tail = "/eater-order-detail";
    private String confirmTail = "/eater-confirm-order";
    private String completeTail = "/eater-complete-order";
    private String cancelTail = "/eater-cancel-order";

    // Send bird
    final String appId = "D4B1661C-35A0-49B1-9D04-BD721ED6DD74";

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
        /*
        Button completeBtn = findViewById(R.id.completeBtn);
        completeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("DEBUG: completeBtn is clicked");

                EaterOrderDetailActivity.CompleteOrder completeOrder = new EaterOrderDetailActivity.CompleteOrder();
                completeOrder.execute();

            }
        });
        */

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("DEBUG: confirmBtn is clicked");
                EaterOrderDetailActivity.ConfirmOrder confirmOrder = new EaterOrderDetailActivity.ConfirmOrder();
                confirmOrder.execute();
            }
        });
    }

    public void startMessaging(View view) {
        String[] targetIDs = {deliverEmail};
        Intent intent = new Intent(this, SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingStartArgs(appId,
                UserHelper.getCurrentUserEmail(), UserHelper.getFirstName(), targetIDs);
        intent.putExtras(args);

        //startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
        startActivity(intent);
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
                    .add("orderID",id)
                    .add("deliverEmail", deliverEmail)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + confirmTail)
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
        protected void onPostExecute(OrderDetail orderDetail) {
            String message = "Confirm this order successfully!";
            EaterOrderDetailActivity.ResultDialogFragment dialogFragment = new EaterOrderDetailActivity.ResultDialogFragment();
            dialogFragment.message = message;
            dialogFragment.show(getSupportFragmentManager(), "result");
        }

    }
    private class CompleteOrder extends AsyncTask<Object, Void, OrderDetail> {
        @Override
        protected OrderDetail doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("orderID",id)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + completeTail)
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
        protected void onPostExecute(OrderDetail orderDetail) {
            String message = "Complete this order successfully!";
            EaterOrderDetailActivity.ResultDialogFragment dialogFragment = new EaterOrderDetailActivity.ResultDialogFragment();
            dialogFragment.message = message;
            dialogFragment.show(getSupportFragmentManager(), "result");
        }

    }


    public void cancelBtnClick(View view){
        System.out.println("DEBUG: In EaterOrderDetail: cancelBtn is clicked");
        EaterOrderDetailActivity.CancelOrder cancelOrder = new EaterOrderDetailActivity.CancelOrder();
        cancelOrder.execute();
        //finish();
        //Intent upIntent = NavUtils.getParentActivityIntent(this);
        //NavUtils.navigateUpTo(this, upIntent);
    }

    // Cancel Order Function
    private class CancelOrder extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id",id)
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
            String message = "Eater cancels this order sucessfully.";
            EaterOrderDetailActivity.ResultDialogFragment dialogFragment = new EaterOrderDetailActivity.ResultDialogFragment();
            dialogFragment.message = message;
            dialogFragment.show(getSupportFragmentManager(), "result");
        }
    }

    public void codeBtnClick(View view){
        System.out.println("DEBUG: codeBtn is clicked");
        Intent intent = new Intent(this, GenerateQrcodeActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        intent.putExtra("orderID", id);
        startActivity(intent);
    }
}
