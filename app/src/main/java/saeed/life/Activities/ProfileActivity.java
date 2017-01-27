package saeed.life.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saeed.life.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, email, phone, gender, bloodType;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        phone = (TextView)findViewById(R.id.phone);
        gender = (TextView)findViewById(R.id.gender);
        bloodType = (TextView)findViewById(R.id.blood_type);
        edit = (Button)findViewById(R.id.edit);
    }

    @Override
    protected void onStart() {
        super.onStart();

        name.setText(HomeActivity.getUser().getName());
        email.setText(HomeActivity.getUser().getEmail());
        phone.setText(HomeActivity.getUser().getPhone());
        gender.setText(HomeActivity.getUser().getGender());
        bloodType.setText(HomeActivity.getUser().getBloodType());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
    }
}
