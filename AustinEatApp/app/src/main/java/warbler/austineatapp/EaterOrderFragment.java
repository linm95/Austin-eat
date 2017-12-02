package warbler.austineatapp;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class EaterOrderFragment extends Fragment {


    public EaterOrderFragment() {
        // Required empty public constructor
    }


    private float lat = 0;
    private float lon = 0;
    private String tail = "/eater-order";
    private boolean confirmed = false;
    private ListView mListView;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_deliver_order, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        mListView = getActivity().findViewById(R.id.eater_order_list_view);
        setListView();
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
            DiscoverAdapter adapter = new DiscoverAdapter(context, orders);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Order selectedOrder = finalOrders.get(position);

                    Intent detailIntent = new Intent(context, EaterOrderDetailActivity.class);

                    detailIntent.putExtra("OrderId", selectedOrder.id);

                    startActivity(detailIntent);
                }
            });
        }
    }

}
