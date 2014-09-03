package amgh.no.rabbitapp.activities.sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.recipient.RecipientActivity;
import amgh.no.rabbitapp.parse.entity.Message;

public class SmsActivity extends Activity {

    private EditText mEditText;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mEditText = (EditText) findViewById(R.id.sms_txt);
        mButton = (Button) findViewById(R.id.sms_btn);
        mButton.setOnClickListener(mButtonListener);
    }
    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sms = mEditText.getText().toString();
            if (sms.length() > 0){
                Intent recipientIntent = new Intent(SmsActivity.this, RecipientActivity.class);
                recipientIntent.putExtra(Message.KEY_FILE_TYPE, Message.TYPE_SMS);
                recipientIntent.putExtra(Message.TYPE_SMS, sms);
                startActivity(recipientIntent);
            }
        }
    };

}
