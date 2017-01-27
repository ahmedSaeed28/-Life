package saeed.life.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saeed.life.Adapters.DrawersAdapter;
import saeed.life.Adapters.HomeRequestAdapter;
import saeed.life.Model.DonationRequest;
import saeed.life.Model.NavItem;
import saeed.life.Model.User;
import saeed.life.R;
import saeed.life.SQLiteDatabase.UserSQLite;

public class HomeActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private SharedPreferences isLogged;
    private SharedPreferences.Editor editor;
    private ArrayList<NavItem> mNavItems;
    private ListView mDrawerList;
    private DrawersAdapter adapter;
    private Button logOut;
    private TextView name, email;
    private static User user;
    private ListView requestList;
    private List<DonationRequest> donationRequests;
    private View progressView, requestFormView;
    private UserSQLite userSQLite;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ImageView iv;
    private MenuItem menuItem;
    private Button newRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        newRequest = (Button)findViewById(R.id.new_request);
        mNavItems = new ArrayList<NavItem>();
        userSQLite = new UserSQLite(HomeActivity.this);
        user = userSQLite.getUser();
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        name.setText(user.getName());
        email.setText(user.getEmail());
        mNavItems.add(new NavItem(getString(R.string.profile_word), R.drawable.ic_profile));
        mNavItems.add(new NavItem(getString(R.string.my_requests_word), R.drawable.ic_request));
        mDrawerList = (ListView) findViewById(R.id.navList);
        adapter = new DrawersAdapter(this, mNavItems, user);
        mDrawerList.setAdapter(adapter);
        logOut = (Button)findViewById(R.id.log_out);
        requestList = (ListView)findViewById(R.id.request_list);
        requestList.setEmptyView(findViewById(R.id.empty_list_item));
        requestFormView = findViewById(R.id.request_form);
        progressView = findViewById(R.id.request_progress);
        donationRequests = new ArrayList<>();

        isLogged = getApplicationContext().getSharedPreferences("Check", 0);
        editor = isLogged.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()) {
            showProgress(true);
            updateList();
        }
        else {
            Toast.makeText(HomeActivity.this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }

        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewRequestActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOnline()) {
                    Toast.makeText(HomeActivity.this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(HomeActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(R.string.sure_word);
                    dlgAlert.setPositiveButton(R.string.yes_word, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor = isLogged.edit();
                            editor.clear();
                            editor.commit();
                            userSQLite.deleteUser(user);
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    dlgAlert.setNegativeButton(R.string.no_word, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dlgAlert.show();

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ic_menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_By_hint));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(isOnline()) {
                    changeList(newText);
                }
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        new MenuInflater(this).inflate(R.menu.ic_menu_refresh, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItem = item;
        switch (item.getItemId()) {
            case R.id.airport_menuRefresh:
                if(isOnline()) {
                    LayoutInflater inflater = (LayoutInflater) getApplication()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    iv = (ImageView) inflater.inflate(R.layout.action_refresh, null);
                    Animation rotation = AnimationUtils.loadAnimation(getApplication(), R.anim.refresh_rotate);
                    rotation.setRepeatCount(Animation.INFINITE);
                    iv.startAnimation(rotation);
                    item.setActionView(iv);
                    updateList();
                }
                return true;
            case R.id.search:
                onSearchRequested();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean updateList(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Requests");
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> requestsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if(requestsMap != null) {
                            donationRequests = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : requestsMap.entrySet()) {
                                Map singleRequest = (Map) entry.getValue();
                                DonationRequest donationRequest = new DonationRequest(singleRequest.get("name").toString(),
                                        singleRequest.get("bloodType").toString(),
                                        singleRequest.get("city").toString(),
                                        singleRequest.get("hospital").toString(),
                                        singleRequest.get("phone").toString(),
                                        singleRequest.get("userId").toString(),
                                        singleRequest.get("userName").toString());
                                donationRequests.add(donationRequest);
                            }
                            requestList.setAdapter(new HomeRequestAdapter(HomeActivity.this, donationRequests));
                            showProgress(false);
                            if(iv != null){
                                iv.clearAnimation();
                                menuItem.setActionView(null);
                            }
                        }
                        else {
                            showProgress(false);
                            if(iv != null){
                                iv.clearAnimation();
                                menuItem.setActionView(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showProgress(false);
                        if(iv != null){
                            iv.clearAnimation();
                            menuItem.setActionView(null);
                        }
                        Toast.makeText(HomeActivity.this, getString(R.string.error_failed),
                                Toast.LENGTH_LONG).show();
                    }
                });
        return !donationRequests.isEmpty();
    }

    private void changeList(String s){
        if(TextUtils.isEmpty(s) && donationRequests.size()!=0)
            requestList.setAdapter(new HomeRequestAdapter(HomeActivity.this, donationRequests));
        else if(donationRequests.size() != 0){
            List<DonationRequest> donationRequestListAfterSearch = new ArrayList<>();
            for (int i = 0; i < donationRequests.size(); i++) {
                if (donationRequests.get(i).getBloodType().contains(s.toUpperCase()) ||
                        donationRequests.get(i).getCity().toUpperCase().startsWith(s.toUpperCase()))
                    donationRequestListAfterSearch.add(donationRequests.get(i));
            }
            requestList.setAdapter(new HomeRequestAdapter(HomeActivity.this, donationRequestListAfterSearch));
        }
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

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        HomeActivity.user = user;
    }

}
