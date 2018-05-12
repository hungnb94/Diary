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
import example.com.hb.diary.Utils.MySharedPreference;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.layoutSplash)
    RelativeLayout rootLayout;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvGuide)
    TextView tvGuide;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    Typeface typeface;
    private int SPLASH_DISPLAY_LENGTH = 2000;
    private int backgroundColor;
    private int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MySharedPreference preference = new MySharedPreference(this);
        backgroundColor = preference.getInt(MySharedPreference.BACKGROUND_COLOR,
                getResources().getColor(R.color.colorDefault));
        textColor = preference.getInt(MySharedPreference.TEXT_COLOR, Color.GREEN);
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
     * Chuyen sang man hinh khoa de xu ly
     */
    private void goToLockActivity() {
        Intent intent = new Intent(this, LockActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(LockActivity.IS_START_APP, true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * Chuyển Activity sang MainActivity nếu:
     * người dùng nhập mã code đúng
     * hoặc người dùng chưa set code thì sẽ tự chuyển sau SPLASH_DISPLAY_LENGTH
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
