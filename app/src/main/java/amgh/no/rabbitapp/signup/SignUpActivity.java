package amgh.no.rabbitapp.signup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SignUpCallback;

import amgh.no.rabbitapp.MainActivity;
import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.hepler.Helper;
import amgh.no.rabbitapp.user.User;

public class SignUpActivity extends Activity {

    public final static String TAG = SignUpActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    private EditText mEmail;
    protected Button mSignUpButton;
    private ProgressBar mProgressBar;
    private AlertDialog mAlertDialog;

    protected SignUpCallback signUpCallback = new SignUpCallback() {

        @Override
        public void done(ParseException e) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (e == null) {
                Log.v(TAG, "Registered user");
                Toast.makeText(SignUpActivity.this,
                        "You have now successfully signed up", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Helper.showErrorDialog(SignUpActivity.this, R.string.reg_dialog_title,
                        R.string.error_dialog_message);
                Log.v(TAG, getString(R.string.reg_dialog_title));
                Log.v(TAG, e.getMessage());
            }
        }
    };

    protected View.OnClickListener signUpUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mSignUpButton.getWindowToken(), 0);
            String username = Helper.trimEditTextInput(mUsername);
            String password = Helper.trimEditTextInput(mPassword);
            String email = Helper.trimEditTextInput(mEmail);
            String[] strings = {username, password, email};
            if (!Helper.isValid(strings)) {
                Helper.showErrorDialog(SignUpActivity.this,R.string.error_dialog_title,
                        R.string.error_dialog_message);
            } else {
                //Create a new user.
                User newUser = new User(username, password, email);
                mProgressBar.setVisibility(View.VISIBLE);
                newUser.signUpInBackground(signUpCallback);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);

        mSignUpButton = (Button) findViewById(R.id.signup);
        mSignUpButton.setOnClickListener(signUpUserListener);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
