package com.barmej.astronomypicture;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.barmej.astronomypicture.Utiles.OpenAstronomyDataParser;
import com.barmej.astronomypicture.entity.Astronomy;
import com.barmej.astronomypicture.fragments.DataPakerDialogFragment;
import com.barmej.astronomypicture.networks.NetworkUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements DataPakerDialogFragment.ButtonSheetListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final String TAG_OF_DAILOG_FRAGMENT = "dialog fragment";

    private WebView mWebView;
    private ImageView mImageView;
    private TextView titleTextView, explanationTextView;
    private View mNestedScrollView;

    private NetworkUtils mNetworkUtils;
    private Astronomy mAstronomy;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNestedScrollView = findViewById(R.id.nested_scroll_view);
        BottomSheetBehavior.from(mNestedScrollView);

        mImageView = findViewById(R.id.image_view);
        titleTextView = findViewById(R.id.title_text_view);
        explanationTextView = findViewById(R.id.explanation_text_view);


        mWebView = findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        mNetworkUtils = NetworkUtils.getInstance(this);

        requestAstronomyInfo();
    }


    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mNestedScrollView.setVisibility(View.VISIBLE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mNestedScrollView.setVisibility(View.INVISIBLE);
            hideSystemBars();

        }

    }//end of onConfigurationChanged

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

    }//end of hideSystemBars

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onDonwloadURL();

        }else{
                Toast.makeText(MainActivity.this,R.string.we_need_permission_to_download,Toast.LENGTH_SHORT).show();

        }
    }//end of onRequestPermissionsResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }//end of onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.date_picker:
                onShowDataPakerDialog();
                break;
            case R.id.donwloadHdPhoto:
                    onDonwloadURL();
                break;
            case R.id.share_url:
                onShareUri();
                break;
            case R.id.about_us:
                onGoTOAboutUsActivity();
                break;
        }
        return super.onOptionsItemSelected(item);

    }//end of onOptionsItemSelected

    private void onShowDataPakerDialog() {
        DataPakerDialogFragment dataPakerDialogFragment = new DataPakerDialogFragment();
        dataPakerDialogFragment.show(getSupportFragmentManager(), TAG_OF_DAILOG_FRAGMENT);

    }//end of onShowDataPakerDialog

    private void onGoTOAboutUsActivity() {
        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));

    }//end of onGoTOAboutUsActivity

    private void onShareUri() {
        if(mAstronomy != null) {
            if (mAstronomy.getMediaType().equals("image")){
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            URL imageUrl = new URL(mAstronomy.getHdurl().toString());
                            Bitmap image = BitmapFactory.decodeStream(imageUrl.openStream());
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
                            Uri imageUri =  Uri.parse(path);
                            share.setType("image/*");
                            share.putExtra(Intent.EXTRA_STREAM, imageUri);
                            share.putExtra(Intent.EXTRA_TEXT, mAstronomy.getTitle());
                            startActivity(Intent.createChooser(share, "Select"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }else if (mAstronomy != null){
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/*");
                share.putExtra(Intent.EXTRA_TEXT, mAstronomy.getUri().toString());
                startActivity(Intent.createChooser(share, "Select"));
            }
        }else {
            Toast.makeText(MainActivity.this, R.string.there_are_no_network, Toast.LENGTH_SHORT).show();
        }
    }//end of onShareUri

    private void onDonwloadURL(){
        if(mAstronomy != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mAstronomy.getHdurl().toString()));
                String title = URLUtil.guessFileName(mAstronomy.getHdurl().toString(), null, null);
                request.setTitle(title);
                request.setDescription(getString(R.string.Download_File_please_wait));
                String cookie = CookieManager.getInstance().getCookie(mAstronomy.getHdurl().toString());
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
                Toast.makeText(MainActivity.this, getString(R.string.download_start), Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,WRITE_EXTERNAL_STORAGE_CODE);
            }

        }else {
                Toast.makeText(MainActivity.this, R.string.there_are_no_network, Toast.LENGTH_SHORT).show();
        }
    }//end of onDonwloadURL

    @Override
    public void onButtonCilcked(String date) {
        requestAstronomyInfo(date);

    }//end of onButtonCilcked

    private void requestAstronomyInfo() {
        // The getForecastsUrl method will return the URL that we need to get the JSON for the upcoming forecasts
        String astronomyRequestUrl = NetworkUtils.getUrl(MainActivity.this).toString();

        // Request a string response from the provided URL.
        JsonObjectRequest astronomyListRequest = new JsonObjectRequest(Request.Method.GET, astronomyRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Astronomy astronomy = null;

                        try {
                            // Get ForecastLists object from json response

                            astronomy = OpenAstronomyDataParser.getAstronomyInfoObjectFromJson(response);

                        } catch (JSONException | MalformedURLException | URISyntaxException e) {
                            e.printStackTrace();
                        }

                        if (astronomy != null) {
                            onDataShow(astronomy);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        astronomyListRequest.setTag(TAG);

        mNetworkUtils.addToRequestQueue(astronomyListRequest);

    }//end of requestAstronomyInfo

    private void requestAstronomyInfo(String date) {
        String astronomyRequestUrl = NetworkUtils.getUrl(MainActivity.this, date).toString();

        JsonObjectRequest astronomyListRequest = new JsonObjectRequest(Request.Method.GET, astronomyRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Astronomy astronomy = null;
                        try {
                            astronomy = OpenAstronomyDataParser.getAstronomyInfoObjectFromJson(response);
                        } catch (JSONException | MalformedURLException | URISyntaxException e) {
                            e.printStackTrace();
                        }

                        if (astronomy != null) {
                            onDataShow(astronomy);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        astronomyListRequest.setTag(TAG);

        mNetworkUtils.addToRequestQueue(astronomyListRequest);

    }//end of requestAstronomyInfo(data)

    private void onDataShow(Astronomy astronomy) {
        mAstronomy = astronomy;
        if(astronomy.getMediaType().equals("image")){
            Glide.with(MainActivity.this)
                    .load(astronomy.getUri())
                    .into(mImageView);

            mWebView.setVisibility(View.INVISIBLE);
            mWebView.reload();
            mWebView.stopLoading();

            titleTextView.setText(mAstronomy.getTitle());
            explanationTextView.setText(mAstronomy.getExplanation());

            mImageView.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            mMenu.findItem(R.id.donwloadHdPhoto).setVisible(true);
        }else {
            mWebView.loadUrl(mAstronomy.getUri().toString());
            titleTextView.setText(mAstronomy.getTitle());
            explanationTextView.setText(mAstronomy.getExplanation());

            mWebView.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);

            mImageView.setVisibility(View.INVISIBLE);
            mMenu.findItem(R.id.donwloadHdPhoto).setVisible(false);

        }

    }//end of onDataShow

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkUtils.cancelRequests(TAG);
        mWebView.destroy();
    }//end of onStop


}//end of MainActivity