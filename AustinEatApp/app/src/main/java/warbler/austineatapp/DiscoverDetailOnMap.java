package warbler.austineatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DiscoverDetailOnMap extends AppCompatActivity implements OnMapReadyCallback{

    private double resLat;
    private double resLon;
    private double destLat;
    private double destLon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_detail_on_map);
        Intent intent = getIntent();
        resLat = intent.getDoubleExtra("resLat", 0.0);
        resLon = intent.getDoubleExtra("resLon", 0.0);
        destLat = intent.getDoubleExtra("destLat", 0.0);
        destLon = intent.getDoubleExtra("destLon", 0.0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng cur = new LatLng(LocationHelper.getLatitude(), LocationHelper.getLongitude());
        LatLng res = new LatLng(resLat, resLon);
        LatLng dest = new LatLng(destLat, destLon);
        googleMap.addMarker(new MarkerOptions().position(cur).title("You"));
        googleMap.addMarker(new MarkerOptions().position(res).title("Restaurant"));
        googleMap.addMarker(new MarkerOptions().position(dest).title("Destination"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cur));
    }
}
