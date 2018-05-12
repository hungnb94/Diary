package example.com.hb.diary.Utils;

import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

import example.com.hb.diary.R;
import example.com.hb.diary.model.Note;

/**
 * Created by HP ProBook on 4/21/2018.
 */

public class ShareIntentUtils {
    public static void shareNote(Context context, Note note) {
        SimpleDateFormat format = new SimpleDateFormat(Config.DATE_FORMAT);
        String sDate = format.format(new Date(note.getDate()));
        String title = context.getResources().getString(R.string.diary_note)
                + ", " + sDate
                + ", " + note.getTitle();
        String shareBody = context.getResources().getString(R.string.date) + ": " + sDate
                + "\n" + context.getResources().getString(R.string.subject) + ": " + note.getTitle()
                + "\n\n" + note.getContent()
                +"\n\n---\n" + context.getResources().getString(R.string.signal_share);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getResources().getString(R.string.share_using)));

    }
}
