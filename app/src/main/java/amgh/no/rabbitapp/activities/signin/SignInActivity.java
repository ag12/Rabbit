package amgh.no.rabbitapp.activities.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.MainActivity;
import amgh.no.rabbitapp.activities.signup.SignUpActivity;
import amgh.no.rabbitapp.apphelper.Helper;


public class SignInActivity extends Activity {

    public final static String TAG = SignInActivity.class.getSimpleName();

    protected TextView mSignUpTextView;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;
    private Button mSingInButton;

    private LogInCallback logInCallbackListener = new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mSingInButton.setOnClickListener(singInButtonListener);
            setProgressBarIndeterminateVisibility(false);
            if (user != null) {
                Toast.makeText(SignInActivity.this,
                        "You have now successfully signed up", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Helper.showErrorDialog(SignInActivity.this, R.string.error_dialog_title,
                        R.string.login_error_message);
            }

        }
    };


    private View.OnClickListener singInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mSingInButton.getWindowToken(), 0);
            String username = Helper.trimEditTextInput(mUsername);
            String password = Helper.trimEditTextInput(mPassword);
            if (Helper.isValid(new String[]{username, password})) {
                mSingInButton.setOnClickListener(null);
                setProgressBarIndeterminateVisibility(true);
                mProgressBar.setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(username, password, logInCallbackListener);
            } else {
                Helper.showErrorDialog(SignInActivity.this, R.string.error_dialog_title,
                        R.string.error_dialog_message);

            }


        }
    };
    protected View.OnClickListener signUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signin);
        getActionBar().hide();

        mSignUpTextView = (TextView) findViewById(R.id.sign_up_text);
        mSignUpTextView.setOnClickListener(signUpOnClickListener);

        mUsername = setEditText(R.id.username);
        mPassword = setEditText(R.id.password);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSingInButton = (Button) findViewById(R.id.signin);
        mSingInButton.setOnClickListener(singInButtonListener);



    }

    private EditText setEditText(int id) {
        return (EditText) findViewById(id);
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
