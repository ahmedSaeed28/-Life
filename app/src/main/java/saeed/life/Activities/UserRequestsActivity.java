package saeed.life.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saeed.life.Adapters.UserRequestAdapter;
import saeed.life.Model.DonationRequest;
import saeed.life.R;

public class UserRequestsActivity extends AppCompatActivity {

    private ListView requestList;
    private List<DonationRequest> donationRequests;
    private View progressView, requestFormView;
    private SharedPreferences isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);

        requestList = (ListView)findViewById(R.id.request_list);
        requestList.setEmptyView(findViewById(R.id.empty_list_item));
        requestFormView = findViewById(R.id.request_form);
        progressView = findViewById(R.id.request_progress);
        donationRequests = new ArrayList<>();

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isOnline()) {
            showProgress(true);
            updateList();
        }
        else {
            Toast.makeText(UserRequestsActivity.this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean updateList(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Requests");
        final String userId = isLogged.getString("userId", "");
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    Map<String, Object> requestsMap = (Map<String, Object>) dataSnapshot.getValue();
                    donationRequests = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : requestsMap.entrySet()) {
                        Map singleRequest = (Map) entry.getValue();
                        if(singleRequest.get("userId").equals(userId)) {
                            DonationRequest donationRequest = new DonationRequest(singleRequest.get("name").toString(),
                                    singleRequest.get("bloodType").toString(),
                                    singleRequest.get("city").toString(),
                                    singleRequest.get("hospital").toString(),
                                    singleRequest.get("phone").toString(),
                                    singleRequest.get("userId").toString(),
                                    singleRequest.get("userName").toString());
                            donationRequests.add(donationRequest);
                        }
                    }
                    showProgress(false);
                    requestList.setAdapter(new UserRequestAdapter(UserRequestsActivity.this, donationRequests));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
                Toast.makeText(UserRequestsActivity.this, getString(R.string.error_failed),
                        Toast.LENGTH_LONG).show();
            }
        });
        return !donationRequests.isEmpty();
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            requestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            requestFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    requestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            requestFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
