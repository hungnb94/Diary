package example.com.hb.diary;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by HP ProBook on 4/19/2018.
 */

public class App extends Application {
    public final int TIME_TO_LOCK = 8000;
    String TAG = "App";
    Date pauseTime = Calendar.getInstance().getTime();
    int nStart = 0;
    int nPause = 0;
    int backgroundColor = Color.GREEN;
    int textColor = Color.GREEN;
    float textSize = 0;
    boolean isUnderline=true;
    int dateFormat = 1;
    String code;
    String email, password, uuid;
    boolean syncWifiOnly;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isSyncWifiOnly() {
        return syncWifiOnly;
    }

    public void setSyncWifiOnly(boolean syncWifiOnly) {
        this.syncWifiOnly = syncWifiOnly;
    }

    /**
     * Counter Activity start
     */
    public void activityStart() {
        ++nStart;
        Log.i(TAG, "visible");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    /**
     * Counter Activity pause
     */
    public void activityPause() {
        ++nPause;
        Log.i(TAG, "invisible");
        updateTime();
    }

    public void updateTime(){
        pauseTime = Calendar.getInstance().getTime();
    }

    /**
     * This call when activity finish
     */
    public void activityFinish() {
        --nStart;
        --nPause;
    }

    public boolean shouldLogin() {
        Date now = Calendar.getInstance().getTime();
        Log.d(TAG, "Now      : " + now.toString());
        Log.d(TAG, "Last time: " + pauseTime.toString());
        return (now.getTime() - pauseTime.getTime()) >= TIME_TO_LOCK;
//        return nStart == nPause;
    }

    public String getCode(){
        return code;
    }

    public void setNewCode(String newCode){
        code = newCode;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor=color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setTextColor(int color) {
        this.textSize=color;
    }

    public int getTextColor() {
        return backgroundColor;
    }

    public void setTextSize(float textSize) {
        this.textSize=textSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setUnderline(boolean isUnderline) {
        this.isUnderline=isUnderline;
    }

    public boolean isUnderline(){
        return isUnderline;
    }

    public void setDateFormatType(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getDateFormatType() {
        return dateFormat;
    }
}
