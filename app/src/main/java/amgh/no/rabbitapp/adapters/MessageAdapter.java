package amgh.no.rabbitapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Date;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.parse.entity.Message;

public class MessageAdapter extends ArrayAdapter<Message> {

    public static final String TAG = MessageAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Message> mMassages;

    public MessageAdapter(Context mContext, ArrayList<Message> mMessages) {
        super(mContext, R.layout.message_item, mMessages);
        this.mContext = mContext;
        this.mMassages = mMessages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder pattern
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.message_icon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.message_label);
            holder.dateLabel = (TextView) convertView.findViewById(R.id.message_date);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Message message = mMassages.get(position);

        if (message.getFileType().equals(Message.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
        } else if (message.getFileType().equals(Message.TYPE_VIDEO)) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_action_chat);
        }
        holder.nameLabel.setText(message.getSenderName());
        holder.dateLabel.setText(getTime(message.getCreatedAt()));
        return convertView;
    }

    private String getTime(Date sendDate){

        DateTime sendDateTime = new DateTime(sendDate);
        Period p = new Period(sendDateTime, new DateTime());
        int hour = p.getHours();
        int min = p.getMinutes();
        String s = "";
        if (hour > 0) {
            s += hour;
            if (min > 0) {
                s  += " hours ago";
                return s;
            }
        } else {
            if (min > 0) {
                s += min + " minutes ago";
                return s;
            } else {
                s = "Now";
            }
        }
        return s;
    }
    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView nameLabel;
        public TextView dateLabel;
    }
    public void refill(ArrayList<Message> messages) {
        mMassages.clear();
        mMassages.addAll(messages);
        notifyDataSetChanged();
    }

}
