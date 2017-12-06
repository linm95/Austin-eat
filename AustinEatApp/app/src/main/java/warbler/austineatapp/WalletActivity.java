package warbler.austineatapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener {
    private BraintreeFragment braintreeFragment;
    private PayPalAccountNonce nonce;
    String topupAmount;
    private String errorMsg;

    public static class RetryDialogFragment extends DialogFragment {
        public String message;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    public BraintreeFragment InitBraintreeFragment(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        String clientToken;

        try {
            Response response = client.newCall(request).execute();
            clientToken = response.body().string();
        } catch (java.io.IOException e) {
            System.err.println("- Error: IO Exception getting Paypal client token");
            System.err.println("- Error: " + e.toString());
            return null;
        }

        try {
            return BraintreeFragment.newInstance(WalletActivity.this, clientToken);
        } catch (InvalidArgumentException e) {
            System.err.println("- Error: invalid client token");
            System.err.println("- Error: " + e.toString());
            return null;
        }
    }

    public boolean Topup(String url) {
        PostalAddress shippingAddress = nonce.getShippingAddress();
        String tag, token;

        if (UserHelper.useEmailAsToken) {
            tag = "email";
            token = UserHelper.getCurrentUserEmail();
        } else {
            tag = "idToken";
            token = UserHelper.getUserIdToken();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", topupAmount)
                .addFormDataPart("payment_method_nonce", nonce.getNonce())
                .addFormDataPart(tag, token)
                //.addFormDataPart("first_name", nonce.getFirstName())
                //.addFormDataPart("last_name", nonce.getLastName())
                //.addFormDataPart("street_address", shippingAddress.getStreetAddress())
                //.addFormDataPart("locality", shippingAddress.getLocality())
                //.addFormDataPart("region", shippingAddress.getRegion())
                //.addFormDataPart("postal_code", shippingAddress.getPostalCode())
                //.addFormDataPart("country", shippingAddress.getCountryCodeAlpha2())
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();

        try {
            Response response = client.newCall(request).execute();
            errorMsg = response.body().toString();
            return response.isSuccessful();
        } catch (IOException e) {
            System.err.println("- Error: IO Exception sending nonce to server");
            System.err.println("- Error: " + e.toString());
            return false;
        }
    }

     public String GetBalance(String url) {
         RequestBody body;
         if (UserHelper.useEmailAsToken) {
             body = new FormBody.Builder().add("email", UserHelper.getCurrentUserEmail()).build();
         } else {
             body = new FormBody.Builder().add("idToken", UserHelper.getUserIdToken()).build();
         }

         OkHttpClient client = new OkHttpClient();
         Request request = new Request.Builder().url(url).post(body).build();

         try {
             Response response = client.newCall(request).execute();
             return response.body().string();
         } catch (java.io.IOException e) {
             System.err.println("- Error: IO Exception getting balance");
             System.err.println("- Error: " + e.toString());
             return null;
         }
    }

    public void SetBalance(String balance) {
        TextView balanceView = (TextView) findViewById(R.id.walletBalance);
        String balance_with_dollar_sign = "$" + balance;
        balanceView.setText(balance_with_dollar_sign);
    }

    public void ShowTopupResult(Boolean successful) {
        if (successful) {
            // add balance
            TextView textView = (TextView) findViewById(R.id.walletBalance);
            String balance = textView.getText().toString();
            Float new_balance = Float.parseFloat(balance.substring(1))
                    + Float.parseFloat(topupAmount);
            String new_balance_str = String.format(java.util.Locale.US, "%.2f", new_balance);
            String balance_with_dollar_sign = "$" + new_balance_str;
            textView.setText(balance_with_dollar_sign);

            // notify user
            RetryDialogFragment dialogFragment = new RetryDialogFragment();
            dialogFragment.message = getString(R.string.wallet_success_msg);
            dialogFragment.show(getSupportFragmentManager(), "success");
        } else {
            RetryDialogFragment dialogFragment = new RetryDialogFragment();
            dialogFragment.message = getString(R.string.wallet_fail_msg);
            dialogFragment.message += "\n" + errorMsg;
            dialogFragment.show(getSupportFragmentManager(), "success");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        // create an http request to get client token
        AsyncTask<String, Void, Void> getClientToken = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... urls) {
                braintreeFragment = InitBraintreeFragment(urls[0]);
                return null;
            }
        };

        // execute http request above
        String url = getString(R.string.root_url) + getString(R.string.wallet_url);
        getClientToken.execute(url);

        // create an http request to get balance
        AsyncTask<String, Void, String> getBalance  = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                return GetBalance(urls[0]);
            }

            @Override
            protected void onPostExecute(String balance) {
                SetBalance(balance);
            }
        };

        url = getString(R.string.root_url) + getString(R.string.balance_url);
        getBalance.execute(url);
    }

    public void setupBraintreeAndStartExpressTopup(View view) {
        if (braintreeFragment != null) {
            EditText topupAmountEditText = (EditText) findViewById(R.id.walletInputAmount);
            topupAmount = topupAmountEditText.getText().toString();
            PayPalRequest request = new PayPalRequest(topupAmount)
                    .currencyCode("USD")
                    .intent(PayPalRequest.INTENT_AUTHORIZE);
            PayPal.requestOneTimePayment(braintreeFragment, request);
        } else {
            RetryDialogFragment dialogFragment = new RetryDialogFragment();
            dialogFragment.message = getString(R.string.wallet_init_msg);
            dialogFragment.show(getSupportFragmentManager(), "retry");
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        nonce = (PayPalAccountNonce) paymentMethodNonce;
        System.out.println("- Info" + nonce.getBillingAddress().getStreetAddress());

        AsyncTask<String,Void,Boolean> sendTransaction = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... urls) {
                return Topup(urls[0]);
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                ShowTopupResult(successful);
            }
        };

        String url = getString(R.string.root_url) + getString(R.string.topup_url);
        sendTransaction.execute(url);
    }
}
