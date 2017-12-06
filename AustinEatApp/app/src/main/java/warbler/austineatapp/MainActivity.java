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
    DeliverOrderFragment deliverOrderFragment;
    DiscoverFragment discoverFragment;
    FragmentManager fragmentManager;
    //FragmentTransaction fragmentTransaction;

    // encode three tabs
    enum TAB {
        DISCOVER,
        ORDER,
        PROFILE
    }

    // remember where are we
    private static TAB tab = TAB.DISCOVER;

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
                    tab = TAB.DISCOVER;
                    return true;
                case R.id.navigation_dashboard:
                    FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();

                    System.out.println("DEBUG: currentUserProperty is " + UserHelper.getCurrentUserProperty());

                    if (UserHelper.getCurrentUserProperty().equals("deliver")){
                        deliverOrderFragment = new DeliverOrderFragment();
                        fragmentTransaction2.replace(R.id.content, deliverOrderFragment);
                        fragmentTransaction2.commit();
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
                    tab = TAB.ORDER;

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
                    tab = TAB.PROFILE;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mainIntent = getIntent();
        int temp = mainIntent.getIntExtra("tab", 0);
        switch(temp){
            case 1:
                tab = TAB.ORDER;
                break;
            case 2:
                tab = TAB.PROFILE;
                break;
            default:
                tab = TAB.DISCOVER;
        }

        if (!UserHelper.isSignedIn()) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        } else {
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // go back to where we left
            if (tab == TAB.DISCOVER) {
                if (discoverFragment == null) {
                    discoverFragment = new DiscoverFragment();
                }
                fragmentTransaction.replace(R.id.content, discoverFragment);
                navigation.setSelectedItemId(R.id.navigation_home);
            } else if (tab == TAB.ORDER) {
                if (noPropertyFragment == null) {
                    noPropertyFragment = new NoPropertyFragment();
                }
                fragmentTransaction.replace(R.id.content, noPropertyFragment);
                navigation.setSelectedItemId(R.id.navigation_dashboard);
            } else if (tab == TAB.PROFILE) {
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment();
                }
                fragmentTransaction.replace(R.id.content, profileFragment);
                navigation.setSelectedItemId(R.id.navigation_notifications);
            }
            fragmentTransaction.commit();
        }
    }

    public void onClickWallet(View view) {
        profileFragment.onClickWallet(view);
    }

    public void onClickProfileEdit(View view) {
        profileFragment.onClickProfileEdit(view);
    }

}
