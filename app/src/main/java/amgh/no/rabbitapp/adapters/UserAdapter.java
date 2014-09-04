package amgh.no.rabbitapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.apphelper.MD5Util;

public class UserAdapter extends ArrayAdapter<ParseUser> {

    public static final String TAG = UserAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ParseUser> mUsers;

    public UserAdapter(Context mContext, ArrayList<ParseUser> mUsers) {
        super(mContext, R.layout.user_item, mUsers);
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder pattern
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseUser user = mUsers.get(position);
        String email = user.getEmail();
        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            email = MD5Util.md5Hex(email.toLowerCase());
            String url = "https://s.gravatar.com/avatar/" + email + "?s=204&d=404";
            Picasso.with(mContext).load(url).placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
            Log.d(TAG, url);
        }
        holder.nameLabel.setText(user.getUsername());
        return convertView;
    }
    private static class ViewHolder {
        public ImageView userImageView;
        public TextView nameLabel;
    }
    public void refill(ArrayList<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(mUsers);
        notifyDataSetChanged();
    }


}
