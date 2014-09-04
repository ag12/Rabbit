package amgh.no.rabbitapp.main;


import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.MainActivity;
import amgh.no.rabbitapp.parse.entity.Message;

public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this, "WBWyEGugb7wsDZNEwFMuJspxzkNtdjJh0FdjPbXI",
                "XcXAi0B7h73TWWJ1McqWPq9x6zQr5hbjKnXLUvfB");
        PushService.setDefaultPushCallback(this, MainActivity.class,
                R.drawable.ic_stat_ic_launcher);
    }

    public static void updateParseInstallation(Context context, ParseUser user){
       /*
        PushService.setDefaultPushCallback(context, MainActivity.class,
                R.drawable.ic_stat_ic_launcher);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(Message.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
        */
    }
}
