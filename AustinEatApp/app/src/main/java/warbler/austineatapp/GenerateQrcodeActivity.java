package warbler.austineatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenerateQrcodeActivity extends AppCompatActivity{

    private String LOG_TAG = "GenerateQRCode";
    private String orderID;
    private TextView qrInput;
    private String rating;
    private String rateDeliverTail = "/rate-deliver";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_generate_qrcode);
        Intent intent = getIntent();
        orderID = intent.getStringExtra("orderID");
        qrInput = (TextView) findViewById(R.id.qrInput);
        qrInput.setText(orderID);
        //Button button1 = (Button) findViewById(R.id.button1);
        //button1.setOnClickListener(this);

    }

    public void generateBtnClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                //EditText qrInput = (EditText) findViewById(R.id.qrInput);

                String qrInputText = qrInput.getText().toString();
                Log.v(LOG_TAG, qrInputText);

                //Find screen size
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3/4;

                //Encode with a QR Code image
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
                    myImage.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }


                break;

            // More buttons go here (if any) ...

        }
    }

    public void rateBtnClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RatingBarFragment ratingBarFragment = new RatingBarFragment();
        ratingBarFragment.setValue("deliver", orderID);
        ratingBarFragment.show(fragmentManager, "dialog");
    }


    private class RateDeliver extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("rating", rating)
                    .add("id", orderID)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + rateDeliverTail)
                    .post(body)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                    return true;
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isSuccessful) {


        }

    }

}
