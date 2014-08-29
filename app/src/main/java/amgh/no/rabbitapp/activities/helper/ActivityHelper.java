package amgh.no.rabbitapp.activities.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class ActivityHelper {

    public ActivityHelper(Context mContext){
        this.mContext = mContext;
    }
    private Context mContext;
    public void logToast(String s){
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
    public void shortToast(String s){
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
    public void showDialogFromResource(int title, int message) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(title))
                .setMessage(mContext.getString(message))
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
    public void showDialog(String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
}
