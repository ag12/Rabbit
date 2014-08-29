To run you have to create this class, using your parse client and server ids

package amgh.no.rabbit;

import android.app.Application;

import com.parse.Parse;

public class RabbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Messenger.class);
        //These values are provided by parse.com
        String clientid = "xxx";
        String serverid = "xxx";
        Parse.initialize(this, clientid,
                serverid);


    }
}
