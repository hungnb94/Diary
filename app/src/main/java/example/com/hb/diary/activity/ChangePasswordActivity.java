package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import example.com.hb.diary.Utils.Config;
import example.com.hb.diary.Utils.MySharedPreference;
import example.com.hb.diary.fragment.AlertFailedFragment;
import example.com.hb.diary.fragment.AlertSuccessFragment;
import example.com.hb.diary.fragment.ProgressFragment;

public class ChangePasswordActivity extends BaseActivity {
    @BindView(R.id.rootLayout)
    View rootLayout;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtRepeatPassword)
    EditText edtRepeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        loginFirst();
    }

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

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.change_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btnChangePassword)
    void resetPassword(View view){
        final String password = edtPassword.getText().toString();
        String repeatpassword = edtRepeatPassword.getText().toString();
        if (TextUtils.isEmpty(password)){
            edtPassword.setError(getResources().getString(R.string.mandatory));
            return;
        }
        if (!password.equals(repeatpassword)){
            edtPassword.setError(getResources().getString(R.string.does_not_match));
            return;
        }
        if (password.length()< Config.MIN_PASSWORD_LENG){
            edtPassword.setError(getResources().getString(R.string.too_short));
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new ProgressFragment());
        ft.addToBackStack(null);
        ft.commit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment f;
                if (task.isSuccessful()){
                    saveNewPassword(password);
                    f = new AlertSuccessFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AlertSuccessFragment.ARG_TITLE, getResources().getString(R.string.password_updated));
                    bundle.putString(AlertSuccessFragment.ARG_CONTENT,
                            getResources().getString(R.string.update_password_guide));
                    f.setArguments(bundle);
                    ft.replace(android.R.id.content, f);
                    ft.commit();
                } else {
                    task.getException().printStackTrace();
                    f = new AlertFailedFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AlertFailedFragment.ARG_TITLE, getResources().getString(R.string.error));
                    bundle.putString(AlertFailedFragment.ARG_MESSAGE, getResources().getString(R.string.some_error_occur)
                            +": " + task.getException().getMessage());
                    f.setArguments(bundle);
                    ft.replace(android.R.id.content, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    private void saveNewPassword(String password) {
        MySharedPreference pre = new MySharedPreference(this);
        pre.putString(MySharedPreference.PASSWORD, password);
        App app = (App) getApplication();
        app.setPassword(password);
    }
}
