package saeed.life.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import saeed.life.Model.User;
import saeed.life.R;
import saeed.life.SQLiteDatabase.UserSQLite;

public class SignUpActivity extends AppCompatActivity{

    private EditText name, email, password, confirmPassword, phone;
    private RadioButton male, female;
    private Spinner bloodType;
    private Button signUp;
    private View progressView, signUpFormView;
    private boolean isRunning;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences isLogged;
    private SharedPreferences.Editor editor;
    private UserSQLite userSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userSQLite = new UserSQLite(SignUpActivity.this);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);
        phone = (EditText)findViewById(R.id.phone);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        bloodType = (Spinner)findViewById(R.id.blood_type);
        String[] items = new String[]{getString(R.string.blood_type_word),
                "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.sign_up_spinner_item, items);
        bloodType.setAdapter(adapter);
        signUp = (Button)findViewById(R.id.sign_up);
        signUpFormView = findViewById(R.id.sign_up_form);
        progressView = findViewById(R.id.sign_up_progress);

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
        editor = isLogged.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning){
                    attemptSignUp();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
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

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nameString)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(emailString)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }
        else if (!isEmailValid(emailString)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (!TextUtils.isEmpty(passwordString) && !isPasswordValid(passwordString)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        if (!TextUtils.isEmpty(confirmPasswordString) &&
                !passwordString.equals(confirmPasswordString)) {
            confirmPassword.setError(getString(R.string.error_password_does_not_match));
            focusView = confirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneString)) {
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        }

        if(!isMale && !isFemale){
            TextView gender = (TextView)findViewById(R.id.gender);
            gender.setError(getString(R.string.error_field_required));
            focusView = gender;
            cancel = true;
        }

        if(bloodTypeString.equals(getString(R.string.blood_type_word)))
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
                String token = FirebaseInstanceId.getInstance().getToken();
                if(isMale){
                    user = new User(nameString, emailString, bloodTypeString, phoneString, getString(R.string.male_word), token);
                }
                else{
                    user = new User(nameString, emailString, bloodTypeString, phoneString, getString(R.string.female_word), token);
                }
                createAccount(passwordString);
            }
            else{
                Toast.makeText(SignUpActivity.this, getString(R.string.error_no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createAccount(String password){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        editor.clear();
                        editor.commit();
                        editor.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.commit();
                        isRunning = false;
                        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        myRef.child("Users").child(uId).setValue(user);
                        showProgress(false);
                        userSQLite.insertUser(user);
                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        finish();
                    }
                    else {
                        showProgress(false);
                        Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".com");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
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

            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}