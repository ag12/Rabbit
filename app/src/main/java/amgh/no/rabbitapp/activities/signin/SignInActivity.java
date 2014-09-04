package amgh.no.rabbitapp.activities.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import amgh.no.rabbitapp.main.RibbitApplication;


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
                RibbitApplication.updateParseInstallation(SignInActivity.this, user);
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
}
