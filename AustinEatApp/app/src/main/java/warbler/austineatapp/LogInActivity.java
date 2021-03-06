package warbler.austineatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private String tail = "/create-user";
    private String getPropertyTail = "/get-user-property";
    private TextView mStatusTextView;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LogInActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mStatusTextView = findViewById(R.id.status);
        SetUserProperty setUserProperty = new SetUserProperty();
        setUserProperty.execute();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private class CreateUser extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("email", UserHelper.getCurrentUserEmail())
                    .add("firstName", UserHelper.getFirstName())
                    .add("lastName", UserHelper.getLastName())
                    .add("url", UserHelper.getPhotoUrl())
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
            return null;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct.getEmail().endsWith("@utexas.edu")) {
                mStatusTextView.setText("Hello! " + acct.getDisplayName());
                updateUI(true);
                UserHelper.setCurrentUserID(acct.getId());
                UserHelper.setCurrentUserEmail(acct.getEmail());
                if(acct.getPhotoUrl() != null)
                    UserHelper.setPhotoUrl(acct.getPhotoUrl().toString());
                else
                    UserHelper.setPhotoUrl("https://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png?itok=xhm7jaxS&timestamp=1362601455");

                UserHelper.setFirstName(acct.getGivenName());
                UserHelper.setLastName(acct.getFamilyName());
                //Log.d("PHOTO URL", UserHelper.getPhotoUrl());
                CreateUser createUser = new CreateUser();
                createUser.execute();

                SetUserProperty setUserProperty = new SetUserProperty();
                setUserProperty.execute();
                //UserHelper.setCurrentUserProperty();

                //Intent intent = new Intent(this, DiscoverActivity.class);
                //startActivity(intent);

                Intent upIntent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, upIntent);

            }
            else{
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if(status.isSuccess()) {
                                    updateUI(false);
                                    mStatusTextView.setText("Please sign in with UT mail");
                                }
                                else{
                                    mStatusTextView.setText("Sign out failed:" + status.getStatusMessage());
                                }
                            }
                        }
                );
            }
        } else {
            // Signed out, show unauthenticated UI.
            mStatusTextView.setText("Signing in failed:" + result.getStatus());
        }
    }

    private void updateUI(boolean signedIn){
        if(signedIn){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            updateUI(false);
                            mStatusTextView.setText("You are signed out now");
                            UserHelper.reset();
                        }
                        else{
                            mStatusTextView.setText("Sign out failed:" + status.getStatusMessage());
                        }
                    }
                }
        );
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private class SetUserProperty extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + getPropertyTail + "?email=" + UserHelper.getCurrentUserEmail())
                    .build();
            try{
                Response response = client.newCall(request).execute();
                String userProperty = response.body().string();
                System.out.println("DEBUG: userProperty is " + userProperty);
                UserHelper.setCurrentUserProperty(userProperty);
            }catch(IOException e){
                e.printStackTrace();
            }


            return null;

        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
