package saeed.life.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import saeed.life.R;

public class MainActivity extends AppCompatActivity {

    private Button login, signUp;
    private SharedPreferences isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login);
        signUp = (Button) findViewById(R.id.sign_up);

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isLogged.getString("userId", "").equals("")) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }
}
