package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;


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
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.movies.mybakingapp.fragments.StepDetailFragment;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Steps;

public class RecipeDetailViewModel extends AndroidViewModel implements ExoPlayer.EventListener{
    private MutableLiveData<Steps> currentStep;
    private Recipe currentRecipe;
    private MediaSessionCompat mediaSession;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder playbackStateBuilder;

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        this.currentStep = new MutableLiveData<>();
    }

    public LiveData<Steps> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Steps currentStep) {
        this.currentStep.setValue(currentStep);
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public void setExoPlayer(Context context) {
        if(exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            this.exoPlayer = ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(),trackSelector, loadControl);
        }
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void initialisePlayer(SimpleExoPlayerView view, Uri uri, SimpleExoPlayer player, Context context) {
        view.setPlayer(player);

        player.addListener(this);
        String userAgent = Util.getUserAgent(context, "MyBakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    public MediaSessionCompat getMediaSession(Context context, String activityNameTag) {
        if(mediaSession == null) {
            this.mediaSession = new MediaSessionCompat(context, activityNameTag);
        }
        return mediaSession;
    }

    public PlaybackStateCompat.Builder getStateBuilder() {
        if(playbackStateBuilder == null) {
            this.playbackStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);
        }
        return playbackStateBuilder;
    }
    public void initialiseMediaSession(Context context, String activityNameTag, SimpleExoPlayer exoPlayer) {
        getMediaSession(context, activityNameTag);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);
        mediaSession.setPlaybackState(getStateBuilder().build());
        mediaSession.setCallback(new MySessionCallback(exoPlayer));
        mediaSession.setActive(true);
    }

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
        if(playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            getStateBuilder().setState(PlaybackStateCompat.STATE_PLAYING,
                    getExoPlayer().getCurrentPosition(), 1f);
        } else if(playbackState == ExoPlayer.STATE_READY) {
            getStateBuilder().setState(PlaybackStateCompat.STATE_PAUSED,
                    getExoPlayer().getCurrentPosition(), 1f);
        }
        getMediaSession(getApplication(), StepDetailFragment.MEDIA_SESSION_TAG).setPlaybackState(getStateBuilder().build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        private SimpleExoPlayer exoPlayer;
        public MySessionCallback(SimpleExoPlayer exoPlayer) {
            this.exoPlayer = exoPlayer;
        }
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }

    public void releasePlayer(SimpleExoPlayer exoPlayer) {
        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer = null;
    }
}
