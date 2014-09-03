package amgh.no.rabbitapp.activities.friend;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.apphelper.Helper;
import amgh.no.rabbitapp.parse.repository.UserRepository;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    private UserRepository mUserRepository;
    private ArrayAdapter<String> mAdapter;
    private ProgressBar progressBar;
    private List<ParseUser> mUserFriendsList;

    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            getListView().setVisibility(View.VISIBLE);
            if (e == null) {
                //Success
                mUserRepository.setFriends(parseUsers);
                int i = 0;
                for(ParseUser user : mUserRepository.getUsers()) {
                    for(ParseUser friend : mUserRepository.getFriends()) {
                        if (friend.getObjectId().equals(user.getObjectId())) {
                            getListView().setItemChecked(i, true);
                        }
                    }
                    i++;
                }
            } else {
            }
        }
    };

    private FindCallback<ParseUser> findUsersInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            if (e == null) {
                //Success
                setProgressBarIndeterminateVisibility(false);
                mUserRepository.setUsers(parseUsers);
                mAdapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                        android.R.layout.simple_list_item_checked, mUserRepository.getUserNames());
                setListAdapter(mAdapter);
                addCheckMarks();
                Log.d(TAG, "Got all users");
            } else {
                Helper.showErrorDialog(EditFriendsActivity.this, R.string.error_dialog_title,
                        R.string.error_dialog_message_friends);
                Log.d(TAG, e.getMessage());
            }
        }
    };
    
    private SaveCallback saveInBackground = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null) {
                //Success
                Log.d(TAG, "Did save");
            }
            else {
                Log.d(TAG, "Cant save");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
        mUserRepository = new UserRepository();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        getListView().setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onResume(){
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        if (mUserRepository.getCurrentUser() != null) {
            mUserRepository.getUsersFromRepository(findUsersInBackground);
        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseUser friend = mUserRepository.getUsersByPosition(position);
        if (getListView().isItemChecked(position)) {
            mUserRepository
                    .setUserRelation(
                            friend);
        } else {
            mUserRepository.removeUserRelation(friend);
        }
        //To not exhausting the main thread, after getting
        //I/Choreographer: Skipped 60 frames!  The application may be doing too much work on its main thread.

        DoInBackground doInBackground = new DoInBackground();
        doInBackground.execute();

    }
    private void addCheckMarks(){
        mUserRepository.getFriendsFromRepository(findFriendsInBackground);
    }
    private class DoInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mUserRepository.saveUser(saveInBackground);
            return null;
        }
    }
}
