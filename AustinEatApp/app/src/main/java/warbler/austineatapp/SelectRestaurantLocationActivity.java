package warbler.austineatapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class SelectRestaurantLocationActivity extends AppCompatActivity {
    private Activity activity;
    private int PLACE_PICKER_REQUEST = 1;
    private double lat;
    private double lon;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_restaurant_location);

        activity = this;
        final EditText locationInput = (EditText) findViewById(R.id.restaurant_input);
        locationInput.setClickable(true);
        locationInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button button = findViewById(R.id.create_next);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.food_input);
                Intent intent = new Intent(activity, CreateOrder.class);
                intent.putExtra("food", text.getText().toString());
                text = findViewById(R.id.price_input);
                intent.putExtra("price", text.getText().toString());
                intent.putExtra("res", res);
                intent.putExtra("res_lat", lat);
                intent.putExtra("res_lon", lon);
                //startActivity(intent);
                startActivityForResult(intent, 2);
            }
        });
        button = findViewById(R.id.create_cancel);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                //Intent intent = new Intent(activity, MainActivity.class);
                //startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, activity);
                res = place.getName().toString();
                EditText locationInput = findViewById(R.id.restaurant_input);
                locationInput.setText(res);
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
            }
        }
        else{
            if (resultCode == RESULT_OK)
                finish();
        }
    }
}
