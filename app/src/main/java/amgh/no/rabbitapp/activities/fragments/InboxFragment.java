package amgh.no.rabbitapp.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.recipient.ViewImageActivity;
import amgh.no.rabbitapp.activities.sms.SmsActivity;
import amgh.no.rabbitapp.adapters.MessageAdapter;
import amgh.no.rabbitapp.parse.entity.Message;
import amgh.no.rabbitapp.parse.repository.MessageRepository;

public class InboxFragment extends ListFragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = InboxFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private MessageRepository mMessageRepository;
    private MessageAdapter mMessageAdapter;
    private AlertDialog mDialog;
    private ArrayList<Message> mMessages;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageRepository = new MessageRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);
        if (mMessageRepository != null){
            GetMessagesInBackground getMessagesInBackground = new GetMessagesInBackground();
            getMessagesInBackground.execute();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Message message = mMessages.get(position);

        String fileType = message.getFileType();
        if (fileType.equals(Message.TYPE_IMAGE) || fileType.equals(Message.TYPE_VIDEO)){
            ParseFile file = message.getFile();
            Uri fileUri = Uri.parse(file.getUrl());
            if (fileType.equals(Message.TYPE_IMAGE)) {
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "video/*");
                startActivity(intent);

            }

        }  else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Message form " + message.getSenderName())
                    .setMessage(message.getSms())
                    .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent smsIntent = new Intent(getActivity(), SmsActivity.class);
                            startActivity(smsIntent);
                            mDialog.dismiss();

                        }
                    })
                    .setNegativeButton("Forget about it..", null);
            mDialog = builder.create();
            mDialog.show();

        }

        //DELETING
        boolean clean = false;
        ArrayList<String> ids = message.getRecipientIds();
        mMessages.remove(position);
        if (ids.size() == 1 ) {
            message.deleteInBackground();
            clean = true;
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idesToRemove = new ArrayList<String>();
            idesToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(Message.KEY_RECIPIENT_IDS, idesToRemove);
            message.saveInBackground();

            clean = true;
        }
        if (clean) {
            GetMessagesInBackground getMessagesInBackground = new GetMessagesInBackground();
            getMessagesInBackground.execute();
        }

    }
    private FindCallback<Message> findMessagesInBackground = new FindCallback<Message>() {
        @Override
        public void done(List<Message> messages, ParseException e) {
            if (getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(false);}
            if (e == null) {
                mMessages = (ArrayList<Message>)messages;
                if (messages.size() == 0){
                    longToast("No massages");
                } else {
                    if (mMessageAdapter == null) {
                        longToast(messages.size() + " Unread messages..");
                        mMessageAdapter = new MessageAdapter(getActivity(),
                                mMessages);
                        setListAdapter(mMessageAdapter);
                    } else {
                        //Refill
                        mMessageAdapter.refill((mMessages));
                    }

                }
            }

        }
    };

    private class GetMessagesInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mMessageRepository.getUnReadMessagesInBackground(findMessagesInBackground);
            return null;
        }
    }
    private void longToast(String s){
        Toast.makeText(getActivity(),
                s, Toast.LENGTH_LONG).show();
    }
    private void shortToast(String s){
        Toast.makeText(getActivity(),
                s, Toast.LENGTH_SHORT).show();
    }

}
