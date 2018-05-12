package example.com.hb.diary.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import example.com.hb.diary.App;
import example.com.hb.diary.activity.MainActivity;
import example.com.hb.diary.model.Note;
import io.realm.Realm;
import io.realm.RealmResults;

public class MyFirebaseUtil extends AsyncTask<Void, Void, Void>{
    String TAG = MyFirebaseUtil.class.getSimpleName();
    Activity activity;
    private static FirebaseDatabase database;
    RealmResults<Note> realmNotes;
    DatabaseReference myRef;
    String userUid;

    public MyFirebaseUtil(Activity activity) {
        this.activity = activity;
        App app = (App) activity.getApplication();
        userUid = app.getUuid();
        if (userUid == null) return;
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
    }

    /**
     * Delete note on firebase by noteId
     *
     * @param noteId
     */
    public void deleteOnFirebase(String noteId) {
        Log.e(TAG, "Delete on firebase " + noteId);
        if (userUid == null) return;
        modifyFirebaseState();
        DatabaseReference ref = database.getReference()
                .child(Note.getChild(userUid));
        ref.child(noteId).removeValue();
        Log.e(TAG, "Delete OK");
    }

    /**
     * Chỉnh trạng thái online/offline của firebase
     */
    private void modifyFirebaseState() {
        App app = (App) activity.getApplication();
        boolean wifiOnly = app.isSyncWifiOnly();
        if (wifiOnly && !InternetConnectionUtil.isWifiConnecion(activity)
                && InternetConnectionUtil.isOnline(activity)) {
            database.goOffline();
        } else {
            database.goOnline();
        }
    }

    public void sync() {
        if (userUid == null) return;
        modifyFirebaseState();
        String childRef = Note.getChild(userUid);
        myRef = database.getReference().child(childRef);
        final Realm realm = Realm.getDefaultInstance();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList realmList = new ArrayList();
                realmList.addAll(Arrays.asList(realmNotes.toArray()));
                ArrayList<Note> firebaseList = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, "Snapshot: " + snapshot.toString());
                    Note note = new Note();
                    note.setId(snapshot.getKey());
                    note.setDate((Long) snapshot.child(Note.FIELD_DATE).getValue());
                    note.setTitle((String) snapshot.child(Note.FIELD_TITLE).getValue());
                    note.setContent((String) snapshot.child(Note.FIELD_CONTENT).getValue());
                    note.setUpdateTime((Long) snapshot.child(Note.FIELD_UPDATE_TIME).getValue());
                    firebaseList.add(note);
                }
                syncTwoList(firebaseList, realmList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Sync between firebase database and realm database
     *
     * @param firebaseList: firebase database
     * @param realmList:    realm database
     */
    private void syncTwoList(ArrayList<Note> firebaseList, ArrayList<Note> realmList) {
        Collections.sort(firebaseList, new Comparator<Note>() {
            @Override
            public int compare(Note note, Note t1) {
                return note.getId().compareTo(t1.getId());
            }
        });
        Collections.sort(realmList, new Comparator<Note>() {
            @Override
            public int compare(Note note, Note t1) {
                return note.getId().compareTo(t1.getId());
            }
        });
        int i = 0, j = 0;
        while (i < firebaseList.size() && j < realmList.size()) {
            Note fn = firebaseList.get(i);
            Note rn = realmList.get(j);
            int compare = fn.getId().compareTo(rn.getId());
            if (compare < 0) {
                saveNoteToRealm(fn);
                ++i;
            } else if (compare > 0) {
                saveNoteToFirebase(rn);
                ++j;
            } else {
                if (fn.getUpdateTime() > rn.getUpdateTime()) {
                    saveNoteToRealm(fn);
                } else if (fn.getUpdateTime() < rn.getUpdateTime()) {
                    saveNoteToFirebase(rn);
                }
                ++i;
                ++j;
            }
        }
        while (i < firebaseList.size()) {
            saveNoteToRealm(firebaseList.get(i));
            ++i;
        }
        while (j < realmList.size()) {
            saveNoteToFirebase(realmList.get(j));
            ++j;
        }
    }

    /**
     * Save note from firebase database to realm database
     *
     * @param note: note to save
     */
    private void saveNoteToRealm(final Note note) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(note); // using insert API
            }
        });
    }


    /**
     * Add note to firebase database
     *
     * @param note: note to save
     */
    public void saveNoteToFirebase(Note note) {
        if (userUid == null) return;
        String childRef = Note.getChild(userUid);
        myRef = database.getReference().child(childRef);
        DatabaseReference ref = myRef.child(note.getId());
        Map<String, Object> map = note.toMap();
        Map<String, Object> addValue = new HashMap<>();
        addValue.put(note.getId(), map);
        myRef.updateChildren(addValue);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        sync();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.hideProgressBar();
            mainActivity.refreshListView();
        }
    }

    public void setRealmNotes(RealmResults<Note> realmNotes) {
        this.realmNotes = realmNotes;
    }
}
