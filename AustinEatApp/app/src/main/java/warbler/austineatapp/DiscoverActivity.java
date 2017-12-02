package warbler.austineatapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscoverActivity extends AppCompatActivity {

    private double lat = 0;
    private double lon = 0;
    private String tail = "/discover";
    private ListView mListView;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        context = this;
        lat = LocationHelper.getLatitude();
        lon = LocationHelper.getLongitude();
        mListView = (ListView) findViewById(R.id.discover_list_view);
        setListView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, CreateOrder.class);
                startActivity(intent);
            }
        });
    }

    private void setListView(){
        PullOrders pullOrders = new PullOrders();
        pullOrders.execute();
    }

    private class PullOrders extends AsyncTask<Object, Void, ArrayList<Order>>{

        @Override
        protected ArrayList<Order> doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("lat", "" + lat)
                    .add("lon", "" + lon)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + tail)
                    .post(body)
                    .build();
            ArrayList<Order> orders = null;
            try{
                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                TypeToken<ArrayList<Order>> token = new TypeToken<ArrayList<Order>>(){};
                orders = gson.fromJson(response.body().string(), token.getType());
            }catch(IOException e){
                e.printStackTrace();
            }
            return orders;
        }

        @Override
        protected void onPostExecute(ArrayList<Order> orders){
            final ArrayList<Order> finalOrders = orders;
            if(finalOrders.size() != 0) {
                DiscoverAdapter adapter = new DiscoverAdapter(context, orders);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Order selectedOrder = finalOrders.get(position);

                        Intent detailIntent = new Intent(context, DiscoverDetailActivity.class);

                        detailIntent.putExtra("orderID", selectedOrder.id);

                        startActivity(detailIntent);
                    }
                });
            }
        }
    }
}
