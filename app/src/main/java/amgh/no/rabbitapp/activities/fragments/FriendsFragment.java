package amgh.no.rabbitapp.activities.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.acthelper.ActivityHelper;
import amgh.no.rabbitapp.parse.repository.UserRepository;

public class FriendsFragment extends ListFragment {

    private static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private UserRepository mUserRepository;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserRepository = new UserRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null){
            getActivity().setProgressBarIndeterminateVisibility(true);}
        //To not exhausting the main thread,
        GetFriendsInBackground getFriendsInBackground = new GetFriendsInBackground();
        getFriendsInBackground.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseUser friend = mUserRepository.getFriendsByPosition(position);
        ActivityHelper activityHelper = new ActivityHelper(getActivity());
        Object bio = friend.get("bio");
        if (bio == null) {
            bio = "This user dos not have not a bio..";
        }
        activityHelper.showDialog(friend.getUsername(), bio.toString());


    }
    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            if (getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(false);}
            if (e == null) {
                mUserRepository.setFriends(parseUsers);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.simple_list_item_1,
                                mUserRepository.getFriendNames());
                setListAdapter(arrayAdapter);
            } else {
                Toast.makeText(getActivity(), "Problem with the backend", Toast.LENGTH_LONG).show();
            }

        }
    };

    private class GetFriendsInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mUserRepository.getFriendsFromRepository(findFriendsInBackground);
            return null;
        }
    }
}
