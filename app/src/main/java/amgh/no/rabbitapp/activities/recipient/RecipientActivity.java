package amgh.no.rabbitapp.activities.recipient;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.MainActivity;
import amgh.no.rabbitapp.parse.entity.Message;
import amgh.no.rabbitapp.parse.repository.UserRepository;
import amgh.no.rabbitapp.treehouse.FileHelper;

public class RecipientActivity extends ListActivity {

    private static final String TAG = RecipientActivity.class.getSimpleName();
    private UserRepository mUserRepository;
    private MenuItem mSendMenuItem;
    private Uri mMediaUri;
    private String mFileType;
    private String mSms = null;
    private Message mMessage;
    private ProgressBar mProgressBar;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipient);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mUserRepository = new UserRepository();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent != null) {
            mMediaUri = intent.getData();
            mFileType = intent.getExtras().getString(Message.KEY_FILE_TYPE);
            mSms = intent.getExtras().getString(Message.TYPE_SMS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        mUserRepository.getFriendsFromRepository(findFriendsInBackground);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipient, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send) {
            getListView().setVisibility(View.INVISIBLE);
            mSendMenuItem.setVisible(false);
            mProgressBar.setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(true);
            mMessage = createMessage();
            if (mMessage != null) {
                DoInBackground doInBackground = new DoInBackground();
                doInBackground.execute();
            } else {
                showErrorAlert(getString(R.string.error_dialog_select_button), dialogListener);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        boolean leastOne = l.getCheckedItemCount() > 0;
        if (leastOne) {
            mSendMenuItem.setVisible(true);
        } else {
            mSendMenuItem.setVisible(false);
        }
        if (l.isItemChecked(position)){
            addNameToBar(position);
        } else {
            removeNameFromBar(position, leastOne);
        }


    }
    private void showErrorAlert(String s, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title_some_dialog))
                .setMessage(s)
                .setPositiveButton(android.R.string.ok, listener);
                mDialog = builder.create();
                mDialog.show();

    }
    private void sendMessage(Message message){

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    Toast.makeText(
                            RecipientActivity.this, "Success",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    showErrorAlert(getString(R.string.error_dialog_send_message), null);
                }
                if (mSms != null){
                    Intent intent = new Intent(RecipientActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }

            }
        });
    }
    private Message createMessage() {

        Message message = null;
        if (mSms != null) {
            //it is a sms
            message = new Message();
            message = message.createSms(mSms,
                    mFileType, getRecipientIds());
            return message;
        }
        ParseFile file = null;
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,
                mMediaUri);
        if (fileBytes == null) {
            //return null
        }
        else {
            if (mFileType.equals(Message.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            try{
                file = new ParseFile(fileName, fileBytes);
            } catch (Exception e) {
                Toast.makeText(this, "Oooobs bad file", Toast.LENGTH_LONG).show();
            }

        }
        if (file != null){
            message = new Message();
            message = message.createFileMessage(file,
                    mFileType, getRecipientIds());

        }
        return message;
    }
    private void addNameToBar(int position) {

        String s = getTitle().toString();
        boolean firstName = false;
        if (s.equals(getResources().getString(R.string.title_activity_recipient))) {
            s = "";
            firstName = true;
        }
        s += (firstName == true ?  "" :  ", ") +
                mUserRepository.getFriendsByPosition(position).getUsername();
        setTitle(s);
    }
    private void removeNameFromBar(int position, boolean hasName) {
        if (!hasName) {
            setTitle(getResources().getString(R.string.title_activity_recipient));
            return;
        }
        String s = getTitle().toString();
        s = s.replace(", " + mUserRepository.getFriendsByPosition(position).getUsername(), "");
        setTitle(s);
    }

    private ArrayList<String> getRecipientIds(){
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < getListView().getCount(); i++){
            if (getListView().isItemChecked(i)) {
                ids.add(mUserRepository.getFriendsByPosition(i).getObjectId());
            }
        }
        return ids;
    }

    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            setProgressBarIndeterminateVisibility(false);
            if (e == null) {
                mUserRepository.setFriends(parseUsers);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (RecipientActivity.this, android.R.layout.simple_list_item_checked,
                                mUserRepository.getFriendNames());
                setListAdapter(arrayAdapter);
            } else {
                Log.d(TAG, e.getMessage());
            }

        }
    };
    private class DoInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            sendMessage(mMessage);
            return null;
        }
    }
    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mDialog.dismiss();
            finish();
        }
    };
}
