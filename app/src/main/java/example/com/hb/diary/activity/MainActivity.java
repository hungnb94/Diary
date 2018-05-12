package example.com.hb.diary.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.Utils.MyFirebaseUtil;
import example.com.hb.diary.adapter.NoteAdapter;
import example.com.hb.diary.model.Note;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_ADD_NOTE = 100;
    public static final int REQUEST_CODE_EDIT_NOTE = 120;
    private static final int REQUEST_CODE_SETTING_STYLE_ACTIVITY = 130;
    private static final String FACEBOOK_URL = "https://m.facebook.com/WriteDiaryApp";
    Realm realm;

    @BindView(R.id.listView)
    RecyclerView listView;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.ivCloseSearch)
    ImageView ivCloseSearch;
    @BindView(R.id.tvSearchKey)
    TextView tvSearchKey;
    @BindView(R.id.tvItemMatches)
    TextView tvItemMatches;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Button btnAccount;
    TextView tvAccount;
    LinearLayout llCodelock, llColorAndStyle, llReminder, llExport, llRate,
            llRecommend, llVisitOnFB, llFeedback, llHelp, llAbout;
    private NoteAdapter adapter;
    private RealmResults<Note> noteResults, allNotes;
    LinearLayout navHeaderAccount;
    private DrawerLayout drawer;

    String email, password, userUid;
    FirebaseAuth auth;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        App app = (App) getApplication();
        email = app.getEmail();
        password = app.getPassword();
        userUid = app.getUuid();
        auth = FirebaseAuth.getInstance();
        initView();
        sync();
    }

    /**
     * Sync data between firebase and realm
     */
    public void sync() {
        if (auth.getCurrentUser() != null) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            App app = (App) getApplication();
                            if (task.isSuccessful()) {
                                Log.e(TAG, "Login successful");
                                Log.e(TAG, "User UID " + task.getResult().getUser().getUid());
                                userUid = auth.getUid();
                                app.setUuid(userUid);
                            } else {
                                app.setUuid(null);
                            }
                        }
                    });
        }
        if (auth.getCurrentUser() == null) return;
        allNotes = realm.where(Note.class).findAll();
        MyFirebaseUtil myFirebase = new MyFirebaseUtil(this);
        myFirebase.setRealmNotes(allNotes);
        progressBar.setVisibility(View.VISIBLE);
        myFirebase.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            refreshListView();
        } else if (requestCode == REQUEST_CODE_EDIT_NOTE) {
            if (resultCode == RESULT_OK || resultCode == AddingNoteActivity.RESULT_DELETE) {
                refreshListView();
            }
        } else if (requestCode == REQUEST_CODE_SETTING_STYLE_ACTIVITY
                && resultCode == SettingStyleActivity.CHANGE_DATE_STYLE) {
            refreshListView();
        }
    }

    /**
     * Khởi tạo giao diện
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.write_note));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderAccount = findViewById(R.id.navHeaderAccount);
        btnAccount = (Button) findViewById(R.id.btnAccount);
        tvAccount = (TextView) findViewById(R.id.tvAccount);
        llCodelock = (LinearLayout) findViewById(R.id.llCodelock);
        llColorAndStyle = (LinearLayout) findViewById(R.id.llColorStyle);
        llReminder = (LinearLayout) findViewById(R.id.llReminder);
        llExport = (LinearLayout) findViewById(R.id.llExport);
//        llRate = (LinearLayout) findViewById(R.id.llRateReview);
        llRecommend = (LinearLayout) findViewById(R.id.llRecommendFriend);
        llVisitOnFB = (LinearLayout) findViewById(R.id.llVisitFacebook);
//        llFeedback = (LinearLayout) findViewById(R.id.llFeedback);
        llHelp = (LinearLayout) findViewById(R.id.llHelp);
        llAbout = (LinearLayout) findViewById(R.id.llAbout);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });

        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);
        showListView();
    }

    /**
     * Lam moi lai danh sach listView
     */
    public void refreshListView() {
        noteResults = realm.where(Note.class).findAll().sort(Note.FIELD_DATE, Sort.DESCENDING,
                Note.FIELD_UPDATE_TIME, Sort.DESCENDING);
        adapter = new NoteAdapter(MainActivity.this, noteResults);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
    }

    /**
     * Hiển thị danh sách note theo thời gian giảm dần
     */
    private void showListView() {
        refreshListView();
    }

    private void addNote() {
        Intent intent = new Intent(this, AddingNoteActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App app = (App) getApplication();
        drawer.setBackgroundColor(app.getBackgroundColor());
        fab.setBackgroundTintList(ColorStateList.valueOf(app.getBackgroundColor()));
        int colors[] = {app.getBackgroundColor(), app.getBackgroundColor(),
                app.getBackgroundColor(), Color.DKGRAY};
        if (auth.getCurrentUser() != null) {
            tvAccount.setVisibility(View.VISIBLE);
            email = app.getEmail();
            tvAccount.setText(email);
        } else {
            tvAccount.setVisibility(View.INVISIBLE);
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, colors);
        navHeaderAccount.setBackgroundDrawable(gradientDrawable);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.ivCloseSearch)
    public void closeSearchNotify(View view) {
        refreshListView();
        searchLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.llCodelock)
    public void codeLock(View view) {
        Intent intent = new Intent(this, PasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.llColorStyle)
    public void changeColorStyle(View view) {
        Intent intent = new Intent(this, SettingStyleActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTING_STYLE_ACTIVITY);
    }

    @OnClick(R.id.btnAccount)
    public void clickButtonAccount(View view) {
        openAccountActivity();
    }

    @OnClick(R.id.tvAccount)
    public void clickTextViewAccount(View view) {
        openAccountActivity();
    }

    @OnClick(R.id.llHelp)
    void clickHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.llRecommendFriend)
    void recommendFriend(View view) {
        String defaultSubject = getResources().getString(R.string.take_a_look);
        String body = "http://market.android.com/details?id=" + getPackageName();

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_SUBJECT, defaultSubject);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    @OnClick(R.id.llExport)
    void clickExport(View view) {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.llReminder)
    void clickReminder(View view){
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.llVisitFacebook)
    void visitFacebook(View view) {
        Uri uri = Uri.parse(FACEBOOK_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.llAbout)
    void clickAbout(View view){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Open AccountGuideActivity
     */
    private void openAccountActivity() {
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, AccountGuideActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MyAccountActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Show search dialog with edittext and button
     */
    private void showSearchDialog() {
        // khởi tạo dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        // xét layout cho dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_searching);
        // Tạo spinner
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerFilter);
        // Danh sách của spinner
        ArrayList<String> years = new ArrayList<>();
        years.add(getResources().getString(R.string.all));
        if (allNotes == null) allNotes = realm.where(Note.class).findAll();
        for (Note note : allNotes) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String year = format.format(new Date(note.getDate()));
            if (!years.contains(year)) years.add(year);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.item_spinner, years);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinner.setAdapter(adapter);
        TextView search = (TextView) dialog.findViewById(R.id.tvSearch);
        TextView close = (TextView) dialog.findViewById(R.id.tvClose);
        final EditText field = (EditText) dialog.findViewById(R.id.edtSearch);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmQuery<Note> realmQuery = realm.where(Note.class)
                        .contains(Note.FIELD_TITLE, field.getText().toString());
                String year = spinner.getSelectedItem().toString();
                if (!year.equals(getResources().getString(R.string.all))) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    try {
                        long startTime = format.parse(year + "/01/01 00:00:00").getTime();
                        long stopTime = format.parse((year + 1) + "/01/01 00:00:00").getTime();
                        realmQuery.greaterThan(Note.FIELD_DATE, startTime)
                                .lessThan(Note.FIELD_DATE, stopTime);
                    } catch (ParseException e) {
                        Log.e(TAG, "ParseException");
                        e.printStackTrace();
                    }
                }

                noteResults = realmQuery.findAll().sort(Note.FIELD_DATE, Sort.DESCENDING);
                tvSearchKey.setText(getResources().getString(R.string.action_search)
                        + ": " + field.getText().toString());
                tvItemMatches.setText(noteResults.size()
                        + " " + getResources().getString(R.string.matches));
                NoteAdapter adapter = new NoteAdapter(MainActivity.this, noteResults);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                listView.setLayoutManager(mLayoutManager);
                listView.setItemAnimator(new DefaultItemAnimator());
                listView.setAdapter(adapter);
                searchLayout.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_codelock:
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
