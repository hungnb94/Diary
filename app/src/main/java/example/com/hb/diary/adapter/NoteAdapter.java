package example.com.hb.diary.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.utils.Constant;
import example.com.hb.diary.activity.AddingNoteActivity;
import example.com.hb.diary.activity.MainActivity;
import example.com.hb.diary.dialog.SelectNoteDialog;
import example.com.hb.diary.model.Note;
import io.realm.RealmResults;

import static example.com.hb.diary.utils.Constant.IS_EDIT;
import static example.com.hb.diary.utils.Constant.REQUEST_CODE_EDIT_NOTE;

/**
 * Created by HP ProBook on 4/19/2018.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
        implements View.OnClickListener {
    private MainActivity context;
    String TAG = "NoteAdapter";
    private RealmResults<Note> list;

    public NoteAdapter(MainActivity context, RealmResults<Note> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, final int position) {
        final App app = (App) context.getApplication();
        int color = app.getTextColor();
        int dateFormatType = app.getDateFormatType();
        holder.tvTitle.setTextColor(color);
        holder.tvContent.setTextColor(color);
        Note note = list.get(position);
        Date date = new Date(note.getDate());
        SimpleDateFormat format;
        switch (dateFormatType) {
            case 2:
                format = new SimpleDateFormat(Constant.ITEM_DATE_FORMAT_2);
                break;
            case 3:
                format = new SimpleDateFormat(Constant.ITEM_DATE_FORMAT_3);
                break;
            case 4:
                format = new SimpleDateFormat(Constant.ITEM_DATE_FORMAT_4);
                break;
            default:
                format = new SimpleDateFormat(Constant.ITEM_DATE_FORMAT_1);
                break;
        }
        if (date != null) {
            String strDate = format.format(date);
            StringTokenizer tokenizer = new StringTokenizer(strDate,";");
            holder.tvMonth.setText(tokenizer.nextToken());
            holder.tvDate.setText(tokenizer.nextToken());
            holder.tvYear.setText(tokenizer.nextToken());
        }
        if (TextUtils.isEmpty(note.getTitle())) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            holder.tvTitle.setText(note.getTitle());
        }
        if (TextUtils.isEmpty(note.getContent())) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            holder.tvContent.setText(note.getContent());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOption(position);
                return false;
            }
        });
        //Hide line between two item
        if (position == 0) holder.vSeperator.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvView:
                break;
            case R.id.tvDelete:
                break;
            case R.id.tvShare:
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMonth)
        TextView tvMonth;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvYear)
        TextView tvYear;
        @BindView(R.id.tvTitleNote)
        TextView tvTitle;
        @BindView(R.id.tvContentNote)
        TextView tvContent;
        @BindView(R.id.vSeperator)
        View vSeperator;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Show dialog
     * @param position
     */
    private void showOption(int position) {
        SelectNoteDialog dialog = new SelectNoteDialog(context, list.get(position));
        dialog.show();
    }

    /**
     * Edit a note
     * @param position: position in list
     */
    private void editNote(int position) {
        Intent intent = new Intent(context, AddingNoteActivity.class);
        Bundle bundle = new Bundle();
        Note note = list.get(position);
        bundle.putString(Note.FIELD_ID, note.getId());
        bundle.putLong(Note.FIELD_DATE, note.getDate());
        bundle.putString(Note.FIELD_TITLE, note.getTitle());
        bundle.putString(Note.FIELD_CONTENT, note.getContent());
        bundle.putLong(Note.FIELD_UPDATE_TIME, note.getUpdateTime());
        bundle.putBoolean(IS_EDIT, true);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
    }
}
