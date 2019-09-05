package com.bit7skes.bake;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bit7skes.bake.models.Cake;
import com.bit7skes.bake.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.w3c.dom.Text;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mBakingStepTextView;
    private TextView mShortDescriptionTextView;
    private SimpleExoPlayerView mVideoPlayerView;
    private TextView mDescriptionTextView;
    private Button mNextStepButton;
    private Button mPreviousStepButton;
    private Cake clickedCake;
    private List<Step> stepList;
    private int currentStepNum;
    private SimpleExoPlayer mPlayer;
    private NotificationManager mNotificationManager;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.d("DetailActivity", "in detail activity");

        mBakingStepTextView = findViewById(R.id.baking_step_tv);
        mShortDescriptionTextView = findViewById(R.id.short_description_tv);
        mVideoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video_player_view);
        mDescriptionTextView = findViewById(R.id.description_tv);
        mNextStepButton = findViewById(R.id.next_step_button);
        mPreviousStepButton = findViewById(R.id.previous_step_button);
        mNextStepButton.setOnClickListener(this);
        mPreviousStepButton.setOnClickListener(this);

        Intent intent = getIntent();

        if (intent == null) {
            closeOnError();
            return;
        }

        clickedCake = (Cake) intent.getSerializableExtra("cake");
        currentStepNum = intent.getIntExtra("currentStepNum", currentStepNum);
        Log.v("currentStepNum", "" + currentStepNum);
        stepList = clickedCake.getSteps();

        loadCake();
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void loadCake() {
        mBakingStepTextView.setText("Step " + (currentStepNum + 1) + " of " + stepList.size());
        mShortDescriptionTextView.setText(stepList.get(currentStepNum).getShortDescription());
        mDescriptionTextView.setText(stepList.get(currentStepNum).getDescription());
        if (currentStepNum == 0) {
            mPreviousStepButton.setVisibility(View.INVISIBLE);
        } else {
            mPreviousStepButton.setVisibility(View.VISIBLE);
        }

        if (currentStepNum == stepList.size() - 1) {
            mNextStepButton.setVisibility(View.INVISIBLE);
        } else {
            mNextStepButton.setVisibility(View.VISIBLE);
        }

        if (mPlayer == null) {
            TrackSelector tmpTrackSelector = new DefaultTrackSelector();
            LoadControl tmpLoadControl = new DefaultLoadControl();

            mPlayer = ExoPlayerFactory.newSimpleInstance(this, tmpTrackSelector, tmpLoadControl);
            mPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }
            });
            mVideoPlayerView.setPlayer(mPlayer);

            String videoURL = stepList.get(currentStepNum).getVideoURL();
            Log.v("videoURL", videoURL);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            String userAgent = Util.getUserAgent(this, "Bake");
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoURL),
                    dataSourceFactory, extractorsFactory, null, null);
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onClick(final View view) {
        Log.v("onClick", "onClick");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view.getId() == R.id.next_step_button) {
                    currentStepNum++;
                } else {
                    currentStepNum--;
                }
                Log.v("test", "test");
                Intent nextStep = new Intent(DetailActivity.this, DetailActivity.class);
                nextStep.putExtra("cake", clickedCake);
                nextStep.putExtra("currentStepNum", currentStepNum);
                finish();
                startActivity(nextStep);
            }
        }, 0);
    }
}
