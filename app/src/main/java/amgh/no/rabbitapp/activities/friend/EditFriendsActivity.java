package amgh.no.rabbitapp.activities.friend;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.hepler.Helper;
import amgh.no.rabbitapp.parse.FriendRepository;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    private FindCallback<ParseUser> findInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            if (e == null) {
                //Success
                setProgressBarIndeterminateVisibility(false);
                friendRepository.setUsers(parseUsers);
                Log.i(TAG, friendRepository.getUserNames().length + "");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                        android.R.layout.simple_list_item_1, friendRepository.getUserNames());
                setListAdapter(adapter);
                Log.d(TAG, "Got friends");
            } else {
                Helper.showErrorDialog(EditFriendsActivity.this, R.string.error_dialog_title,
                        R.string.error_dialog_message_friends);
                Log.d(TAG, e.getMessage());
            }
        }
    };

    private FriendRepository friendRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
        friendRepository = new FriendRepository(this);

    }
    @Override
    protected void onResume(){
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        friendRepository.getFriendsFromRepository(findInBackground);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_friends, menu);
        return true;
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
}
