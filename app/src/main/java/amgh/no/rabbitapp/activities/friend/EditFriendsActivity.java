package amgh.no.rabbitapp.activities.friend;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import amgh.no.rabbitapp.hepler.Helper;
import amgh.no.rabbitapp.parse.UserRepository;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    private UserRepository mUserRepository;
    private ArrayAdapter<String> mAdapter;
    private ProgressBar progressBar;

    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            getListView().setVisibility(View.VISIBLE);
            if (e == null) {
                //Success
                Log.i(TAG, "Gott all friends ");
                mUserRepository.setFriends(parseUsers);
                int i = 0;
                for(ParseUser user : mUserRepository.getUsers()) {
                    for(ParseUser friend : mUserRepository.getFriends()) {
                        if (friend.getObjectId().equals(user.getObjectId())) {
                            getListView().setItemChecked(i, true);
                            Log.i(TAG, "2i " + i);
                        }
                    }
                    Log.i(TAG, "1i " + i);
                    i++;
                }
            } else {
                Log.i(TAG, "problems getting friends " + e.getMessage());
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
                Log.i(TAG, mUserRepository.getUserNames().length + " lengde");
                mAdapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, mUserRepository.getUserNames());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseUser friend = mUserRepository.getUsersByPosition(position);
        if (getListView().isItemChecked(position)) {
            mUserRepository
                    .setUserRelation(
                            friend, saveInBackground);
        } else {
            mUserRepository.removeUserRelation(friend, saveInBackground);
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        //store();
    }
    public void store(){
        int pos = 0;
        for(ParseUser user : mUserRepository.getUsers()) {
            if (getListView().isItemChecked(pos)){
                mUserRepository
                        .setUserRelation(
                                user, saveInBackground);
            }
            else {
                mUserRepository.removeUserRelation(user, saveInBackground);
            }
            pos++;
        }
    }
    private void addCheckMarks(){
        mUserRepository.getFriendsFromRepository(findFriendsInBackground);
    }
}
