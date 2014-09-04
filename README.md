Android experimental app
To run you have to create this class, using your parse client and server ids

package amgh.no.rabbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Messenger.class);
        //These values are provided by parse.com
        String clientid = "xxx";
        String serverid = "xxx";
        Parse.initialize(this, clientid,
                serverid);

        //Push notification
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    //Push notification
    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(Message.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
