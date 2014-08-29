package amgh.no.rabbitapp.parse;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class UserRepository {

    public static final String TAG = UserRepository.class.getSimpleName();
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";
    public static final int LIMIT = 1000;

    private List<ParseUser> mUsers;
    private List<ParseUser> mFriends;

    private ParseRelation<ParseUser> mUserRelation;

    private ParseUser mCurrentUSer;

    public UserRepository() {
        mCurrentUSer = ParseUser.getCurrentUser();
        if (mCurrentUSer != null) {
            mUserRelation = mCurrentUSer.getRelation(KEY_FRIENDS_RELATION);
        }
    }

    public List<ParseUser> getFriends() {
        return mFriends;
    }

    public void setFriends(List<ParseUser> mFriends) {
        this.mFriends = mFriends;
    }

    public ParseUser getCurrentUser() {
        return mCurrentUSer;
    }
    public void setUsers(List<ParseUser> mUsers) {
        this.mUsers = mUsers;
    }
    public List<ParseUser> getUsers() {return mUsers;}
    public ParseUser getUsersByPosition(int pos) {
        return getByPosition(mUsers, pos);
    }
    public ParseUser getFriendsByPosition(int pos) {
        return getByPosition(mFriends, pos);
    }

    private ParseUser getByPosition(List<ParseUser> parseUsers, int position){
        return parseUsers.get(position);
    }

    public String[] getUserNames() {
        return createUsernameArray(mUsers);
    }
    public String[] getFriendNames() {
        return createUsernameArray(mFriends);
    }
    private String[] createUsernameArray(List<ParseUser> parseUsers){
        String[] userNames = new String[parseUsers.size()];
        int i = 0;
        for(ParseUser user: parseUsers) {
            userNames[i] = user.getUsername();
            i++;
        }
        return userNames;
    }
    public void getUsersFromRepository(FindCallback<ParseUser> findInBackground){

        ParseQuery<ParseUser> query =
                ParseUser.getQuery();
        query.orderByAscending(KEY_USERNAME);
        query.setLimit(LIMIT);
        query.whereNotEqualTo(KEY_USERNAME, mCurrentUSer.getUsername());
        query.findInBackground(findInBackground);
    }


    public ParseRelation<ParseUser> getUserRelation() {
        return mUserRelation;
    }
    public void getFriendsFromRepository(FindCallback<ParseUser> findCallback) {
        ParseQuery<ParseUser> query = this.mUserRelation.getQuery();
        query.orderByAscending(KEY_USERNAME);
        query.findInBackground(findCallback);
    }
    public void setUserRelation(ParseUser user) {
        this.mUserRelation.add(user);

    }
    public void saveUser(SaveCallback saveCallback){
        this.mCurrentUSer.saveInBackground(saveCallback);
    }
    public void removeUserRelation(ParseUser friend){
        this.mUserRelation.remove(friend);
    }
}
