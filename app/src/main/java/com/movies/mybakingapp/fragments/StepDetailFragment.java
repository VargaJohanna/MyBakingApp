package com.movies.mybakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.movies.mybakingapp.R;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.utilities.ConnectionUtils;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {
    public static final String MEDIA_SESSION_TAG = StepDetailFragment.class.getSimpleName();
    private static final String CURRENT_STEP_POSITION = "current_step_position";
    private static final String CURRENT_PLAYER_POSITION = "current_player_position";
    private static final String IS_PLAYER_PLAYING = "is_player_playing";
    private RecipeDetailViewModel detailViewModel;
    private SimpleExoPlayerView simpleExoPlayerView;
    private ConstraintLayout constraintLayout;
    private TextView stepDetail;
    private ImageView imageView;
    private long currentPlayerPosition;
    private boolean isPlayerPlaying = true;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaSessionCompat mediaSession;


    public StepDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        detailViewModel = ViewModelProviders.of(requireActivity()).get(RecipeDetailViewModel.class);
        detailViewModel.setStepClicked(true);
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        stepDetail = rootView.findViewById(R.id.step_full_description);
        simpleExoPlayerView = rootView.findViewById(R.id.playerView);
        imageView = rootView.findViewById(R.id.step_image);
        constraintLayout = rootView.findViewById(R.id.step_detail_layout);
        ImageButton prevButton = rootView.findViewById(R.id.previous_button);
        ImageButton nextButton = rootView.findViewById(R.id.next_button);
        if (detailViewModel.getLatestAvailableRecipe() == null) {
            detailViewModel.setCurrentRecipeValueFromDB();
        }

        if (detailViewModel.getPlayer() != null) {
            currentPlayerPosition = detailViewModel.getCurrentPlayerPosition();
            isPlayerPlaying = detailViewModel.isPlayerPlaying();
        }

        if (savedInstanceState != null) {
            List<Step> stepList = detailViewModel.getLatestAvailableRecipe().getStepsList();
            Step currentStep;
            if (stepList.size() > savedInstanceState.getInt(CURRENT_STEP_POSITION)) {
                currentStep = stepList.get(savedInstanceState.getInt(CURRENT_STEP_POSITION));
                detailViewModel.setSelectedStepPosition(savedInstanceState.getInt(CURRENT_STEP_POSITION));
            } else {
                currentStep = stepList.get(stepList.size() - 1);
                detailViewModel.setSelectedStepPosition(stepList.size() - 1);
            }
            detailViewModel.setCurrentStepDetails(currentStep);
            currentPlayerPosition = savedInstanceState.getLong(CURRENT_PLAYER_POSITION);
            isPlayerPlaying = savedInstanceState.getBoolean(IS_PLAYER_PLAYING);

        }

        stepDetail.setText(detailViewModel.getStepLongDescription());
        showMedia(imageView, simpleExoPlayerView, stepDetail, nextButton, prevButton);

        setUpButtonViews(rootView.findViewById(R.id.previous_button),
                rootView.findViewById(R.id.next_button));

        return rootView;
    }

    @Override
    public void onPause() {
        if (detailViewModel.getPlayer() != null) {
            currentPlayerPosition = detailViewModel.getPlayer().getCurrentPosition();
            isPlayerPlaying = detailViewModel.getPlayer().getPlayWhenReady();
            detailViewModel.setCurrentPlayerPosition(currentPlayerPosition);
            detailViewModel.setPlayerPlaying(isPlayerPlaying);
        }

        if (Util.SDK_INT <= 23) {
            releasePlayer();
            getMediaSession(requireActivity(), MEDIA_SESSION_TAG).setActive(false);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayer();
            getMediaSession(requireActivity(), MEDIA_SESSION_TAG).setActive(false);
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION, detailViewModel.getCurrentStepPosition());
        outState.putLong(CURRENT_PLAYER_POSITION, currentPlayerPosition);
        outState.putBoolean(IS_PLAYER_PLAYING, isPlayerPlaying);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 && detailViewModel.isMediaAvailableForStep()) {
            initialisePlayer(simpleExoPlayerView, detailViewModel.getUri(), requireActivity());
            initialiseMediaSession(MEDIA_SESSION_TAG, detailViewModel.getPlayer());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || detailViewModel.getPlayer() == null) && detailViewModel.isMediaAvailableForStep()) {
            initialisePlayer(simpleExoPlayerView, detailViewModel.getUri(), requireActivity());
            initialiseMediaSession(MEDIA_SESSION_TAG, detailViewModel.getPlayer());
        }

        // Hide action bar and navigation in landscape view if player is available
        if (getActivity().getResources() != null && getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE && detailViewModel.getPlayer() != null && !detailViewModel.isTwoPaneMode()) {
            // Hide action bar
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            // Hide the status bar.
            View decorView = requireActivity().getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void showMedia(ImageView imageView, SimpleExoPlayerView simpleExoPlayerView, TextView stepDetail, ImageButton nextButton, ImageButton prevButton) {
        if (detailViewModel.isMediaAvailableForStep()) {
            if (detailViewModel.isUrlMp4()) {
                imageView.setVisibility(View.GONE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.grey_background));
            } else {
                showImageInsteadOfVideo(simpleExoPlayerView, imageView);
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                stepDetail.setVisibility(View.VISIBLE);
            }
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
            stepDetail.setVisibility(View.VISIBLE);
            stepDetail.setText(detailViewModel.getStepLongDescription());
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
        if (ConnectionUtils.isNetworkAvailable(requireActivity())) {
        } else {
            Toast.makeText(requireActivity(), getString(R.string.no_network_message), Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageInsteadOfVideo(SimpleExoPlayerView simpleExoPlayerView, ImageView imageView) {
        simpleExoPlayerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(detailViewModel.getUri())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

    }

    private void setUpButtonViews(final View preButton, final View nextButton) {
        detailViewModel.getCurrentRecipe().observe(requireActivity(), new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    final List<Step> stepList = recipe.getStepsList();
                    setButtonsVisibility(preButton, nextButton, stepList.size());
                    preButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            releasePlayer();
                            getMediaSession(requireActivity(), MEDIA_SESSION_TAG).setActive(false);
                            if (detailViewModel.getCurrentStepPosition() - 1 > 0) {
                                detailViewModel.setCurrentStepDetails(stepList.get(detailViewModel.getCurrentStepPosition() - 1));
                                detailViewModel.setSelectedStepPosition(detailViewModel.getCurrentStepPosition());
                            } else {
                                detailViewModel.setCurrentStepDetails(stepList.get(0));
                                detailViewModel.setSelectedStepPosition(0);
                            }
                            getFragmentManager().popBackStack();
                        }
                    });
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            releasePlayer();
                            getMediaSession(requireActivity(), MEDIA_SESSION_TAG).setActive(false);
                            if (detailViewModel.getCurrentStepPosition() + 1 < stepList.size()) {
                                detailViewModel.setCurrentStepDetails(stepList.get(detailViewModel.getCurrentStepPosition() + 1));
                                detailViewModel.setSelectedStepPosition(detailViewModel.getCurrentStepPosition());
                            } else {
                                nextButton.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setButtonsVisibility(View prevButton, View nextButton, int stepListSize) {
        if (!detailViewModel.isTwoPaneMode()) {
            if (detailViewModel.getCurrentStepPosition() != 0 && detailViewModel.getCurrentStepPosition() != stepListSize - 1) {
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
            } else if (detailViewModel.getCurrentStepPosition() == 0) {
                prevButton.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.VISIBLE);
            } else if (detailViewModel.getCurrentStepPosition() == stepListSize - 1) {
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.INVISIBLE);
            }
        } else {
            prevButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
        }
    }

    public MediaSessionCompat getMediaSession(Context context, String activityNameTag) {
        if (mediaSession == null) {
            this.mediaSession = new MediaSessionCompat(context, activityNameTag);
        }
        return mediaSession;
    }

    public void initialisePlayer(SimpleExoPlayerView view, Uri uri, Context context) {

        detailViewModel.setPlayer(ExoPlayerFactory.newSimpleInstance(
                requireActivity(),
                new DefaultTrackSelector(), new DefaultLoadControl()));
        view.setPlayer(detailViewModel.getPlayer());
        detailViewModel.getPlayer().addListener(this);
        String userAgent = Util.getUserAgent(context, "MyBakingApp");

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null, null);
        detailViewModel.getPlayer().prepare(mediaSource);
        detailViewModel.getPlayer().seekTo(currentPlayerPosition);
        detailViewModel.getPlayer().setPlayWhenReady(isPlayerPlaying);
    }

    private PlaybackStateCompat.Builder getStateBuilder() {
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

    public void initialiseMediaSession(String activityNameTag, SimpleExoPlayer exoPlayer) {
        MediaSessionCompat mediaSession = getMediaSession(requireActivity(), activityNameTag);
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
                    detailViewModel.getPlayer().getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            getStateBuilder().setState(PlaybackStateCompat.STATE_PAUSED,
                    detailViewModel.getPlayer().getCurrentPosition(), 1f);
        }
        getMediaSession(requireActivity(), StepDetailFragment.MEDIA_SESSION_TAG).setPlaybackState(getStateBuilder().build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        private SimpleExoPlayer exoPlayer;

        MySessionCallback(SimpleExoPlayer exoPlayer) {
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
        if (detailViewModel.getPlayer() != null) {
            detailViewModel.getPlayer().stop();
            detailViewModel.getPlayer().release();
            detailViewModel.setPlayer(null);
            detailViewModel.setCurrentPlayerPosition(0);
            detailViewModel.setPlayerPlaying(true);
        }
    }
}
