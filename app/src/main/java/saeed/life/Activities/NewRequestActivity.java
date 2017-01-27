package saeed.life.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import saeed.life.Model.DonationRequest;
import saeed.life.R;
import saeed.life.SQLiteDatabase.UserSQLite;


public class NewRequestActivity extends AppCompatActivity {

    private final String SERVER_KEY = "AAAAMBSY7io:APA91bEAdxu2NEBjR8MjHXJdnJ6z38nij82040Yy90egQ0HjKPAAgvOUJzzDGAKhIviEL_dpgZKMPiHOGW_e1pc-2Zpeak6jklNMXpC_E2c3ZaCrhm484r-dQ6DE_9a0kJXz9Zr3YmFw";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private EditText name, phone, hospital, city;
    private Spinner bloodType;
    private Button newRequest;
    private View progressView, newRequestFormView;
    private boolean isRunning;
    private DonationRequest donationRequest;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userId, userName;
    private SharedPreferences isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        name = (EditText)findViewById(R.id.patient_name);
        phone = (EditText)findViewById(R.id.phone_to_connect);
        hospital = (EditText)findViewById(R.id.hospital);
        city = (EditText)findViewById(R.id.city);
        bloodType = (Spinner)findViewById(R.id.patient_blood_type);
        String[] items = new String[]{getString(R.string.patient_blood_type_word),
                "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.new_request_spinner_item, items);
        bloodType.setAdapter(adapter);
        newRequest = (Button)findViewById(R.id.request);
        newRequestFormView = findViewById(R.id.new_request_form);
        progressView = findViewById(R.id.new_request_progress);

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
        userId = isLogged.getString("userId", "");
        userName = new UserSQLite(NewRequestActivity.this).getUser().getName();
    }

    @Override
    protected void onStart() {
        super.onStart();

        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning){
                    attemptSignUp();
                }
            }
        });
    }

    private void attemptSignUp() {
        name.setError(null);
        phone.setError(null);
        hospital.setError(null);
        city.setError(null);
        String nameString = name.getText().toString();
        String phoneString = phone.getText().toString();
        String hospitalString = hospital.getText().toString();
        String cityString = city.getText().toString();
        String bloodTypeString = bloodType.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nameString)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneString)) {
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(hospitalString)) {
            hospital.setError(getString(R.string.error_field_required));
            focusView = hospital;
            cancel = true;
        }

        if (TextUtils.isEmpty(cityString)) {
            city.setError(getString(R.string.error_field_required));
            focusView = city;
            cancel = true;
        }

        if(bloodTypeString.equals(getString(R.string.patient_blood_type_word)))
        {
            TextView errorText = (TextView)bloodType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.error_field_required));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            if(isOnline()){
                showProgress(true);
                isRunning = true;
                donationRequest = new DonationRequest(nameString, bloodTypeString, cityString, hospitalString,
                        phoneString, userId, userName);
                myRef.child("Requests").push().setValue(donationRequest);
                sendNotification();
                Toast.makeText(NewRequestActivity.this, getString(R.string.done_message),
                        Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                Toast.makeText(NewRequestActivity.this, getString(R.string.error_no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            newRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            newRequestFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    newRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            newRequestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void sendNotification(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    Map<String, Object> requestsMap = (Map<String, Object>) dataSnapshot.getValue();
                    String tokens[] = new String[requestsMap.size()];
                    int i = 0;
                    for (Map.Entry<String, Object> entry : requestsMap.entrySet()) {
                        Map singleRequest = (Map) entry.getValue();
                        if(donationRequest.getBloodType().equals(singleRequest.get("bloodType").toString())) {
                            tokens[i] = singleRequest.get("token").toString();
                            i++;
                        }
                    }
                    SendRequestToFCM sendRequestToFCM = new SendRequestToFCM(tokens);
                    sendRequestToFCM.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sendNotification();
            }
        });
    }

    public class SendRequestToFCM extends AsyncTask<String, String, String>{

        private String tokens[];

        public SendRequestToFCM(String tokens[]) {
            this.tokens = tokens;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject data = new JSONObject();
                data.put("name", NewRequestActivity.this.donationRequest.getName());
                data.put("bloodType", NewRequestActivity.this.donationRequest.getBloodType());
                data.put("city", NewRequestActivity.this.donationRequest.getCity());
                data.put("hospital", NewRequestActivity.this.donationRequest.getHospital());
                data.put("phone", NewRequestActivity.this.donationRequest.getPhone());
                data.put("userId", NewRequestActivity.this.donationRequest.getUserId());
                data.put("userName", NewRequestActivity.this.donationRequest.getUserName());

                JSONObject notification = new JSONObject();
                notification.put("body", NewRequestActivity.this.donationRequest.getCity() +
                        ", " + NewRequestActivity.this.donationRequest.getHospital());
                notification.put("title", getString(R.string.notification_title));
                notification.put("icon", "myicon");
                notification.put("sound", "default");
                notification.put("color", "#990000");
                notification.put("click_action", "OPEN_NOTIFICATION_ACTIVITY");

                JSONArray mJSONArray = new JSONArray(Arrays.asList(tokens));

                JSONObject root = new JSONObject();
                root.put("notification", notification);
                root.put("data", data);
                root.put("registration_ids", mJSONArray);

                OkHttpClient mClient = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, root.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .addHeader("Authorization", "key=" + SERVER_KEY)
                        .build();
                Response response = mClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }
    }
}
