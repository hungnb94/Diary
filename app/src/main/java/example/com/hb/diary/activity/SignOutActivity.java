package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.preference.MySharedPreference;
import example.com.hb.diary.fragment.AlertSuccessFragment;

import static example.com.hb.diary.utils.Constant.ARG_CONTENT;
import static example.com.hb.diary.utils.Constant.ARG_TITLE;

public class SignOutActivity extends BaseActivity {
    @BindView(R.id.btnYes)
    Button btnYes;
    @BindView(R.id.btnNo)
    Button btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnYes)
    void signOut(View view) {
        MySharedPreference preference = new MySharedPreference(this);
        preference.remove(MySharedPreference.USER_UID);
        App app = (App) getApplication();
        app.setUuid(null);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) auth.signOut();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle args = new Bundle();
        AlertSuccessFragment f = new AlertSuccessFragment();
        args.putString(ARG_TITLE, getResources().getString(R.string.disconnected));
        args.putString(ARG_CONTENT, getResources().getString(R.string.disconnected_guide));
        f.setArguments(args);
        ft.replace(android.R.id.content, f);
        ft.commit();
    }

    @OnClick(R.id.btnNo)
    void cancel(View view) {
        finish();
    }
}
