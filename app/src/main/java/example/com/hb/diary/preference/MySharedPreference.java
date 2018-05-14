package example.com.hb.diary.preference;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HP ProBook on 4/19/2018.
 */

public class MySharedPreference {
    public static final String DATE_FORMAT_TYPE = "date_format";
    private static final String SHARED_PREFERENCE_NAME = "my_diary";
    public static final String CODE_LOCK = "code";
    public static final String EMAIL_FOR_CODE = "email";
    public static final String EMAIL_LOGIN = "email_login";
    public static final String PASSWORD = "password";
    public static final String BACKGROUND_COLOR="background_color";
    public static final String TEXT_COLOR="text_color";
    public static final String TEXT_SIZE="text_size";
    public static final String USER_UID = "user_uid";
    public static final String IS_UNDERLINE="underline";
    public static final String IS_SYNC_WIFI_ONLY = "wifi";

    private SharedPreferences myShared;

    public MySharedPreference(Context context) {
        myShared=context.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
    }

    public boolean putString(String key, String value){
        SharedPreferences.Editor editor=myShared.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key, String defaultValue){
        return myShared.getString(key, defaultValue);
    }

    public boolean putInt(String key, int value){
        SharedPreferences.Editor editor=myShared.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public int getInt(String key, int defaultValue){
        return myShared.getInt(key, defaultValue);
    }

    public boolean remove(String key){
        SharedPreferences.Editor editor=myShared.edit();
        editor.remove(key);
        return editor.commit();
    }

    public boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor=myShared.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float getFloat(String key, int defaultValue){
        return myShared.getFloat(key, defaultValue);
    }

    public boolean putBoolean(String key, boolean value){
        SharedPreferences.Editor editor=myShared.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue){
        return myShared.getBoolean(key, defaultValue);
    }
}
