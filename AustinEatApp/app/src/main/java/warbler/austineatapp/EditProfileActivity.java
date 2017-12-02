package warbler.austineatapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity {
    public static class ResultDialogFragment extends DialogFragment {
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

    public Boolean UploadUserInfo(String url) {
        EditText first_name_input = (EditText) findViewById(R.id.editProfileFirstName);
        String first_name = first_name_input.getText().toString();

        EditText last_name_input = (EditText) findViewById(R.id.editProfileLastName);
        String last_name = last_name_input.getText().toString();

        EditText intro_input = (EditText) findViewById(R.id.editProfileIntro);
        String intro = intro_input.getText().toString();

        EditText FFS_input = (EditText) findViewById(R.id.editProfileFFS);
        String FFS = FFS_input.getText().toString();

        EditText FF_input = (EditText) findViewById(R.id.editProfileFF);
        String FF = FF_input.getText().toString();

        String tag, token;

        if (UserHelper.useEmailAsToken) {
            tag = "email";
            token = UserHelper.getCurrentUserEmail();
        } else {
            tag = "idToken";
            token = UserHelper.getUserIdToken();
        }

        RequestBody body = new FormBody.Builder()
                .add("first_name", first_name)
                .add("last_name", last_name)
                .add("intro", intro)
                .add("favorite_food_styles", FFS)
                .add("favorite_foods", FF)
                .add(tag, token)
                .build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();

        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (java.io.IOException e) {
            System.err.println("- Error: IO Exception while getting user profile");
            System.err.println("- Error: " + e.toString());
            return false;
        }
    }

    public void ShowUploadResult(Boolean successful) {
        String message;

        if (successful) {
            message = "Your changes have been save!";
        } else {
            message = "Update failed. Please try again.";
        }

        ResultDialogFragment dialogFragment = new ResultDialogFragment();
        dialogFragment.message = message;
        dialogFragment.show(getSupportFragmentManager(), "result");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();

        EditText first_name = (EditText) findViewById(R.id.editProfileFirstName);
        first_name.setText(intent.getStringExtra("user_first_name"));

        EditText last_name = (EditText) findViewById(R.id.editProfileLastName);
        last_name.setText(intent.getStringExtra("user_last_name"));

        EditText intro = (EditText) findViewById(R.id.editProfileIntro);
        intro.setText(intent.getStringExtra("user_intro"));

        EditText FFS = (EditText) findViewById(R.id.editProfileFFS);
        FFS.setText(intent.getStringExtra("user_ffs"));

        EditText FF = (EditText) findViewById(R.id.editProfileFF);
        FF.setText(intent.getStringExtra("user_ff"));
    }

    public void onClickSave(View view) {
        AsyncTask<String, Void, Boolean> uploader = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... urls) {
                return UploadUserInfo(urls[0]);
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                ShowUploadResult(successful);
            }
        };

        uploader.execute(getString(R.string.root_url) + getString(R.string.edit_profile_url));
    }
}
