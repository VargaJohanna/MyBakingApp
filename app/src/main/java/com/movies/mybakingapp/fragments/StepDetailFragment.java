package com.movies.mybakingapp.fragments;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.RecipeInstructionsActivity;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.FROM_STEP_TAG;
import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.RECIPE_FRAGMENT;
import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.STEP_FRAGMENT;

public class StepDetailFragment extends Fragment {
    public static final String MEDIA_SESSION_TAG = StepDetailFragment.class.getSimpleName();
    private RecipeDetailViewModel detailViewModel;
    private TextView stepDetail;
    private SimpleExoPlayerView simpleExoPlayerView;
    private Dialog fullScreenDialog;
    private boolean playerFullScreen;
    private boolean isMediaAvailable = false;

    public StepDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        detailViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        detailViewModel.setExoPlayer(getActivity());
        RecipeInstructionsActivity.isStepClicked = true;
        initFullScreenDialog();

        // Check if media is available
        if (!detailViewModel.getSavedStep().getThumbnailURL().isEmpty() || !detailViewModel.getSavedStep().getVideoURL().isEmpty()) isMediaAvailable = true;

        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        stepDetail = rootView.findViewById(R.id.step_full_description);
        simpleExoPlayerView = rootView.findViewById(R.id.playerView);
        stepDetail.setText(detailViewModel.getSavedStep().getDescription());
        if(isMediaAvailable) {
            simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.grey_background));
            detailViewModel.initialiseMediaSession(getActivity(), MEDIA_SESSION_TAG, detailViewModel.getExoPlayer());
            detailViewModel.initialisePlayer(simpleExoPlayerView, getUri(), detailViewModel.getExoPlayer(), getActivity());
        }

        if(savedInstanceState == null && !detailViewModel.isTwoPane()) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                openFullScreenDialog();
            }
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailViewModel.releasePlayer();
        detailViewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
    }

    private Uri getUri() {
        String url = "";
        if(isMediaAvailable) {
            if(!detailViewModel.getSavedStep().getThumbnailURL().isEmpty()) {
                url = detailViewModel.getSavedStep().getThumbnailURL();
            } else if (!detailViewModel.getSavedStep().getVideoURL().isEmpty()) {
                url = detailViewModel.getSavedStep().getVideoURL();
            }
        } else {
            Log.d("DEBUG", "No media was available in the api");
        }
        return Uri.parse(url);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !detailViewModel.isTwoPane()) {
            openFullScreenDialog();
        } else {
            closeFullScreenDialog(new StepDetailFragment(), STEP_FRAGMENT);
        }
    }

    private void initFullScreenDialog() {
        fullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                closeFullScreenDialog(new RecipeDetailFragment(), RECIPE_FRAGMENT);
            }
        };
    }

    private void openFullScreenDialog() {
        if(isMediaAvailable) {
            ((ViewGroup)simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
            fullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            playerFullScreen = true;
            fullScreenDialog.show();
        }
    }

    private void closeFullScreenDialog(Fragment fragment, String fragmentTag) {
        ((ViewGroup)simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        fullScreenDialog.dismiss();
        detailViewModel.getFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_fragment_framelayout, fragment, fragmentTag)
                .addToBackStack(FROM_STEP_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        playerFullScreen = false;
    }
}
