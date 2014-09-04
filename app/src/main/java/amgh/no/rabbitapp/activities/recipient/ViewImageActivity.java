package amgh.no.rabbitapp.activities.recipient;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(getIntent().getData().toString()).into(mImageView);

        new Timer().schedule(timerTasks,5*1000);
    }
    private TimerTask timerTasks = new TimerTask() {
        @Override
        public void run() {
            finish();
        }
    };
}
