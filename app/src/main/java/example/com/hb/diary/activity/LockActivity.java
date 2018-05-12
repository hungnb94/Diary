package example.com.hb.diary.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.MySharedPreference;

public class LockActivity extends AppCompatActivity {
    String TAG = LockActivity.class.getSimpleName();
    public static final String IS_START_APP = "startApp";
    boolean isStart;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvGuide)
    TextView tvGuide;
    @BindView(R.id.edtEnterCode)
    EditText edtCode;
    @BindView(R.id.btnUnlock)
    Button btnUnlock;
    @BindView(R.id.btnForgotCode)
    Button btnForgotCode;
    @BindView(R.id.rootLayout)
    View rootLayout;

    MySharedPreference sharedPreference;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        //Thiết lập font để sử dụng từ assets
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Yesteryear-Regular.ttf");
        //Set font cho textview của các bạn.
        tvTitle.setTypeface(typeface);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        tvGuide.setTypeface(typeface);

        Bundle bundle = getIntent().getExtras();
        if (bundle==null){
            isStart=false;
        } else {
            isStart=true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @OnClick(R.id.btnUnlock)
    void unlock(View v){
        if (sharedPreference==null) sharedPreference = new MySharedPreference(this);
        String realcode = sharedPreference.getString(MySharedPreference.CODE_LOCK, "");
        String passcode = edtCode.getText().toString();
        if (realcode.equals(passcode)){
            App app = (App) getApplication();
            app.updateTime();
            if (isStart){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        } else {
            edtCode.setError(getResources().getString(R.string.incorrect_code));
        }
    }

    @OnClick(R.id.ivHelp)
    void clickHelp(View view){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
