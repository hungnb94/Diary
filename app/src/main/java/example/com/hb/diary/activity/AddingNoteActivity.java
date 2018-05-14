package example.com.hb.diary.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.LineEditText;
import example.com.hb.diary.Utils.MyFirebase;
import example.com.hb.diary.Utils.ShareIntentUtils;
import example.com.hb.diary.dialog.AlertEmptyNoteDialog;
import example.com.hb.diary.dialog.SaveChangeDialog;
import example.com.hb.diary.model.Note;
import io.realm.Realm;
import io.realm.RealmResults;

import static example.com.hb.diary.Utils.Config.DATE_FORMAT;

public class AddingNoteActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AddingNoteActivity.class.getSimpleName();
    public static final int RESULT_DELETE = 11;
    public static final String IS_EDIT = "edit";
    boolean isEdit = false;

    @BindView(R.id.layoutAddingNote)
    RelativeLayout layoutAddingNote;
    @BindView(R.id.tvDateToAdd)
    TextView tvDate;
    @BindView(R.id.edtTitle)
    EditText edtTitle;
    @BindView(R.id.edtContent)
    LineEditText edtContent;
    @BindView(R.id.tvSaving)
    TextView tvSaving;

    Note note;
    private AlertEmptyNoteDialog dialog;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_note);

        realm = Realm.getDefaultInstance();
        initView();
        addListener();
    }

    private void addListener() {
        tvDate.setOnClickListener(this);
    }

    /**
     * Init view
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.write_note));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        App app = (App) getApplication();
        layoutAddingNote.setBackgroundColor(app.getBackgroundColor());
        int textColor = app.getTextColor();
        float textSize = app.getTextSize() / SettingStyleActivity.TEXT_SIZE_SCALE;
        float defaultTextSize = edtContent.getTextSize() / 2;
        tvDate.setTextColor(textColor);
        edtTitle.setTextColor(textColor);
        edtContent.setTextColor(textColor);
        boolean isUnderline = app.isUnderline();
        edtContent.setUnderline(isUnderline);
        if (textSize > 0) {
            tvDate.setTextSize(defaultTextSize + textSize + 2);
            edtTitle.setTextSize(defaultTextSize + textSize + 2);
            edtContent.setTextSize(defaultTextSize + textSize);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean(IS_EDIT)) {
            isEdit = true;
            note = new Note(bundle.getString(Note.FIELD_ID),
                    bundle.getLong(Note.FIELD_DATE),
                    bundle.getString(Note.FIELD_TITLE),
                    bundle.getString(Note.FIELD_CONTENT),
                    bundle.getLong(Note.FIELD_UPDATE_TIME));
            long date = note.getDate();
            String format = new SimpleDateFormat(DATE_FORMAT).format(new Date(date));
            tvDate.setText(format);
            edtTitle.setText(note.getTitle());
            edtContent.setText(note.getContent());
        } else {
            Calendar cal = Calendar.getInstance();
            long date = cal.getTime().getTime();
            String format = new SimpleDateFormat(DATE_FORMAT).format(new Date(date));
            tvDate.setText(format);
        }
        tvSaving.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                displayHome();
                return true;
            case R.id.action_add:
                addNote();
                return true;
            case R.id.action_share:
                shareNote();
                return true;
            case R.id.action_delete:
                deleteNote();
                return true;
//            case R.id.action_emoji:
//                turnEmoji();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Go to main activity
     */
    private void displayHome() {
        if (isEdit) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();
                Date date = format.parse(tvDate.getText().toString());
                if (date.getTime() == note.getDate() && title.equals(note.getTitle())
                        && content.equals(note.getContent())) {
                    finish();
                    return;
                }
                SaveChangeDialog dialog = new SaveChangeDialog(this);
                dialog.show();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "Parse Exception from tvDate");
            }
        } else {
            String title = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                finish();
                return;
            }
            SaveChangeDialog dialog = new SaveChangeDialog(this);
            dialog.show();
        }
    }

    private void turnEmoji() {
    }

    /**
     * Sharing a note
     */
    private void shareNote() {
        ShareIntentUtils.shareNote(this, note);
    }

    /**
     * Delete a note
     */
    private void deleteNote() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_deleting);
        TextView cancel = (TextView) dialog.findViewById(R.id.tvCancel);
        final TextView delete = (TextView) dialog.findViewById(R.id.tvYes);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteId = note.getId();
                MyFirebase myFirebase = new MyFirebase(AddingNoteActivity.this);
                myFirebase.deleteOnFirebase(noteId);
                deleteOnRealm();
                dialog.dismiss();
                setResult(RESULT_DELETE);
                finish();
            }
        });
        dialog.show();
    }

    /**
     * Delete note on realm database
     */
    public void deleteOnRealm() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Note> rows = realm.where(Note.class)
                        .equalTo(Note.FIELD_ID, note.getId()).findAll();
                if (rows.size() > 0) rows.deleteFirstFromRealm();
            }
        });
    }

    /**
     * Add a new note
     */
    private void addNote() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) alertSaveEmptyNote();
        else saveAndFinish();
    }

    /**
     * Save note to realm and finish activity
     */
    public void saveAndFinish() {
        if (note==null) {
            note = new Note();
            note.setId(UUID.randomUUID().toString());
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                try {
                    Date date = format.parse(tvDate.getText().toString());
                    note.setDate(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                note.setTitle(edtTitle.getText().toString());
                note.setContent(edtContent.getText().toString());
                note.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                realm.copyToRealmOrUpdate(note); // using insert API
            }
        });
        MyFirebase firebaseUtil = new MyFirebase(this);
        firebaseUtil.saveNoteToFirebase(note);
        tvSaving.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Alert when user save an empty note
     */
    private void alertSaveEmptyNote() {
        dialog = new AlertEmptyNoteDialog(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                Log.d(TAG, "Click cancel empty note");
                dialog.dismiss();
                break;
            case R.id.tvNo:
                Log.d(TAG, "Click don't save empty note");
                dialog.dismiss();
                finish();
                break;
            case R.id.tvYes:
                Log.d(TAG, "Click save empty");
                dialog.dismiss();
                saveAndFinish();
                break;
            case R.id.tvDateToAdd:
                showDatePicker();
            default:
                break;
        }
    }

    /**
     * Show date picker to select
     */
    private void showDatePicker() {
        final Calendar cal = new GregorianCalendar();
        Date date = new Date(note.getDate());
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            date = format.parse(tvDate.getText().toString());
            note.setDate(date.getTime());
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }
        cal.setTime(date);
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            //Sự kiện khi click vào nút Done trên Dialog
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                cal.set(year, month, day);
                String format = new SimpleDateFormat(DATE_FORMAT)
                        .format(cal.getTime());
                tvDate.setText(format);
            }
        };
        //Hiển thị ra Dialog
        DatePickerDialog pic = new DatePickerDialog(
                AddingNoteActivity.this,
                callback, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        pic.show();
    }

}
