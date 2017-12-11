package warbler.austineatapp;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class EaterOrderFragment extends Fragment {


    public EaterOrderFragment() {
        // Required empty public constructor
    }


    private double lat = 0;
    private double lon = 0;
    private String tail = "/eater-order";
    private boolean confirmed = false;
    private ListView mListView;
    private Context context;
    private TextView statusText;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_eater_order, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        context = getActivity();
        lat = LocationHelper.getLatitude();
        lon = LocationHelper.getLongitude();

        mListView = getActivity().findViewById(R.id.eater_order_list_view);
        //scrollView = getActivity().findViewById(R.id.horizontalscrollView);
        confirmed = false;
        setListView();
        mySwipeRefreshLayout = getActivity().findViewById(R.id.swiperefresh);
        statusText = getActivity().findViewById(R.id.status_text);
        /*
        if(confirmed) {
            statusTextView.setText("Confirmed");
        }
        else
            statusTextView.setText("Pending");
            */
                /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("DEBUG", "onRefresh called from SwipeRefreshLayout");
                        setListView();
                        mySwipeRefreshLayout.setRefreshing(false);
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        //myUpdateOperation();
                    }
                }
        );

    }


    private void setListView(){
        EaterOrderFragment.PullOrders pullOrders = new EaterOrderFragment.PullOrders();
        pullOrders.execute();
    }


    private class PullOrders extends AsyncTask<Object, Void, ArrayList<Order>> {

        @Override
        protected ArrayList<Order> doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("lat", "" + lat)
                    .add("lon", "" + lon)
                    .add("email", UserHelper.getCurrentUserEmail())
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
            if(orders.size()==0){
                System.out.println("DEBUG: No pending or confirmed order.");
            }
            else{
                EaterOrderAdapter adapter = new EaterOrderAdapter(context, orders);

                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Order selectedOrder = finalOrders.get(position);
                        if(selectedOrder.status.equals("confirmed"))
                            confirmed = true;
                        Intent detailIntent = new Intent(context, EaterOrderDetailActivity.class);

                        detailIntent.putExtra("orderID", selectedOrder.id);
                        detailIntent.putExtra("deliverEmail", selectedOrder.deliver);
                        startActivity(detailIntent);
                    }


                });
                /*
                if(confirmed) {
                    statusText.setText("Confirmed");
                }
                else
                    statusText.setText("Pending");
                */
            }



        }
    }

}
