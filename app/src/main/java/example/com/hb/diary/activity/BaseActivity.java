package example.com.hb.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import example.com.hb.diary.App;

/**
 * Created by HP ProBook on 4/19/2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App) getApplication();
        app.activityStart();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                myHandaling(paramThread, paramThrowable);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        App app = (App) getApplication();
        if (app.shouldLogin()) {
            String code = app.getCode();
            if (code != null) {
                Intent intent = new Intent(BaseActivity.this, LockActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void finish() {
        App app = (App) getApplication();
        app.activityFinish();
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App app = (App) getApplication();
        app.activityPause();
    }

    /**
     * Handle uncaught exception
     * @param paramThread
     * @param paramThrowable
     */
    public void myHandaling(Thread paramThread, Throwable paramThrowable) {
        Log.e("Alert", "Lets See if it Works !!!" + "ParamThread:::" + paramThread + "ParamThrowable:::" + paramThrowable);
        paramThrowable.printStackTrace();
        Toast.makeText(example.com.hb.diary.activity.BaseActivity.this,
                "Alert UncaughtException",
                Toast.LENGTH_SHORT)
                .show();
//        Intent in = new Intent(this, SplashActivity.class);
//        startActivity(in);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
