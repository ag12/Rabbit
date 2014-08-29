package amgh.no.rabbitapp.parse;


import com.parse.*;

import java.util.ArrayList;

@ParseClassName("Message")
public class Messenger extends ParseObject {

    public static final String TAG = Messenger.class.getSimpleName();

    public static final String CLASS_MESSAGES = "Messages";
    public static final String KEY_RECIPIENT_IDS = "recipientsIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_TYPE = "fileType";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";



    public Messenger(){

    }

    public Messenger(String senderId, String senderName,
                     ParseFile file, String fileType, ArrayList<String> recipientIds){
        setSenderId(senderId);
        setSenderName(senderName);
        setFile(file);
        setFileType(fileType);
        setRecipientIds(recipientIds);

    }

    public Messenger(ParseFile file, String fileType, ArrayList<String> recipientIds){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            setSenderId(currentUser.getObjectId());
            setSenderName(currentUser.getUsername());
        }
        setFile(file);
        setFileType(fileType);
        setRecipientIds(recipientIds);

    }


    public void createMessage() {
        Messenger messenger = new Messenger();


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


}
