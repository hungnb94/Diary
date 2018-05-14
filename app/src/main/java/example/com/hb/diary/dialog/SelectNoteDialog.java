package example.com.hb.diary.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.MyFirebase;
import example.com.hb.diary.Utils.ShareIntentUtils;
import example.com.hb.diary.activity.AddingNoteActivity;
import example.com.hb.diary.activity.MainActivity;
import example.com.hb.diary.model.Note;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP ProBook on 4/20/2018.
 */

public class SelectNoteDialog extends Dialog {
    String TAG = SelectNoteDialog.class.getSimpleName();
    MainActivity context;
    Note note;
    @BindView(R.id.tvView)
    TextView tvView;
    @BindView(R.id.tvShare)
    TextView tvShare;
    @BindView(R.id.tvDelete)
    TextView tvDelete;

    public SelectNoteDialog(@NonNull MainActivity context, Note note) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_note);
        ButterKnife.bind(this);
        this.note = note;
        addListener();
    }

    public void addListener() {
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddingNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Note.FIELD_ID, note.getId());
                bundle.putLong(Note.FIELD_DATE, note.getDate());
                bundle.putString(Note.FIELD_TITLE, note.getTitle());
                bundle.putString(Note.FIELD_CONTENT, note.getContent());
                bundle.putLong(Note.FIELD_UPDATE_TIME, note.getUpdateTime());
                bundle.putBoolean(AddingNoteActivity.IS_EDIT, true);
                intent.putExtras(bundle);
                context.startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_NOTE);
                dismiss();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareIntentUtils.shareNote(context, note);
                dismiss();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Delete item");
                String noteId = note.getId();
                MyFirebase myFirebase = new MyFirebase(context);
                myFirebase.deleteOnFirebase(noteId);
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<Note> results = realm.where(Note.class)
                        .equalTo(Note.FIELD_ID, note.getId()).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteFirstFromRealm();
                    }
                });
                context.refreshListView();
                dismiss();
            }
        });
    }
}
