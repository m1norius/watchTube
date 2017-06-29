package com.minorius.watchertube;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.minorius.watchertube.db.RightDBHandler;
import com.minorius.watchertube.db.RightDBUtils;

import java.io.IOException;

/**
 * Created by minorius on 22.06.2017.
 */

public class VideoActivity extends YouTubeBaseActivity {

    private YouTubePlayerView playerFrame;
    private YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        playerFrame = (YouTubePlayerView) findViewById(R.id.id_player);

        final Object videoUrl = getIntent().getExtras().get("VIDEO_URL");
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if(videoUrl != null){
                    youTubePlayer.loadVideo(videoUrl.toString());
                    youTubePlayer.setFullscreen(true);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        playerFrame.initialize(ContentActivity.KEY, onInitializedListener);
    }
}
