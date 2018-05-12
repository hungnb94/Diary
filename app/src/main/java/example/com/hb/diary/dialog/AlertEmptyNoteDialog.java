package example.com.hb.diary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import example.com.hb.diary.R;
import example.com.hb.diary.activity.AddingNoteActivity;

/**
 * Created by HP ProBook on 4/20/2018.
 */

public class AlertEmptyNoteDialog extends Dialog{
    Activity context;

    TextView tvYes, tvNo, tvCancel;

    public AlertEmptyNoteDialog(@NonNull Activity context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert_empty);
        this.context = context;
        initView();
        addListener();
    }

    private void addListener() {
        AddingNoteActivity activity = (AddingNoteActivity) context;
        tvYes.setOnClickListener(activity);
        tvNo.setOnClickListener(activity);
        tvCancel.setOnClickListener(activity);
    }

    private void initView() {
        tvYes = (TextView) findViewById(R.id.tvYes);
        tvNo = (TextView) findViewById(R.id.tvNo);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
    }
}
