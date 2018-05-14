package example.com.hb.diary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import example.com.hb.diary.R;
import example.com.hb.diary.activity.AddingNoteActivity;


/**
 * Created by HP ProBook on 4/21/2018.
 */

public class SaveChangeDialog extends Dialog {
    private TextView tvYes;
    private TextView tvNo;
    private TextView tvCancel;
    private Activity context;

    public SaveChangeDialog(@NonNull Activity context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_save_change);
        initView();
        addListener();
    }

    private void addListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.finish();
            }
        });
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                AddingNoteActivity activity = (AddingNoteActivity) context;
                activity.saveAndFinish();
            }
        });
    }

    private void initView() {
        tvCancel = findViewById(R.id.tvCancel);
        tvNo = findViewById(R.id.tvNo);
        tvYes = findViewById(R.id.tvYes);
    }
}
