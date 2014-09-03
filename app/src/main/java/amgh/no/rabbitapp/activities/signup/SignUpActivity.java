package amgh.no.rabbitapp.activities.signup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import amgh.no.rabbitapp.activities.MainActivity;
import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.apphelper.Helper;

public class SignUpActivity extends Activity {

    public final static String TAG = SignUpActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    private EditText mEmail;
    private EditText mBio;
    protected Button mSignUpButton;
    protected Button mCancelButton;
    private ProgressBar mProgressBar;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signup);
        getActionBar().hide();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mBio = (EditText) findViewById(R.id.bio);
        mEmail = (EditText) findViewById(R.id.email);

        mSignUpButton = (Button) findViewById(R.id.signup);
        mSignUpButton.setOnClickListener(signUpUserListener);

        mCancelButton = (Button) findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(cancelListener);


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

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    private SignUpCallback signUpCallback = new SignUpCallback() {

        @Override
        public void done(ParseException e) {
            mProgressBar.setVisibility(View.INVISIBLE);
            setProgressBarIndeterminateVisibility(false);
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
                        R.string.sign_up_error_message);
            }
        }
    };

    private View.OnClickListener signUpUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mSignUpButton.getWindowToken(), 0);
            String username = Helper.trimEditTextInput(mUsername);
            String password = Helper.trimEditTextInput(mPassword);
            String email = Helper.trimEditTextInput(mEmail);
            String bio = Helper.trimEditTextInput(mBio);
            String[] strings = {username, password, email};
            if (!Helper.isValid(strings)) {
                Helper.showErrorDialog(SignUpActivity.this,R.string.error_dialog_title,
                        R.string.error_dialog_message);
            } else {
                //Create a new user.
                setProgressBarIndeterminateVisibility(true);
                ParseUser newUser = new ParseUser();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);
                newUser.put("bio", bio);
                mProgressBar.setVisibility(View.VISIBLE);
                newUser.signUpInBackground(signUpCallback);
            }
        }
    };
}
