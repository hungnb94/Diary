package example.com.hb.diary.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.MySharedPreference;
import example.com.hb.diary.fragment.PasscodeChangeFragment;

public class PasswordActivity extends BaseActivity {
    private static final String TAG = PasswordActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 94;

    @BindView(R.id.layoutPassword)
    LinearLayout rootLayout;
    @BindView(R.id.edtEnterCode)
    EditText edtEnterCode;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtRepeatEmail)
    EditText edtRepeatEmail;
    @BindView(R.id.llDeactive)
    LinearLayout llDeactive;
    @BindView(R.id.btnDeactive)
    Button btnDeactive;
    boolean focusEmail=true;

    private MySharedPreference sharedPreference;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.codelock_setup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());

        sharedPreference = new MySharedPreference(this);
        String codelock = sharedPreference.getString(MySharedPreference.CODE_LOCK, null);
        String email = sharedPreference.getString(MySharedPreference.EMAIL_FOR_CODE, "");
        edtEmail.setText(email);
        edtRepeatEmail.setText(email);
        if (codelock != null) {
            edtEnterCode.setText(codelock);
            llDeactive.setVisibility(View.VISIBLE);
        } else {
            llDeactive.setVisibility(View.GONE);
        }
    }

    @OnFocusChange(R.id.edtEmail)
    void clickEmail(View view, boolean focus){
        clickEmail(focus);
    }

    @OnFocusChange(R.id.edtRepeatEmail)
    void clickRepeatEmail(View view, boolean focus){
        clickEmail(focus);
    }


    /**
     * Call method on first time click on edtEmail or edtRepeatEmail
     * It will show dialog to select email
     */
    private void clickEmail(boolean focus) {
        if (focus && focusEmail) {
            focusEmail=false;
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
            edtRepeatEmail.setText(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnSavePassword)
    public void savePasscode(View view) {
        String passcode = edtEnterCode.getText().toString();
        String email = edtEmail.getText().toString();
        String repeatEmail = edtRepeatEmail.getText().toString();
        if (TextUtils.isEmpty(passcode)) {
            edtEnterCode.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (TextUtils.isEmpty(repeatEmail)) {
            edtRepeatEmail.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (!email.equals(repeatEmail)) {
            edtRepeatEmail.setError(getResources().getString(R.string.does_not_match));
            return;
        }
        Log.e(TAG, "Passcode: " + passcode);
        sharedPreference.putString(MySharedPreference.CODE_LOCK, passcode);
        sharedPreference.putString(MySharedPreference.EMAIL_FOR_CODE, email);
        App app = (App) getApplication();
        app.setNewCode(passcode);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment f = new PasscodeChangeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PasscodeChangeFragment.MESSAGE, getResources().getString(R.string.codelock_activated));
        f.setArguments(bundle);
        ft.replace(android.R.id.content, f);
        ft.commit();
    }

    @OnClick(R.id.btnDeactive)
    void deactivePasscode(View view) {
        MySharedPreference sharedPreference = new MySharedPreference(this);
        sharedPreference.remove(MySharedPreference.CODE_LOCK);
        App app = (App) getApplication();
        app.setNewCode(null);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment f = new PasscodeChangeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PasscodeChangeFragment.MESSAGE, getResources().getString(R.string.codelock_deactivated));
        f.setArguments(bundle);
        ft.replace(android.R.id.content, f);
        ft.commit();
    }

    @OnFocusChange(R.id.edtEmail)
    void emailClick(View view, boolean focus) {
        if (focus) selectEmail();
    }

    @OnFocusChange(R.id.edtRepeatEmail)
    void repeatMailClick(View view, boolean focus) {
        if (focus) selectEmail();
    }

    private void selectEmail() {
        Log.e(TAG, "Get account google");
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
            Log.e(TAG, "Email: " + account.name);
        }
    }
}
