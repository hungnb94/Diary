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
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

public class SignUpActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 94;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    App app;
    MySharedPreference preference;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtRepeatPassword)
    EditText edtRepeatPassword;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.llSignUp)
    LinearLayout llSignUp;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    int firstClickEmail = 0;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        app = (App) getApplication();
        auth = FirebaseAuth.getInstance();
        preference = new MySharedPreference(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        String email = preference.getString(MySharedPreference.EMAIL_LOGIN, null);
        if (email == null) {
            email = preference.getString(MySharedPreference.EMAIL_FOR_CODE, null);
        }
        edtEmail.setText(email);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnFocusChange(R.id.edtEmail)
    void clickEmail(View view, boolean focus) {
        if (focus) firstClickEmail++;
        if (focus && firstClickEmail == 1) {
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
            edtEmail.setText(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnCreateAccount)
    void createAccount(View view) {
        final String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getResources().getString(R.string.mandatory));
            return;
        }
        final String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (password.length() < Config.MIN_PASSWORD_LENG) {
            edtPassword.setError(getResources().getString(R.string.too_short));
            return;
        }
        String repeatPassword = edtRepeatPassword.getText().toString();
        if (TextUtils.isEmpty(repeatPassword)) {
            edtRepeatPassword.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (!password.equals(repeatPassword)) {
            edtRepeatPassword.setError(getResources().getString(R.string.does_not_match));
            return;
        }
        if (!checkBox.isChecked()) {
            checkBox.setError("");
            Toast.makeText(this, getResources().getString(R.string.tos_must_accept), Toast.LENGTH_SHORT)
                    .show();
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
        llSignUp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        llSignUp.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment fragment;
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Create account successfull");
                            fragment = new AlertSuccessFragment();
                            preference.putString(MySharedPreference.EMAIL_LOGIN, email);
                            preference.putString(MySharedPreference.PASSWORD, password);
                        } else {
                            Log.e(TAG, "Create account failed " + task.getException().toString());
                            task.getException().printStackTrace();
                            fragment = new AlertFailedFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(AlertFailedFragment.ARG_TITLE,
                                    getResources().getString(R.string.account_exists));
                            bundle.putString(AlertFailedFragment.ARG_SMALL_TITLE, email);
                            bundle.putString(AlertFailedFragment.ARG_MESSAGE,
                                    getResources().getString(R.string.if_you_fogot_password));
                            fragment.setArguments(bundle);
                        }
                        ft.replace(android.R.id.content, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
    }
}
