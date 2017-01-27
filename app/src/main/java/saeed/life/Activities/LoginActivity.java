package saeed.life.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import saeed.life.Model.User;
import saeed.life.R;
import saeed.life.SQLiteDatabase.UserSQLite;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView forgetPassword;
    private Button login;
    private View progressView, loginFormView;
    private SharedPreferences isLogged;
    private SharedPreferences.Editor editor;
    private boolean isRunning;
    private FirebaseAuth mAuth;
    private UserSQLite userSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        userSQLite = new UserSQLite(LoginActivity.this);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        forgetPassword = (TextView)findViewById(R.id.forget_password);
        login = (Button)findViewById(R.id.login);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
        editor = isLogged.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning){
                    attemptLogin();
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                View promptsView = inflater.inflate(R.layout.dialog_item, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
                builder.setView(promptsView);
                builder.setCancelable(false);

                final EditText input = (EditText) promptsView.findViewById(R.id.email);

                builder.setPositiveButton(getString(R.string.send_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        System.out.println(TextUtils.isEmpty(email));
                        if(isOnline()) {
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_field_required),
                                        Toast.LENGTH_LONG).show();
                            }
                            else if (!isEmailValid(email)) {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email),
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                sendEmail(email);
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this, getString(R.string.error_no_internet_connection),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void attemptLogin() {
        email.setError(null);
        password.setError(null);

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(passwordString)) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }

        if (TextUtils.isEmpty(emailString)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(emailString)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            if(isOnline()){
                showProgress(true);
                isRunning = true;
                login(emailString, passwordString);
            }
            else{
                Toast.makeText(LoginActivity.this, getString(R.string.error_no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                                    child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            ref.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            showProgress(false);
                                            if(dataSnapshot != null) {
                                                Map<String, Object> requestsMap = (Map<String, Object>) dataSnapshot.getValue();
                                                String[] s = new String[6];
                                                int i = 0;
                                                for (Map.Entry<String, Object> entry : requestsMap.entrySet()) {
                                                    s[i] = (String)entry.getValue();
                                                    i++;
                                                }
                                                userSQLite.insertUser(new User(s[1], s[2], s[4],
                                                        s[0], s[3], ""));
                                            }
                                            editor.clear();
                                            editor.commit();
                                            editor.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            editor.commit();
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            showProgress(false);
                                            isRunning = false;
                                            Toast.makeText(LoginActivity.this, getString(R.string.error_failed),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }
                        else{
                            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email_or_password),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return false;
    }

    private void sendEmail(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, getString(R.string.password_rest_email_sent),
                                    Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, getString(R.string.error_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".com");
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

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}