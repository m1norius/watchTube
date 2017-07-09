package com.minorius.watchertube;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends YouTubeBaseActivity {

    private String videoUrl;

    private final YouTubeExtractor mExtractor = YouTubeExtractor.create();


    private Callback<YouTubeExtractionResult> mExtractionCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            bindVideoResult(response.body());
        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            onError(t);
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        YouTubePlayerView playerFrame = (YouTubePlayerView) findViewById(R.id.id_player);
        VideoView videoView = (VideoView) findViewById(R.id.id_mediaPlayer);

        videoUrl = (String) getIntent().getExtras().get("VIDEO_URL");

        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (videoUrl != null) {
                    youTubePlayer.loadVideo(videoUrl);
                    youTubePlayer.setFullscreen(true);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        if (isVideoLoaded(getApplicationContext(), videoUrl+".mp4")){
            Toast.makeText(getApplicationContext(), "Video from buffer", Toast.LENGTH_SHORT).show();
            playerFrame.setVisibility(View.GONE);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
            String path = new File(directory, videoUrl+".mp4").getAbsolutePath();

            videoView.setVideoURI(Uri.parse(path));
            videoView.start();

        }else {
            playerFrame.initialize(ContentActivity.KEY, onInitializedListener);
            mExtractor.extract(videoUrl).enqueue(mExtractionCallback);
        }

    }
    private void onError(Throwable t) {
        t.printStackTrace();
        Toast.makeText(getApplicationContext(), "It failed to extract. So sad", Toast.LENGTH_SHORT).show();
    }


    private void bindVideoResult(final YouTubeExtractionResult result) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (result.getSd360VideoUri() != null){
                    downloadFile(result.getSd360VideoUri().toString(), videoUrl);
                }
            }
        }).start();
    }

    private void downloadFile(String fileURL, String fileName) {

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);

            directory.mkdir();

            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(directory, fileName+".mp4"));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }
    }

    public boolean isVideoLoaded(Context context, String fileName){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        return new File(directory, fileName).exists();
    }
}
