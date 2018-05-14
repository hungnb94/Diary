package example.com.hb.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.utils.InternetConnectionUtil;
import example.com.hb.diary.preference.MySharedPreference;

public class MyAccountActivity extends BaseActivity {
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    private boolean defaultCheck;   //Value for setting sync only by wifi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        if (InternetConnectionUtil.isOnline(this)) {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
            }, 2000);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.no_connection);
            textView.setText(getResources().getString(R.string.no_internet_connection));
        }
        defaultCheck = app.isSyncWifiOnly();
        checkBox.setChecked(defaultCheck);
        String email = app.getEmail();
        tvEmail.setText(email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_change_password:
                changePassword();
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_terminate_acc:
                terminateAcc();
                return true;
            case R.id.action_help:
                help();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Change password
     */
    private void changePassword() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean check = checkBox.isChecked();
        if (check != defaultCheck) {
            MySharedPreference preference = new MySharedPreference(this);
            preference.putBoolean(MySharedPreference.IS_SYNC_WIFI_ONLY, check);
            App app = (App) getApplication();
            app.setSyncWifiOnly(check);
        }
    }

    /**
     * User sign out
     */
    private void signOut() {
        Intent intent = new Intent(this, SignOutActivity.class);
        startActivity(intent);
    }

    /**
     * Terminate current account
     */
    private void terminateAcc() {
        Intent intent = new Intent(this, TerminateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Click help
     */
    private void help() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

}
