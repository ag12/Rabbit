package amgh.no.rabbitapp.activities.friend;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.adapters.UserAdapter;
import amgh.no.rabbitapp.apphelper.Helper;
import amgh.no.rabbitapp.parse.repository.UserRepository;

public class EditFriendsActivity extends Activity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    private UserRepository mUserRepository;
    private UserAdapter mAdapter;
    private ProgressBar progressBar;
    private List<ParseUser> mUserFriendsList;
    private GridView mGridView;

    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            mGridView.setVisibility(View.VISIBLE);
            if (e == null) {
                //Success
                mUserRepository.setFriends(parseUsers);
                int i = 0;
                for(ParseUser user : mUserRepository.getUsers()) {
                    for(ParseUser friend : mUserRepository.getFriends()) {
                        if (friend.getObjectId().equals(user.getObjectId())) {
                            mGridView.setItemChecked(i, true);
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
                mAdapter = new UserAdapter(
                        EditFriendsActivity.this,
                        (ArrayList<ParseUser>) mUserRepository.getUsers());
                mGridView.setAdapter(mAdapter);
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
        setContentView(R.layout.user_grid);
        mUserRepository = new UserRepository();
        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        TextView empty = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(empty);
        mGridView.setOnItemClickListener(mGridViewOnClickListener);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onResume(){
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        if (mUserRepository.getCurrentUser() != null) {
            mUserRepository.getUsersFromRepository(findUsersInBackground);
        }
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
    private AdapterView.OnItemClickListener mGridViewOnClickListener =
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ImageView checkImageView =
                    (ImageView) view.findViewById(R.id.checkImageView);

            ParseUser friend = mUserRepository.getUsersByPosition(position);

            if (mGridView.isItemChecked(position)) {
                mUserRepository
                        .setUserRelation(
                                friend);
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                mUserRepository.removeUserRelation(friend);
                checkImageView.setVisibility(View.INVISIBLE);
            }
            //To not exhausting the main thread, after getting
            //I/Choreographer: Skipped 60 frames!  The application may be doing too much work on its main thread.
            DoInBackground doInBackground = new DoInBackground();
            doInBackground.execute();
        }
    };
}
