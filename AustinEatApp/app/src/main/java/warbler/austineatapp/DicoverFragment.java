package warbler.austineatapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class DicoverFragment extends Fragment {

    private double lat = 0;
    private double lon = 0;
    private String url = "";
    private ListView mListView;
    private Context context;

    public DicoverFragment() {
        // Required empty public constructor
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_discover);
        context = getActivity();
        lat = LocationHelper.getLatitude();
        lon = LocationHelper.getLongitude();
        mListView = (ListView) getActivity().findViewById(R.id.discover_list_view);
        setListView();
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
                    .url(url)
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

                    Intent detailIntent = new Intent(context, DiscoverDetailActivity.class);

                    detailIntent.putExtra("OrderId", selectedOrder.id);

                    startActivity(detailIntent);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dicover, container, false);
    }

}
