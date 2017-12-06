package warbler.austineatapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CreateOrder extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private int PLACE_PICKER_REQUEST = 1;
    private String tail = "/create-order";
    private Context context;
    private Activity activity;
    private double lat;
    private double lon;
    private String res;
    private double res_lat;
    private double res_lon;
    private String food;
    private String price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        context = this;
        activity = this;

        Intent intent = getIntent();
        res_lat = intent.getDoubleExtra("res_lat", 0.0);
        res_lon = intent.getDoubleExtra("res_lon", 0.0);
        res = intent.getStringExtra("res");
        food = intent.getStringExtra("food");
        price = intent.getStringExtra("price");

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


        final EditText timeinput = findViewById(R.id.time_input);
        timeinput.setClickable(true);
        timeinput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeinput.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        final EditText locationInput = (EditText) findViewById(R.id.location_input);
        locationInput.setClickable(true);
        locationInput.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button button = findViewById(R.id.create_push);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle("Confirm")
                        .setMessage("Do you want to push this order?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                PushOrder pushOrder = new PushOrder();
                                pushOrder.execute(getInfo());
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        button = findViewById(R.id.create_prev);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, activity);
                EditText locationInput = findViewById(R.id.location_input);
                locationInput.setText(place.getName());
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
            }
        }
    }
    private class PushOrder extends AsyncTask<ArrayList<String>, Void, Integer>{


        @Override
        protected Integer doInBackground(ArrayList<String>... params) {
            ArrayList<String> info = params[0];
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("restaurant", res)
                    .add("res_lat", "" + res_lat)
                    .add("res_lon", "" + res_lon)
                    .add("food", food)
                    .add("price", price)
                    .add("location", info.get(0))
                    .add("lat", "" + lat)
                    .add("lon", "" + lon)
                    .add("deadline", info.get(1))
                    .add("note", info.get(2))
                    .add("email", UserHelper.getCurrentUserEmail())
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + tail)
                    .post(body)
                    .build();
            try{
                client.newCall(request).execute();
            }catch(IOException e){
                e.printStackTrace();
            }

            // Update user property
            //if(UserHelper.getCurrentUserProperty().equals("idle"))
            UserHelper.setCurrentUserProperty("eater");

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result){
            Toast.makeText(activity, "Order pushed successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, null);
            finish();
        }
    }

    private ArrayList<String> getInfo(){
        ArrayList<String> list = new ArrayList<String>();
        EditText text;
        text = findViewById(R.id.location_input);
        list.add(text.getText().toString());
        text = findViewById(R.id.time_input);
        list.add(text.getText().toString());
        text = findViewById(R.id.note_input);
        list.add(text.getText().toString());
        return list;
    }
}
