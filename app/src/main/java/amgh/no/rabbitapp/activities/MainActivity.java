package amgh.no.rabbitapp.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import amgh.no.rabbitapp.R;
import amgh.no.rabbitapp.activities.friend.EditFriendsActivity;
import amgh.no.rabbitapp.activities.recipient.RecipientActivity;
import amgh.no.rabbitapp.activities.signin.SignInActivity;
import amgh.no.rabbitapp.activities.sms.SmsActivity;
import amgh.no.rabbitapp.adapters.SectionsPagerAdapter;
import amgh.no.rabbitapp.parse.entity.Message;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public final static String TAG = MainActivity.class.getSimpleName();
    private final static int TAKE_PHOTO_REQUEST = 0;
    private final static int TAKE_VIDEO_REQUEST = 1;
    private final static int PIC_PHOTO_REQUEST = 2;
    private final static int PIC_VIDEO_REQUEST = 3;

    private final static int MEDIA_TYPE_IMAGE = 5;
    private final static int MEDIA_TYPE_VIDEO = 6;

    private final static int VIDEO_SIZE_LIMIT = 1024*1024*10; // 10MB

    private Uri mMediaUri;
    private AlertDialog mDialog;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        Log.i(TAG, getIntent() == null ? "null" : "IKKE");
        ParseAnalytics.trackAppOpened(getIntent());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            /*.setText(mSectionsPagerAdapter.getPageTitle(i))*/
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));
        }

        if (ParseUser.getCurrentUser() == null) {
            navigateToLogIn();
        } else {
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Log.i(TAG, "settinga");
                return true;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogIn();
                Log.i(TAG, "logut");
                return true;
            case R.id.action_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                Log.i(TAG, "friend");
                return true;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(getResources().getStringArray(R.array.camera_choices),
                        mDialogOnClickListener);
                mDialog = builder.create();
                mDialog.show();
                Log.i(TAG, "Create");
                return true;
            case R.id.action_sms:
                requestSms();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult ok");
            Log.i(TAG, data == null ? "Data er null" : "Data er ikke null");
            if (requestCode == TAKE_PHOTO_REQUEST || requestCode == TAKE_VIDEO_REQUEST) {
                Log.i(TAG, "onActivityResult ok + CREATE video or picture");
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                Log.i(TAG, mMediaUri.getPath());
                this.sendBroadcast(mediaScanIntent);
            } else if (requestCode == PIC_PHOTO_REQUEST || requestCode == PIC_VIDEO_REQUEST) {
                Log.i(TAG, "onActivityResult ok + SELECT video or picture");
                if (data == null) {
                    Log.i(TAG, "onActivityResult ok + SELECT video or picture but data was null");
                    longToast(getString(R.string.error_dialog_title));
                } else {
                    mMediaUri = data.getData();
                    Log.i(TAG, "onActivityResult ok + SELECT video or picture data not null");

                }
                Log.i(TAG, "Media uri" + mMediaUri);
                if (requestCode == PIC_VIDEO_REQUEST) {
                    //Check the size
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();

                    } catch (FileNotFoundException e) {
                        longToast(getString(R.string.toast_error_opening_file));
                        Log.i(TAG, "FileNotFoundException " + e.getMessage());

                    } catch (IOException e) {
                        longToast(getString(R.string.toast_error_opening_file));
                        Log.i(TAG, "IOException " + e.getMessage());
                    } finally {
                        try {
                            inputStream.close();
                        }catch (IOException e){
                            Log.i(TAG, "IOException closing inputStream" + e.getMessage());
                        }
                    }
                    if (fileSize >= VIDEO_SIZE_LIMIT) {
                        longToast(getString(R.string.toast_file_size_large));
                    }
                }
            }
            Intent recipientIntent = new Intent(this, RecipientActivity.class);
            recipientIntent.setData(mMediaUri);
            if (requestCode == PIC_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
                recipientIntent.putExtra(Message.KEY_FILE_TYPE, Message.TYPE_IMAGE);
            } else {
                recipientIntent.putExtra(Message.KEY_FILE_TYPE, Message.TYPE_VIDEO);
            }
            startActivity(recipientIntent);

            Log.i(TAG, "Alltid");
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult cancel");
        } else if (resultCode == RESULT_FIRST_USER) {
            Log.i(TAG, "onActivityResult first user");
        }
    }

    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            //get the URI

            //1. Get the external storage directory
            String appName = getResources().getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES),
                    appName);
            //2. Create our sub-directory

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.i(TAG, "Failed to create directory");
                    return null;

                }
            }
            //3. Create a file name
            File mediaFile;
            String timestamp = new
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String path = mediaStorageDir.getPath() + File.separator;
            //4. Create the file
            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                    break;
                case MEDIA_TYPE_VIDEO:
                    mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                    break;
                default:
                    return null;
            }
            Log.i(TAG, mediaFile.getAbsolutePath());
            return Uri.fromFile(mediaFile);
            //5. Return the files UR
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private void requestCameraForPicture() {
        Intent photoIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            longToast(getString(R.string.toast_error_external_storage));
        } else {
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(photoIntent, TAKE_PHOTO_REQUEST);
        }
    }

    private void requestCameraForVideo() {
        Intent videoIntent = new Intent(
                MediaStore.ACTION_VIDEO_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            longToast(getString(R.string.toast_error_external_storage));
        } else {
            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            //Setting limit and quality constants
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
        }
    }

    private void requestPicture(){
        Intent selectPicture = new Intent(Intent.ACTION_GET_CONTENT);
        selectPicture.setType("image/*");
        startActivityForResult(selectPicture, PIC_PHOTO_REQUEST);
    }
    private void requestVideo() {
        Intent selectVideo = new Intent(Intent.ACTION_GET_CONTENT);
        selectVideo.setType("video/*");
        longToast(getString(R.string.toast_video_file_warning));
        startActivityForResult(selectVideo, PIC_VIDEO_REQUEST);
    }
    private void requestSms(){
        Intent smsIntent = new Intent(this, SmsActivity.class);
        startActivity(smsIntent);
    }

    private DialogInterface.OnClickListener mDialogOnClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //Take picture
                            requestCameraForPicture();
                            break;
                        case 1:
                            //Take video
                            requestCameraForVideo();
                            break;
                        case 2:
                            //Choose picture
                            requestPicture();
                            break;
                        case 3:
                            //choose video
                            requestVideo();
                            break;
                        default:
                            break;

                    }
                    mDialog.dismiss();
                }

            };
    private void longToast(String s){
        Toast.makeText(this,
                s, Toast.LENGTH_LONG).show();
    }
    private void shortToast(String s){
        Toast.makeText(this,
                s, Toast.LENGTH_SHORT).show();
    }
    private void navigateToLogIn() {

        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
