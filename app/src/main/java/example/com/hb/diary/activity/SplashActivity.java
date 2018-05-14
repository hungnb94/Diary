package example.com.hb.diary.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.preference.MySharedPreference;
import example.com.hb.diary.utils.Constant;

import static example.com.hb.diary.utils.Constant.SPLASH_DISPLAY_LENGTH;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.layoutSplash)
    RelativeLayout rootLayout;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvGuide)
    TextView tvGuide;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    private Typeface typeface;
    private int backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MySharedPreference preference = new MySharedPreference(this);
        backgroundColor = preference.getInt(MySharedPreference.BACKGROUND_COLOR,
                getResources().getColor(R.color.colorDefault));
        int textColor = preference.getInt(MySharedPreference.TEXT_COLOR, Color.GREEN);
        boolean isUnderline = preference.getBoolean(MySharedPreference.IS_UNDERLINE, true);
        int dateFormat = preference.getInt(MySharedPreference.DATE_FORMAT_TYPE, 1);
        boolean syncWifyOnly = preference.getBoolean(MySharedPreference.IS_SYNC_WIFI_ONLY, false);
        String email = preference.getString(MySharedPreference.EMAIL_LOGIN, null);
        if(email==null) email = preference.getString(MySharedPreference.EMAIL_FOR_CODE, null);
        String password = preference.getString(MySharedPreference.PASSWORD, null);
        String uuid = preference.getString(MySharedPreference.USER_UID, null);
        App app = (App) getApplication();
        float textSize = preference.getFloat(MySharedPreference.TEXT_SIZE, -1);
        app.setBackgroundColor(backgroundColor);
        app.setTextColor(textColor);
        app.setUnderline(isUnderline);
        app.setDateFormatType(dateFormat);
        if (email != null) app.setEmail(email);
        if (password != null) app.setPassword(password);
        if (uuid != null) app.setUuid(uuid);
        if (textSize > 0) app.setTextSize(textSize);
        String code = preference.getString(MySharedPreference.CODE_LOCK, null);
        if (code == null) {
            initView();
            goToMainActivity();
        } else {
            goToLockActivity();
        }

    }

    /**
     * Go to LockActivity
     */
    private void goToLockActivity() {
        Intent intent = new Intent(this, LockActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IS_START_APP, true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * Open MainActivity
     * if input code == code
     * or code == null
     */
    private void goToMainActivity() {
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    /**
     * Khởi tạo giao diện
     */
    private void initView() {
        ButterKnife.bind(this);
        //Thiết lập font để sử dụng từ assets
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Yesteryear-Regular.ttf");
        //Set font cho textview của các bạn.
        tvTitle.setTypeface(typeface);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        tvGuide.setTypeface(typeface);
        tvVersion.setTypeface(typeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootLayout.setBackgroundColor(backgroundColor);
    }
}
