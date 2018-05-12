package example.com.hb.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.Config;
import example.com.hb.diary.Utils.InternetConnectionUtil;
import example.com.hb.diary.Utils.MySharedPreference;
import example.com.hb.diary.fragment.AlertFailedFragment;
import example.com.hb.diary.fragment.AlertSuccessFragment;
import example.com.hb.diary.fragment.OfflineFragment;

public class LogInActivity extends BaseActivity {
    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 50;

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    MySharedPreference preference;
    int emailClickFirstTime = 0;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        preference = new MySharedPreference(this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        String email = preference.getString(MySharedPreference.EMAIL_LOGIN, null);
        if (email != null) {
            edtEmail.setText(email);
        } else {
            email = preference.getString(MySharedPreference.EMAIL_FOR_CODE, null);
            edtEmail.setText(email);
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(LogInActivity.this, gso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_reset_password:
                resetPassword();
                return true;
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    @OnFocusChange(R.id.edtEmail)
    void clickEmail(View view, boolean focus) {
        if (focus) emailClickFirstTime++;
        if (focus && emailClickFirstTime == 1) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        edtEmail.setText(account.getEmail());
    }

    @OnClick(R.id.btnSignIn)
    void signIn(View view) {
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (password.length() < Config.MIN_PASSWORD_LENG) {
            edtPassword.setError(getResources().getString(R.string.too_short));
            return;
        }
        if (!InternetConnectionUtil.isOnline(this)) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment f = new OfflineFragment();
            ft.replace(android.R.id.content, f);
            ft.addToBackStack(null);
            ft.commit();
            return;
        }
        llLogin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        llLogin.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Login successful");
                            Log.e(TAG, "User UID " + task.getResult().getUser().getUid());
                            preference.putString(MySharedPreference.EMAIL_LOGIN, email);
                            preference.putString(MySharedPreference.PASSWORD, password);
                            String uuid = task.getResult().getUser().getUid();
                            preference.putString(MySharedPreference.USER_UID, uuid);
                            App app = (App) getApplication();
                            app.setEmail(email);
                            app.setPassword(password);
                            app.setUuid(uuid);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Fragment f = new AlertSuccessFragment();
                            ft.replace(android.R.id.content, f);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    }
                })
                .addOnFailureListener(LogInActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        llLogin.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);

                        e.printStackTrace();
                        Bundle bundle = new Bundle();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            //Invalid password
                            bundle.putString(AlertFailedFragment.ARG_TITLE,
                                    getResources().getString(R.string.invalid_login));
                            bundle.putString(AlertFailedFragment.ARG_MESSAGE,
                                    getResources().getString(R.string.forgot_account_password));
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            //Invalid email
                            bundle.putString(AlertFailedFragment.ARG_TITLE,
                                    getResources().getString(R.string.account_not_found));
                            bundle.putString(AlertFailedFragment.ARG_MESSAGE,
                                    getResources().getString(R.string.account_does_not_exists));
                        } else {
                            //Other
                            bundle.putString(AlertFailedFragment.ARG_TITLE,
                                    getResources().getString(R.string.some_error_occur));
                            bundle.putString(AlertFailedFragment.ARG_MESSAGE,
                                    e.getMessage().toString());
                        }
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment f = new AlertFailedFragment();
                        f.setArguments(bundle);
                        ft.replace(android.R.id.content, f);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
    }
}
