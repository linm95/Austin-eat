package warbler.austineatapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link DialogFragment} subclass.
 */
public class RatingBarFragment extends DialogFragment {


    public RatingBarFragment() {
        // Required empty public constructor
    }


    RatingBar ratingBar;
    Button getRating;
    String userProperty;
    String orderID;
    String rating;
    private String rateEaterTail = "/rate-eater";
    private String rateDeliverTail = "/rate-deliver";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_bar, container, false);
        getDialog().setTitle("Rate us");
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        getRating = (Button) view.findViewById(R.id.get_rating);
        getRating.setOnClickListener(new OnGetRatingClickListener());
        return view;
    }

    private class OnGetRatingClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //RatingBar$getRating() returns float value, you should cast(convert) it to string to display in a view
            //RatingBar mBar = (RatingBar) getActivity().findViewById(R.id.rating_bar);
            rating = String.valueOf(ratingBar.getRating());
            RatingBarFragment.Rate rate = new RatingBarFragment.Rate();
            rate.execute();
        }
    }

    void setValue(String userProperty, String orderID){
        //System.out.println(input);
        // do what else you want to do with code
        this.userProperty = userProperty;
        this.orderID = orderID;
    }

    private class Rate extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... args){
            OkHttpClient client = new OkHttpClient();
            String rateTail;
            if(userProperty.equals("eater"))
                rateTail = rateDeliverTail;
            else
                rateTail = rateEaterTail;
            RequestBody body = new FormBody.Builder()
                    .add("rate", rating)
                    .add("id", orderID)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.root_url) + rateTail)
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
            if(isSuccessful)
                Toast.makeText(getActivity(),"Rated "+rating+" stars",Toast.LENGTH_SHORT).show();

            getDialog().dismiss();
        }

    }
}
