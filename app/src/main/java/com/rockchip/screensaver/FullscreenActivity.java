package com.rockchip.screensaver;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Process;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    //private String imagePath[];
    List<String> imagelist= new ArrayList<>();

    private AdapterViewFlipper flipper;
    private boolean mVisible;

    private Vector<String>vecjpg;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    public static Vector<String>GetJpgFileName(String fileAbsolutePath){
        Vector<String>vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        for(int iFileLength = 0;iFileLength<subFile.length;iFileLength++){
            if(!subFile[iFileLength].isDirectory()){
                String filename = subFile[iFileLength].getName();
                if(filename.trim().toLowerCase().endsWith(".jpg")){
                    Log.i(">>>GetJpgFileName<<<","Filename "+iFileLength+" is "+filename);
                    vecFile.add(filename);
                }
            }
        }
        return vecFile;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        vecjpg = new Vector<String>();
        vecjpg = GetJpgFileName("/sdcard/wallpaper");
        final int sizejpg = vecjpg.size();
        for(Iterator<String>iter = vecjpg.iterator();iter.hasNext();){
            String value= iter.next();
            imagelist.add("/sdcard/wallpaper/"+value);

            //imagePath[i]="/sdcard/wallpaper/"+value;

            Log.i(">>>GetJpg name<<<","vecjpg name is "+value+" size is"+sizejpg);
        }

        hide();
        flipper = (AdapterViewFlipper) findViewById(R.id.flipper);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sizejpg;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ImageView imageView = new ImageView(FullscreenActivity.this);
               //Bitmap bmp = readBitmapFromAssetsFile(imagePath[position]);
                Bitmap bmp = readBitmapFromAssetsFile(imagelist.get(position));
                Log.i("Position ScreenSaver<<<","now position is "+position);
                //Bitmap bmp = readBitmapFromAssetsFile("/sdcard/wallpaper/logo.bmp");
                imageView.setImageBitmap(bmp);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        };
        Start(adapter);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quit();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Quit();
        return true;
    }

    public void Start(BaseAdapter adapter) {
        flipper.setAdapter(adapter);
        flipper.getLayoutAnimation();
        flipper.setInAnimation(this, R.animator.alpha_in);
        flipper.setOutAnimation(this, R.animator.alpha_out);
        flipper.startFlipping();
    }

    private void Quit() {
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = true;
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    public static Bitmap readBitmapFromInputStream(InputStream ins, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ins, null, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / height);
            } else {
                inSampleSize = Math.round(srcWidth / width);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeStream(ins, null, options);
    }

    public Bitmap readBitmapFromAssetsFile(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
//        FileInputStream stream = null;
//        try {
//            stream = new FileInputStream(filePath);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        BitmapFactory.decodeStream(stream,null,options);
//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int windowHeight = wm.getDefaultDisplay().getHeight();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int scaleX = imageWidth / windowWidth;
        int scaleY = imageHeight / windowHeight;
        int scale = 1;

        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleX < scaleY && scaleX >= 1) {
            scale = scaleY;
        }
        Bitmap image = null;
        try {
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.outHeight = 720;
            options.outWidth = 1280;
            // InputStream is = getResources().getAssets().open(filePath);
            //File wallpaper = new File(filePath);
            //if(!wallpaper.exists())wallpaper.createNewFile();
            FileInputStream is = new FileInputStream(filePath);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            image = BitmapFactory.decodeStream(is,null,options);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fullscreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.rockchip.screensaver/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fullscreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.rockchip.screensaver/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}