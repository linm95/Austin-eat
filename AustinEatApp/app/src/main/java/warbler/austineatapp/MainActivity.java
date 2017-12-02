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
    EaterOrderFragment eaterOrderFragment;
    DiscoverFragment discoverFragment;
    FragmentManager fragmentManager;
    //FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();;
                    if(discoverFragment == null)
                        discoverFragment = new DiscoverFragment();
                    fragmentTransaction1.replace(R.id.content, discoverFragment);
                    fragmentTransaction1.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();



                    if (UserHelper.getCurrentUserProperty().equals("deliver")){

                    }
                    else if(UserHelper.getCurrentUserProperty().equals("eater")){
                        eaterOrderFragment = new EaterOrderFragment();
                        fragmentTransaction2.replace(R.id.content, eaterOrderFragment);
                        fragmentTransaction2.commit();
                    }
                    else{
                        noPropertyFragment = new NoPropertyFragment();
                        fragmentTransaction2.replace(R.id.content, noPropertyFragment);
                        fragmentTransaction2.commit();
                    }

                    return true;
                case R.id.navigation_notifications:
                    //FragmentManager fragmentManager = getFragmentManager();
                    //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();;
                    if (profileFragment == null) {
                        profileFragment = new ProfileFragment();
                    }
                    fragmentTransaction3.replace(R.id.content, profileFragment);
                    fragmentTransaction3.commit();
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

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        discoverFragment = new DiscoverFragment();
        fragmentTransaction.replace(R.id.content, discoverFragment);
        fragmentTransaction.commit();
    }

    public void onClickWallet(View view) {
        profileFragment.onClickWallet(view);
    }

    public void onClickProfileEdit(View view) {
        profileFragment.onClickProfileEdit(view);
    }

}
