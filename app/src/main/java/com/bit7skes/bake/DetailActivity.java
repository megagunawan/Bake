package com.bit7skes.bake;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bit7skes.bake.models.Cake;
import com.bit7skes.bake.models.Ingredient;
import com.bit7skes.bake.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
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
    private List<Ingredient> ingredientList = new ArrayList<>();
    private int currentStepNum;
    private SimpleExoPlayer mPlayer;

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
        ingredientList = clickedCake.getIngredients();
        sendIntentToWidget();

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

        initializeVideoPlayer();
    }

    private void sendIntentToWidget() {
        try {
            Intent intent = new Intent(this, IngredientWidget.class);
            intent.setAction("update_widget");
            intent.putExtra("ingredientList", (Serializable) ingredientList);
            intent.putExtra("cakeName", clickedCake.getName());
            PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void initializeVideoPlayer() {
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
                    Log.d("ExoPlayer", "playWhenReady = " + playWhenReady);
                    switch (playbackState) {
                        case ExoPlayer.STATE_IDLE:
                            Log.v("ExoPlayer", "State Idle");
                            break;
                        case ExoPlayer.STATE_BUFFERING:
                            Log.v("ExoPlayer", "State Buffering");
                            break;
                        case ExoPlayer.STATE_READY:
                            Log.v("ExoPlayer", "State Ready");
                            break;
                        case ExoPlayer.STATE_ENDED:
                            Log.v("ExoPlayer", "State Ended");
                            break;
                    }
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
            MediaSource mediaSource = buildMediaSource(videoURL);

            mPlayer.prepare(mediaSource);
            //mPlayer.setPlayWhenReady(true);
        }
    }
    private MediaSource buildMediaSource(String url) {
        String userAgent = Util.getUserAgent(this, "Bake");
        return new ExtractorMediaSource(Uri.parse(url),
                new DefaultHttpDataSourceFactory(userAgent),
                new DefaultExtractorsFactory(),
                null,
                null);
    }

    @Override
    public void onClick(final View view) {
        mPlayer.stop();
        mPlayer.release();

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
