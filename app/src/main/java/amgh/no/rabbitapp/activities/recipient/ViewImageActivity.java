package amgh.no.rabbitapp.activities.recipient;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import amgh.no.rabbitapp.R;

public class ViewImageActivity extends Activity {

    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Uri uri = getIntent().getData();
        Picasso.with(this).load(uri.toString()).into(mImageView);

        new Timer().schedule(timerTasks,4*1000);
    }
    private TimerTask timerTasks = new TimerTask() {
        @Override
        public void run() {
            finish();
        }
    };
}
