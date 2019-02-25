package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;

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
import com.movies.mybakingapp.modal.Step;

public class RecipeDetailViewModel extends AndroidViewModel implements ExoPlayer.EventListener {
    private MutableLiveData<Step> currentStep;
    private int currentStepPosition;
    private Recipe currentRecipe;
    private MediaSessionCompat mediaSession;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private int selectedStepPosition = RecyclerView.NO_POSITION;
    private FragmentManager fragmentManager;
    private boolean twoPaneMode;
    private String StepLongDescription;
    private String videoURL;
    private String thumbnailURL;
    private boolean isStepClicked;

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        this.currentStep = new MutableLiveData<>();
    }

    // Step Data
    public LiveData<Step> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Step currentStep) {
        this.currentStep.setValue(currentStep);
    }

    public int getCurrentStepPosition() {
        return currentStepPosition;
    }

    public void setCurrentStepPosition(int currentStepPosition) {
        this.currentStepPosition = currentStepPosition;
    }

    public Step getFirstStep() {
        return getCurrentRecipe().getStepsList().get(0);
    }

    public int getSelectedStepPosition() {
        return selectedStepPosition;
    }

    public void setSelectedStepPosition(int selectedStepPosition) {
        this.selectedStepPosition = selectedStepPosition;
    }

    public boolean isStepClicked() {
        return isStepClicked;
    }

    public void setStepClicked(boolean stepClicked) {
        isStepClicked = stepClicked;
    }

    // Recipe Data
    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    // Fragment Manager
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    // Share the information about two pane view with the fragments
    public boolean isTwoPaneMode() {
        return twoPaneMode;
    }

    public void setTwoPaneMode(boolean twoPaneMode) {
        this.twoPaneMode = twoPaneMode;
    }


    //Exoplayer methods
    public void setExoPlayer(Context context) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            this.exoPlayer = ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(), trackSelector, loadControl);
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
        if (mediaSession == null) {
            this.mediaSession = new MediaSessionCompat(context, activityNameTag);
        }
        return mediaSession;
    }

    public PlaybackStateCompat.Builder getStateBuilder() {
        if (playbackStateBuilder == null) {
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
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            getStateBuilder().setState(PlaybackStateCompat.STATE_PLAYING,
                    getExoPlayer().getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
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

    public void releasePlayer() {
        if (this.exoPlayer != null) {
            this.exoPlayer.stop();
            this.exoPlayer.release();
            this.exoPlayer = null;
        }
    }

    // Data from current step
    public String getStepLongDescription() {
        return StepLongDescription;
    }

    public void setStepLongDescription(String stepLongDescription) {
        StepLongDescription = stepLongDescription;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public boolean isMediaAvailableForStep() {
        return !getThumbnailURL().isEmpty() || !getVideoURL().isEmpty();
    }

    public Uri getUri() {
        if (isMediaAvailableForStep()) {
            if(!getVideoURL().isEmpty() && !getThumbnailURL().isEmpty()) {
                return Uri.parse(getVideoURL());
            } else if (!getThumbnailURL().isEmpty()) {
                return Uri.parse(getThumbnailURL());
            } else if (!getVideoURL().isEmpty()) {
                return Uri.parse(getVideoURL());
            }
        }
        return Uri.parse("");
    }

    public boolean isUrlMp4() {
        if(isMediaAvailableForStep()) {
            return getThumbnailURL().contains(".mp4") || getVideoURL().contains(".mp4");
        } else {
            return false;
        }
    }
}
