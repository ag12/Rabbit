package amgh.no.rabbitapp.parse.entity;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String TAG = Message.class.getSimpleName();

    public static final String KEY_USER_ID = "userId";
    public static final String CLASS_MESSAGES = "Message";
    public static final String KEY_RECIPIENT_IDS = "recipientsIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_SMS = "sms";

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_SMS = "sms";


    public Message(){

    }
    public Message(boolean onlyUser){
        if (onlyUser) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null){
                setSenderId(currentUser.getObjectId());
                setSenderName(currentUser.getUsername());
            }
        }
    }

    public Message(String senderId, String senderName,
                   ParseFile file, String fileType, ArrayList<String> recipientIds){
        setSenderId(senderId);
        setSenderName(senderName);
        setFile(file);
        setFileType(fileType);
        setRecipientIds(recipientIds);

    }

    public Message(ParseFile file, String fileType, ArrayList<String> recipientIds){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            setSenderId(currentUser.getObjectId());
            setSenderName(currentUser.getUsername());
        }
        setFile(file);
        setFileType(fileType);
        setRecipientIds(recipientIds);

    }
    public Message(String sms, String fileType, ArrayList<String> recipientIds){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            setSenderId(currentUser.getObjectId());
            setSenderName(currentUser.getUsername());
        }
        setSms(sms);
        setFileType(fileType);
        setRecipientIds(recipientIds);

    }


    public Message createFileMessage(ParseFile file, String fileType, ArrayList<String> recipientIds){
        return new Message(file,fileType,recipientIds);
    }
    public Message createSms(String sms, String fileType, ArrayList<String> recipientIds){
        return new Message(sms,fileType,recipientIds);
    }


    public void createMessage() {
        Message message = new Message();


    }
    public String getSms() {
        return getString(KEY_SMS);
    }
    public void setSms(String sms) {
        put(KEY_SMS, sms);
    }

    public String getSenderId() {
        return getString(KEY_SENDER_ID);
    }
    public void setSenderId(String senderId) {
        put(KEY_SENDER_ID, senderId);
    }

    public String getSenderName() {
        return getString(KEY_SENDER_NAME);
    }
    public void setSenderName(String name) {
        put(KEY_SENDER_NAME, name);
    }

    public ParseFile getFile(){
        return getParseFile(KEY_FILE);
    }
    public void setFile(ParseFile file) {
        put(KEY_FILE, file);
    }

    public String getFileType(){
        return getString(KEY_FILE_TYPE);
    }
    public void setFileType(String fileType) {
        put(KEY_FILE_TYPE, fileType);
    }

    public ArrayList<String> getRecipientIds(){
        return (ArrayList) getList(KEY_RECIPIENT_IDS);
    }
    public void setRecipientIds(ArrayList<String> ids){
        put(KEY_RECIPIENT_IDS, ids);
    }

    /*public void getUnReadMessages(FindCallback<Message> findCallback){
        ParseQuery<Message> query = new ParseQuery<Message>(CLASS_MESSAGES);
        query.whereEqualTo(KEY_RECIPIENT_IDS, getSenderId());
        query.addAscendingOrder(KEY_CREATED_AT);
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
    }*/
}
