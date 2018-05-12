package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.App;
import example.com.hb.diary.R;

public class ReminderActivity extends BaseActivity {
    @BindView(R.id.rootLayout)
    View rootLayout;
    @BindView(R.id.seekBar)
    SeekBar seekbar;
    @BindView(R.id.tvReminderState)
    TextView tvReminderState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        initView();
        addEvent();
    }

    private void addEvent() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i) {
                    case 0:
                        tvReminderState.setText(getResources().getString(R.string.no_reminder));
                        break;
                    case 1:
                        tvReminderState.setText(getResources().getString(R.string.daily));
                        break;
                    default:
                        tvReminderState.setText(i + " " + getResources().getString(R.string.days));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        App app = (App) getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
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

}
