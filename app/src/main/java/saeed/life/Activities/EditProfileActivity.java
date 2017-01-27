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
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import saeed.life.Model.User;
import saeed.life.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText name, email, password, confirmPassword, phone;
    private RadioButton male, female;
    private Spinner bloodType;
    private Button save;
    private View progressView, editProfileFormView;
    private boolean isRunning;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences isLogged;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);
        phone = (EditText)findViewById(R.id.phone);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        bloodType = (Spinner)findViewById(R.id.blood_type);
        save = (Button)findViewById(R.id.save);
        editProfileFormView = findViewById(R.id.edit_profile_form);
        progressView = findViewById(R.id.edit_profile_progress);

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
        user = HomeActivity.getUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        String[] items = new String[]{"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        int bloodTypeIndex = 0;
        for(int i=0 ; i<8 ; i++){
            if(items[i].equals(user.getBloodType()))
                bloodTypeIndex = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        bloodType.setAdapter(adapter);
        bloodType.setSelection(bloodTypeIndex);
        if (user.getGender().equals(getString(R.string.male_word))){
            male.setChecked(true);
        }
        else {
            female.setChecked(true);
        }
        save.setOnClickListener(new View.OnClickListener() {
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
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        phone.setError(null);
        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();
        String phoneString = phone.getText().toString();
        boolean isMale = male.isChecked();
        boolean isFemale = female.isChecked();
        String bloodTypeString = bloodType.getSelectedItem().toString();
        String genderString = "";

        if(isMale){
            genderString = getString(R.string.male_word);
        }
        else if(isFemale){
            genderString = getString(R.string.female_word);
        }


        if (!TextUtils.isEmpty(passwordString) && !TextUtils.isEmpty(confirmPasswordString) &&
                !passwordString.equals(confirmPasswordString)) {
            confirmPassword.setError(getString(R.string.error_password_does_not_match));
            confirmPassword.requestFocus();
        }
        else {
            if(isOnline()){
                showProgress(true);
                isRunning = true;
                updateAccount(new User(nameString, emailString, bloodTypeString,
                        phoneString, genderString, ""), passwordString);
            }
            else{
                Toast.makeText(EditProfileActivity.this, getString(R.string.error_no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAccount(User newUser, String password){
        String userId = isLogged.getString("userId", "");
        if(!user.getName().equals(newUser.getName())){
            user.setName(newUser.getName());
            myRef.child("Users").child(userId).child("name").setValue(newUser.getName());
        }
        if(!user.getEmail().equals(newUser.getEmail())){
            FirebaseAuth.getInstance().getCurrentUser().updateEmail(newUser.getEmail());
        }
        if(!TextUtils.isEmpty(password)){
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password);
        }
        if (!user.getPhone().equals(newUser.getPhone())){
            user.setPhone(newUser.getPhone());
            myRef.child("Users").child(userId).child("phone").setValue(newUser.getPhone());
        }
        if(!user.getBloodType().equals(newUser.getBloodType())){
            user.setBloodType(newUser.getBloodType());
            myRef.child("Users").child(userId).child("bloodType").setValue(newUser.getBloodType());
        }
        if(!user.getGender().equals(newUser.getGender())){
            user.setGender(newUser.getGender());
            myRef.child("Users").child(userId).child("gender").setValue(newUser.getGender());
        }
        HomeActivity.setUser(user);
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

            editProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            editProfileFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    editProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            editProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}