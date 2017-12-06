package warbler.austineatapp;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private double lat = 0;
    private double lon = 0;
    private ListView mListView;
    private Context context;
    private String tail = "/discover";
    private ArrayList<Order> orders;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        lat = LocationHelper.getLatitude();
        lon = LocationHelper.getLongitude();
        mListView = getActivity().findViewById(R.id.discover_list_view);
        setListView();
        //sortList(2);
    }

    private void setListView(){
        PullOrders pullOrders = new PullOrders();
        pullOrders.execute();
        // For creating a new order
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!UserHelper.getCurrentUserProperty().equals("eater")){
                    Intent intent = new Intent(context, SelectRestaurantLocationActivity.class);
                    startActivity(intent);
                }
                else{
                    CharSequence text = "Before creating new order, please finish or cancel your ongoing order first!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                }

            }
        });
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
            if(finalOrders != null && finalOrders.size() != 0) {
                DiscoverAdapter adapter = new DiscoverAdapter(context, orders);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Order selectedOrder = finalOrders.get(position);

                        Intent detailIntent = new Intent(context, DiscoverDetailActivity.class);

                        detailIntent.putExtra("orderID", selectedOrder.id);
                        detailIntent.putExtra("deliverEmail", UserHelper.getCurrentUserEmail());

                        startActivity(detailIntent);
                    }
                });
            }
            else{
                System.out.println("DEBUG: no order is discovered");
            }
        }
    }

    private void sortList(int option){
        switch (option){
            case 0:
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return Float.compare(o2.price, o1.price);
                    }
                });
                break;
            case 1:
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return Float.compare(o1.distance, o2.distance);
                    }
                });
                break;
            case 2:
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return Float.compare(o1.time, o2.time);
                    }
                });
                break;
            default:
                break;
        }
        final ArrayList<Order> finalOrders = orders;
        if(finalOrders != null && finalOrders.size() != 0) {
            DiscoverAdapter adapter = new DiscoverAdapter(context, orders);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Order selectedOrder = finalOrders.get(position);

                    Intent detailIntent = new Intent(context, DiscoverDetailActivity.class);

                    detailIntent.putExtra("orderID", selectedOrder.id);
                    detailIntent.putExtra("deliverEmail", UserHelper.getCurrentUserEmail());

                    startActivity(detailIntent);
                }
            });
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.sort_price:
                sortList(0);
                return true;
            case R.id.sort_distance:
                sortList(1);
                return true;
            case R.id.sort_time:
                sortList(2);
                return true;
            default:
                sortList(-1);
                return true;
        }
    }
}
