package example.com.hb.diary.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.R;
import example.com.hb.diary.model.Note;
import io.realm.Realm;
import io.realm.RealmResults;

public class ExportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.export));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
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

    @OnClick(R.id.btnExport)
    void export(View view) {
        createFile();
        String filename = "diary/notes.txt";
        String title = getResources().getString(R.string.export);
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(filelocation);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
//        sharingIntent .setType("vnd.android.cursor.dir/email");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                title);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(sharingIntent,
                getResources().getString(R.string.complete_action_using)));
    }

    /**
     * Create file save data before export
     */
    private void createFile() {
        RealmResults<Note> realmResults = Realm.getDefaultInstance().where(Note.class).findAll();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/diary");
        myDir.mkdirs();
        File file = new File(myDir, "notes.txt");
        if (file.exists())
            file.delete();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(getResources().getString(R.string.number_of_notes) + ": " + realmResults.size());
            out.newLine();
            for (int i = 0; i < realmResults.size(); i++) {
                out.newLine();
                out.write("-------------------------");
                out.newLine();
                out.write("# " + i);
                out.newLine();
                out.write("-------------------------");
                out.newLine();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                out.write(getResources().getString(R.string.date) + ": "
                        + format.format(new Date(realmResults.get(i).getDate())));
                out.newLine();
                out.write(getResources().getString(R.string.title) + ": " + realmResults.get(i).getTitle());
                out.newLine();
                out.newLine();
                out.write(realmResults.get(i).getContent());
                out.newLine();
                out.newLine();
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
