package saeed.life.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import saeed.life.Model.DonationRequest;
import saeed.life.R;

public class NotificationActivity extends AppCompatActivity {

    private TextView user, blood, name, city, hospital, phone;
    private DonationRequest donationRequest;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = (TextView)findViewById(R.id.user);
        blood = (TextView)findViewById(R.id.blood_type);
        name = (TextView)findViewById(R.id.patient_name);
        city = (TextView)findViewById(R.id.city);
        hospital = (TextView)findViewById(R.id.hospital);
        phone = (TextView)findViewById(R.id.phone_to_connect);

        bundle = getIntent().getExtras();

        donationRequest = new DonationRequest(bundle.get("name").toString(),
                bundle.get("bloodType").toString(), bundle.get("city").toString(),
                bundle.get("hospital").toString(), bundle.get("phone").toString(),
                bundle.get("userId").toString(), bundle.get("userName").toString());
    }

    @Override
    protected void onStart() {
        super.onStart();

        user.setText(donationRequest.getUserName());
        blood.setText(donationRequest.getBloodType());
        name.setText(donationRequest.getName());
        city.setText(donationRequest.getCity());
        hospital.setText(donationRequest.getHospital());
        phone.setText(donationRequest.getPhone());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
        finish();
    }
}
