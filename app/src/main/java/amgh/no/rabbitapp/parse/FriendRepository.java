package amgh.no.rabbitapp.parse;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class FriendRepository {

    /*
    Experimenting: could have the getFriendsFromRepository method inside EditFriendsActivity as well

     */
    private Context mContext;
    private List<ParseUser> mUsers;

    public void setUsers(List<ParseUser> mUsers) {
        this.mUsers = mUsers;
    }

    public FriendRepository(Context mContext) {
        this.mContext = mContext;
    }
    public static final String TAG = FriendRepository.class.getSimpleName();

    public FindCallback<ParseUser> getFriendsFromRepository(FindCallback<ParseUser> findInBackground){

        /*ParseQuery<ParseUser> query =
                ParseQuery.getQuery(ParseConstant.REPOSITORY_USER);*/
        ParseQuery<ParseUser> query =
                ParseUser.getQuery();
        query.orderByAscending(ParseConstant.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(findInBackground);
        return findInBackground;
    }

    public List<ParseUser> getUsers() {
        return mUsers;
    }

    public String[] getUserNames() {
        String[] userNames = new String[mUsers.size()];
        int i = 0;
        for(ParseUser user: mUsers) {
            userNames[i] = user.getUsername();
            i++;
        }
        return userNames;
    }

}
