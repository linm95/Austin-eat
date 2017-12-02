package warbler.austineatapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ProfileFragment profileFragment;
    NoPropertyFragment noPropertyFragment;
    DiscoverFragment discoverFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(discoverFragment == null)
                        discoverFragment = new DiscoverFragment();
                    fragmentTransaction.replace(R.id.content, discoverFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:

                    if (UserHelper.getCurrentUserProperty().equals("deliver")){

                    }
                    else if(UserHelper.getCurrentUserProperty().equals("eater")){

                    }
                    else{
                        noPropertyFragment = new NoPropertyFragment();
                        fragmentTransaction.replace(R.id.content, noPropertyFragment);
                        fragmentTransaction.commit();
                    }

                    return true;
                case R.id.navigation_notifications:
                    //FragmentManager fragmentManager = getFragmentManager();
                    //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (profileFragment == null) {
                        profileFragment = new ProfileFragment();
                    }
                    fragmentTransaction.replace(R.id.content, profileFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!UserHelper.isSignedIn()) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClickWallet(View view) {
        profileFragment.onClickWallet(view);
    }

    public void onClickProfileEdit(View view) {
        profileFragment.onClickProfileEdit(view);
    }

}
