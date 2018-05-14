package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.preference.MySharedPreference;
import example.com.hb.diary.fragment.AlertFailedFragment;
import example.com.hb.diary.fragment.AlertSuccessFragment;
import example.com.hb.diary.fragment.ProgressFragment;

import static example.com.hb.diary.utils.Constant.ARG_CONTENT;
import static example.com.hb.diary.utils.Constant.ARG_MESSAGE;
import static example.com.hb.diary.utils.Constant.ARG_TITLE;

public class TerminateAccountActivity extends BaseActivity {
    @BindView(R.id.rootLayout)
    View rootLayout;

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.checkbox)
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate_account);
        initView();
        loginFirst();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
    }

    /**
     * Needed login before terminate account
     */
    private void loginFirst() {
        App app = (App) getApplication();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(app.getEmail(), app.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    }
                });
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

    @OnClick(R.id.button)
    void terminateAccount(View view){
        if (!checkBox.isChecked()){
            textView.setError("");
            Toast.makeText(this,
                    getResources().getString(R.string.termination_not_confirm),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new ProgressFragment());
        ft.addToBackStack(null);
        ft.commit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f;
                if (task.isSuccessful()){
                    deleteSavedAccount();
                    f = new AlertSuccessFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_TITLE, getResources().getString(R.string.account_terminated));
                    String content = getResources().getString(R.string.account_deleted);
                    content +="\n\n" + getResources().getString(R.string.account_deleted_2);
                    bundle.putString(ARG_CONTENT, content);
                    f.setArguments(bundle);
                    ft.replace(android.R.id.content, f);
                    ft.commit();
                } else {
                    task.getException().printStackTrace();
                    f = new AlertFailedFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_TITLE, getResources().getString(R.string.error));
                    bundle.putString(ARG_MESSAGE, getResources().getString(R.string.some_error_occur)
                            +": " + task.getException().getMessage());
                    f.setArguments(bundle);
                    ft.replace(android.R.id.content, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    /**
     * Delete saved account
     */
    private void deleteSavedAccount() {
        MySharedPreference pre = new MySharedPreference(this);
        pre.remove(MySharedPreference.EMAIL_LOGIN);
        pre.remove(MySharedPreference.PASSWORD);
        pre.remove(MySharedPreference.USER_UID);
        App app = (App) getApplication();
        app.setEmail(null);
        app.setPassword(null);
        app.setUuid(null);
    }
}
