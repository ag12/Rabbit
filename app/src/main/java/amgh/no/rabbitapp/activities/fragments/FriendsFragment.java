package amgh.no.rabbitapp.activities.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.adapters.UserAdapter;
import amgh.no.rabbitapp.parse.repository.UserRepository;

public class FriendsFragment extends Fragment {

    private static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private UserRepository mUserRepository;
    private GridView mGridView;
    private UserAdapter mUserAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mUserRepository = new UserRepository();
        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);
        TextView empty = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(empty);
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

    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        l.onListItemClick(l, v, position, id);
        ParseUser friend = mUserRepository.getFriendsByPosition(position);
        ActivityHelper activityHelper = new ActivityHelper(getActivity());
        Object bio = friend.get("bio");
        if (bio == null) {
            bio = "This user dos not have not a bio..";
        }
        activityHelper.showDialog(friend.getUsername(), bio.toString());
    }*/
    private FindCallback<ParseUser> findFriendsInBackground = new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            if (getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(false);}
            if (e == null) {
                mUserRepository.setFriends(parseUsers);
                if (mGridView.getAdapter() == null) {
                    mUserAdapter =
                            new UserAdapter(getActivity(),
                                    (ArrayList<ParseUser>)mUserRepository.getFriends());
                    mGridView.setAdapter(mUserAdapter);
                } else {
                    mUserAdapter.refill((ArrayList<ParseUser>) mUserRepository.getFriends());
                }

            } else {
                Toast.makeText(getActivity(), "Problem with the backend",
                        Toast.LENGTH_LONG).show();
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
