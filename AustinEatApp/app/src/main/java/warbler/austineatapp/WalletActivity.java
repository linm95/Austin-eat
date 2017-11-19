package warbler.austineatapp;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.InvalidArgumentException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletActivity extends AppCompatActivity {
    private BraintreeFragment braintreeFragment;

    public String GetClientToken(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            System.err.println("- Error: IO Exception getting Paypal client token");
            System.err.println("- Error: " + e.toString());
            return null;
        }
    }

    public BraintreeFragment CreateBraintreeFragment(String clientToken) {
        try {
            return BraintreeFragment.newInstance(WalletActivity.this, clientToken);
        } catch (InvalidArgumentException e) {
            System.err.println("- Error: invalid client token");
            System.err.println("- Error: " + e.toString());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        // create an http request to get client token
        AsyncTask<String, Void, String> getClientToken = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                return GetClientToken(urls[0]);
            }

            @Override
            protected void onPostExecute(String clientToken) {
                braintreeFragment = CreateBraintreeFragment(clientToken);
            }
        };

        // execute http request above
        String url = getString(R.string.root_url) + getString(R.string.wallet_url);
        getClientToken.execute(url);
    }
}
