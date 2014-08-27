package amgh.no.rabbitapp.hepler;


import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

public class Helper {

    public static boolean isValid(String[] s) {
        if (s == null) {
            return  false;
        }
        for(int i = 0; i < s.length; i++){
            if (!isValid(s[i])){
                return false;
            }
        }
        return true;
    }
    public static Boolean isValid(String s) {
        if (s == null) {
            return false;
        }
        else if(s.isEmpty() || s.length() == 0 || s.equals(null)) {
            return false;
        }
        return true;
    }

    public static void showErrorDialog(Context context,int title, int message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(title))
                .setMessage(context.getString(message))
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
    public static String trimEditTextInput(EditText editText) {
        return editText.getText().toString().trim();
    }
}