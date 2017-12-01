package warbler.austineatapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private class User {
        public String first_name;
        public String last_name;
        public String avatar_url;
        public String intro;
        public String[] favorite_food_styles;
        public String[] favorite_foods;
        public float requester_rate;
        public float deliveryperson_rate;
    }

    User user;

    public String printStringArray(String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(stringArray[i]);
        }
        return stringBuilder.toString();
    }

    public Void FetchProfile(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            user = gson.fromJson(response.body().string(), User.class);
        } catch (java.io.IOException e) {
            System.err.println("- Error: IO Exception while getting user profile");
            System.err.println("- Error: " + e.toString());
        }
        return null;
    }

    public void UpdateProfileUI() {
        // Name
        TextView userName = getActivity().findViewById(R.id.profileName);
        String name = user.first_name + " " + user.last_name;
        userName.setText(name);

        // Avatar
        ImageView avatar = getActivity().findViewById(R.id.profileAvatar);
        Picasso.with(getContext()).load(user.avatar_url).into(avatar);

        // Deliver rate
        RatingBar deliverRate = getActivity().findViewById(R.id.profileDeliverRatingBar);
        deliverRate.setRating(user.deliveryperson_rate);

        // Eater rate
        RatingBar eaterRate = getActivity().findViewById(R.id.profileEaterRatingBar);
        eaterRate.setRating(user.requester_rate);

        // intro
        TextView intro = getActivity().findViewById(R.id.profileIntroContent);
        intro.setText(user.intro);

        // Favorate food style
        TextView ffs = getActivity().findViewById(R.id.profileFFSContent);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < user.favorite_food_styles.length; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(user.favorite_food_styles[i]);
        }
        ffs.setText(stringBuilder.toString());

        // Favorate food
        TextView ff = getActivity().findViewById(R.id.profileFFContent);
        stringBuilder = new StringBuilder();
        for (int i = 0; i < user.favorite_foods.length; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(user.favorite_foods[i]);
        }
        ff.setText(stringBuilder.toString());
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AsyncTask<String, Void, Void> getProfile = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... urls) {
                return FetchProfile(urls[0]);
            }

            @Override
            protected void onPostExecute(Void v){
                UpdateProfileUI();
            }
        };

        getProfile.execute(getString(R.string.root_url) + getString(R.string.profile_url));
    }

    public void onClickWallet(View view) {
        Intent intent = new Intent(getActivity(), WalletActivity.class);
        getActivity().startActivity(intent);
    }

    public void onClickProfileEdit(View view) {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.putExtra("user_first_name", user.first_name);
        intent.putExtra("user_last_name", user.last_name);
        intent.putExtra("user_avatar_url", user.avatar_url);
        intent.putExtra("user_intro", user.intro);
        intent.putExtra("user_ffs", printStringArray(user.favorite_food_styles));
        intent.putExtra("user_ff", printStringArray(user.favorite_foods));
        getActivity().startActivity(intent);
    }

}
