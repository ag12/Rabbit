package amgh.no.rabbitapp.parse.repository;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import amgh.no.rabbitapp.parse.entity.Message;

public class MessageRepository {

    private String mId;
    public MessageRepository(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            mId = currentUser.getObjectId();
        }
    }

    public void getUnReadMessagesInBackground(FindCallback<Message> findCallback){
        ParseQuery<Message> query = new ParseQuery<Message>(Message.CLASS_MESSAGES);
        query.whereEqualTo(Message.KEY_RECIPIENT_IDS, mId);
        query.addAscendingOrder(Message.KEY_CREATED_AT);
        query.findInBackground(findCallback);
    }
    public String[] getSenderUserNames(List<Message> messages){
        String[] senders = new String[messages.size()];
        int i = 0;
        for(Message message: messages){
            senders[i] = message.getSenderName();
            i++;
        }
        return senders;
    }
}
