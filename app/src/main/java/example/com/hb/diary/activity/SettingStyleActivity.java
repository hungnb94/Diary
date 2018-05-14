package example.com.hb.diary.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.utils.LineEditText;
import example.com.hb.diary.R;
import example.com.hb.diary.preference.MySharedPreference;

import static example.com.hb.diary.utils.Constant.BACKGROUND_COLOR_DIALOG_ID;
import static example.com.hb.diary.utils.Constant.CHANGE_DATE_STYLE;
import static example.com.hb.diary.utils.Constant.TEXT_COLOR_DIALOG_ID;

public class SettingStyleActivity extends BaseActivity implements ColorPickerDialogListener {
    private final String TAG = SettingStyleActivity.class.getSimpleName();

    @BindView(R.id.layoutSettingStyle)
    LinearLayout rootLayout;
    @BindView(R.id.llTheme)
    LinearLayout llTheme;
    @BindView(R.id.edtContentDemo)
    LineEditText edtContentDemo;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.cbLines)
    CheckBox checkBox;
    @BindView(R.id.llCalendar1)
    LinearLayout llCalendar1;
    @BindView(R.id.llCalendar2)
    LinearLayout llCalendar2;
    @BindView(R.id.llCalendar3)
    LinearLayout llCalendar3;
    @BindView(R.id.llCalendar4)
    LinearLayout llCalendar4;
    @BindView(R.id.ivShortcut1)
    ImageView ivShortcut1;
    @BindView(R.id.ivShortcut2)
    ImageView ivShortcut2;
    @BindView(R.id.ivShortcut3)
    ImageView ivShortcut3;
    @BindView(R.id.ivShortcut4)
    ImageView ivShortcut4;

    private int[] colors;
    private ImageButton[] btnColors;
    private int backgroundColor;
    private int textColor;
    private float defaultTextsize;
    private MySharedPreference pre;
    private float textSize;
    public static final int TEXT_SIZE_SCALE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_style);
        initView();
        addListener();
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

    private void addListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSize = i / TEXT_SIZE_SCALE;
                edtContentDemo.setTextSize(defaultTextsize + textSize);
                pre.putFloat(MySharedPreference.TEXT_SIZE, textSize);
                App app = (App) getApplication();
                app.setTextSize(textSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                edtContentDemo.setUnderline(bool);
                if (pre == null) pre = new MySharedPreference(SettingStyleActivity.this);
                pre.putBoolean(MySharedPreference.IS_UNDERLINE, bool);
                App app = (App) getApplication();
                app.setUnderline(bool);
                Log.e(TAG, "Check box change: " + bool);
            }
        });
    }

    private void initView() {
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbarSettingStyle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.color_and_style));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pre = new MySharedPreference(this);
        App app = (App) getApplication();
        backgroundColor = app.getBackgroundColor();
        rootLayout.setBackgroundColor(backgroundColor);
        textColor = app.getTextColor();
        edtContentDemo.setTextColor(textColor);
        float textSize = app.getTextSize();
        defaultTextsize = edtContentDemo.getTextSize() / 2;
        edtContentDemo.setUnderline(app.isUnderline());
        checkBox.setChecked(app.isUnderline());
        if (textSize > 0) {
            edtContentDemo.setTextSize(defaultTextsize + textSize);
            seekBar.setProgress((int) (textSize * TEXT_SIZE_SCALE));
        }
        int dateStyle = app.getDateFormatType();
        llCalendar1.setBackgroundColor(Color.TRANSPARENT);
        llCalendar2.setBackgroundColor(Color.TRANSPARENT);
        llCalendar3.setBackgroundColor(Color.TRANSPARENT);
        llCalendar4.setBackgroundColor(Color.TRANSPARENT);
        switch (dateStyle) {
            case 2:
                llCalendar2.setBackgroundColor(Color.WHITE);
                break;
            case 3:
                llCalendar3.setBackgroundColor(Color.WHITE);
                break;
            case 4:
                llCalendar4.setBackgroundColor(Color.WHITE);
                break;
            default:
                llCalendar1.setBackgroundColor(Color.WHITE);
                break;
        }

        TypedArray typedArray = getResources().obtainTypedArray(R.array.colors);
        colors = new int[typedArray.length()];
        btnColors = new ImageButton[colors.length];
        for (int i = 0; i < typedArray.length(); i++) {
            colors[i] = typedArray.getColor(i, 0);
        }
        typedArray.recycle();
        for (int i = 0; i < colors.length; i++) {
            btnColors[i] = new ImageButton(this);
            btnColors[i].setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,
                    1));
            btnColors[i].setBackgroundColor(colors[i]);
            llTheme.addView(btnColors[i]);
            final int finalI = i;
            btnColors[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int color = colors[finalI];
                    rootLayout.setBackgroundColor(color);
                    edtContentDemo.setTextColor(color);
                    saveBackgroundColor(color);
                    saveTextColor(textColor);
                }
            });
        }
    }

    /**
     * Set onclick when click on image view change background color
     *
     * @param view
     */
    @OnClick(R.id.ivChangeBackgroundColor)
    public void changeBackgroundColor(View view) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(BACKGROUND_COLOR_DIALOG_ID)
                .setColor(backgroundColor)
                .setShowAlphaSlider(true)
                .show(this);
    }

    @OnClick(R.id.ivChangeTextColor)
    public void changeTextColor(View view) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(TEXT_COLOR_DIALOG_ID)
                .setColor(backgroundColor)
                .setShowAlphaSlider(true)
                .show(this);
    }

    @OnClick(R.id.llCalendar1)
    public void selectCalendar1(View view) {
        llCalendar1.setBackgroundColor(Color.WHITE);
        llCalendar2.setBackgroundColor(Color.TRANSPARENT);
        llCalendar3.setBackgroundColor(Color.TRANSPARENT);
        llCalendar4.setBackgroundColor(Color.TRANSPARENT);
        if (pre == null) pre = new MySharedPreference(this);
        pre.putInt(MySharedPreference.DATE_FORMAT_TYPE, 1);
        App app = (App) getApplication();
        app.setDateFormatType(1);
        setResult(CHANGE_DATE_STYLE);
    }

    @OnClick(R.id.llCalendar2)
    public void selectCalendar2(View view) {
        llCalendar1.setBackgroundColor(Color.TRANSPARENT);
        llCalendar2.setBackgroundColor(Color.WHITE);
        llCalendar3.setBackgroundColor(Color.TRANSPARENT);
        llCalendar4.setBackgroundColor(Color.TRANSPARENT);
        if (pre == null) pre = new MySharedPreference(this);
        pre.putInt(MySharedPreference.DATE_FORMAT_TYPE, 2);
        App app = (App) getApplication();
        app.setDateFormatType(2);
        setResult(CHANGE_DATE_STYLE);
    }

    @OnClick(R.id.llCalendar3)
    public void selectCalendar3(View view) {
        llCalendar1.setBackgroundColor(Color.TRANSPARENT);
        llCalendar2.setBackgroundColor(Color.TRANSPARENT);
        llCalendar3.setBackgroundColor(Color.WHITE);
        llCalendar4.setBackgroundColor(Color.TRANSPARENT);
        if (pre == null) pre = new MySharedPreference(this);
        pre.putInt(MySharedPreference.DATE_FORMAT_TYPE, 3);
        App app = (App) getApplication();
        app.setDateFormatType(3);
        setResult(CHANGE_DATE_STYLE);
    }

    @OnClick(R.id.llCalendar4)
    public void selectCalendar4(View view) {
        llCalendar1.setBackgroundColor(Color.TRANSPARENT);
        llCalendar2.setBackgroundColor(Color.TRANSPARENT);
        llCalendar3.setBackgroundColor(Color.TRANSPARENT);
        llCalendar4.setBackgroundColor(Color.WHITE);
        if (pre == null) pre = new MySharedPreference(this);
        pre.putInt(MySharedPreference.DATE_FORMAT_TYPE, 4);
        App app = (App) getApplication();
        app.setDateFormatType(4);
        setResult(CHANGE_DATE_STYLE);
    }

    @OnClick(R.id.ivShortcut1)
    public void createShortcut1(View view) {
        createShortcutByIcon(R.drawable.ic_launcher_blue);
    }

    /**
     * Create shortcut by icon id
     *
     * @param icon: icon id
     */
    private void createShortcutByIcon(int icon) {
        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(), icon));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
        Toast.makeText(this, getResources().getString(R.string.add_shortcut), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ivShortcut2)
    public void createShortcut2(View view) {
        createShortcutByIcon(R.drawable.ic_launcher_purple);
    }

    @OnClick(R.id.ivShortcut3)
    public void createShortcut3(View view) {
        createShortcutByIcon(R.drawable.ic_launcher_green);
    }

    @OnClick(R.id.ivShortcut4)
    public void createShortcut4(View view) {
        createShortcutByIcon(R.drawable.ic_launcher_orange);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pre == null) pre = new MySharedPreference(this);
        pre.putFloat(MySharedPreference.TEXT_SIZE, textSize);
        App app = (App) getApplication();
        app.setTextSize(textSize);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == BACKGROUND_COLOR_DIALOG_ID) {
            backgroundColor = color;
            saveBackgroundColor(color);
            saveTextColor(color);
            rootLayout.setBackgroundColor(color);
            edtContentDemo.setTextColor(color);
        } else if (dialogId == TEXT_COLOR_DIALOG_ID) {
            textColor = color;
            saveTextColor(textColor);
            edtContentDemo.setTextColor(textColor);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }

    /**
     * Save background color setting
     *
     * @param backgroundColor
     */
    private void saveBackgroundColor(int backgroundColor) {
        if (pre == null) pre = new MySharedPreference(SettingStyleActivity.this);
        pre.putInt(MySharedPreference.BACKGROUND_COLOR, backgroundColor);
        App app = (App) getApplication();
        app.setBackgroundColor(backgroundColor);
    }

    /**
     * Save text color setting
     *
     * @param textColor
     */
    private void saveTextColor(int textColor) {
        if (pre == null) pre = new MySharedPreference(SettingStyleActivity.this);
        pre.putInt(MySharedPreference.TEXT_COLOR, textColor);
        App app = (App) getApplication();
        app.setTextColor(textColor);
    }
}
