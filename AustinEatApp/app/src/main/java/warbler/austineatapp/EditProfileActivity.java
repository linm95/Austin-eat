package warbler.austineatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditProfileActivity extends AppCompatActivity {

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
}
