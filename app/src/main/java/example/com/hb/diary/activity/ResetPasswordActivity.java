package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.fragment.AlertFailedFragment;
import example.com.hb.diary.fragment.AlertSuccessFragment;
import example.com.hb.diary.fragment.ProgressFragment;

import static example.com.hb.diary.utils.Constant.ARG_CONTENT;
import static example.com.hb.diary.utils.Constant.ARG_MESSAGE;
import static example.com.hb.diary.utils.Constant.ARG_SMALL_TITLE;
import static example.com.hb.diary.utils.Constant.ARG_TITLE;

public class ResetPasswordActivity extends BaseActivity {
    @BindView(R.id.rootLayout)
    View rootLayout;
    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.account));
        ButterKnife.bind(this);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        edtEmail.setText(app.getEmail());
    }

    @OnClick(R.id.btnReset)
    void resetPassword(View view){
        final String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)){
            edtEmail.setError(getResources().getString(R.string.mandatory));
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new ProgressFragment());
        ft.addToBackStack(null);
        ft.commit();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment f;
                if (task.isSuccessful()){
                    f = new AlertSuccessFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_TITLE, getResources().getString(R.string.reset_password));
                    bundle.putString(ARG_CONTENT,
                            getResources().getString(R.string.reset_password_guide));
                    f.setArguments(bundle);
                    ft.replace(android.R.id.content, f);
                    ft.commit();
                } else {
                    task.getException().printStackTrace();
                    f = new AlertFailedFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_TITLE, getResources().getString(R.string.account_not_found));
                    bundle.putString(ARG_SMALL_TITLE, email);
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
}
