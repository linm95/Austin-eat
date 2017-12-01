package warbler.austineatapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrder extends AppCompatActivity {

    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
    }

    private class PushOrder extends AsyncTask<ArrayList<String>, Void, Integer>{


        @Override
        protected Integer doInBackground(ArrayList<String>... params) {
            ArrayList<String> info = params[0];
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("restaurant", info.get(0))
                    .add("food", info.get(1))
                    .add("location", info.get(2))
                    .add("deadline", info.get(3))
                    .add("note", info.get(4))
                    .add("email", UserHelper.getCurrentUserEmail())
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            ArrayList<Order> orders = null;
            try{
                Response response = client.newCall(request).execute();
            }catch(IOException e){
                e.printStackTrace();
            }
            return 0;
        }
    }

    private ArrayList<String> getInfo(){
        ArrayList<String> list = new ArrayList<String>();
        EditText text = (EditText)findViewById(R.id.restaurant_input);
        list.add(text.getText().toString());
        text = (EditText)findViewById(R.id.food_input);
        list.add(text.getText().toString());
        text = (EditText)findViewById(R.id.location_input);
        list.add(text.getText().toString());
        text = (EditText)findViewById(R.id.time_input);
        list.add(text.getText().toString());
        text = (EditText)findViewById(R.id.note_input);
        list.add(text.getText().toString());
        return list;
    }
}
