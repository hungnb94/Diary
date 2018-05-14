package example.com.hb.diary.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HP ProBook on 4/19/2018.
 */

public class Note extends RealmObject implements Serializable {
    public static final String FIELD_ID = "id";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_UPDATE_TIME = "updateTime";
    @PrimaryKey
    private String id;
    private long date;
    private String title;
    private String content;
    private long updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Note(String id, long date, String title, String content, long updateTime) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.updateTime = updateTime;
    }

    public Note() {
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_DATE, date);
        result.put(FIELD_TITLE, title);
        result.put(FIELD_CONTENT, content);
        result.put(FIELD_UPDATE_TIME, updateTime);

        return result;
    }

    public static String getChild(String userUid) {
        return "users/" + userUid + "/notes";
    }

}
