package com.movies.mybakingapp.fragments;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.RecipeInstructionsActivity;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.utilities.ConnectionUtils;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

import java.util.List;

import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.FROM_STEP_TAG;
import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.RECIPE_FRAGMENT;
import static com.movies.mybakingapp.activities.RecipeInstructionsActivity.STEP_FRAGMENT;

public class StepDetailFragment extends Fragment {
    public static final String MEDIA_SESSION_TAG = StepDetailFragment.class.getSimpleName();
    private RecipeDetailViewModel detailViewModel;
    private TextView stepDetail;
    private SimpleExoPlayerView simpleExoPlayerView;
    private Dialog fullScreenDialog;

    public StepDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        detailViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        detailViewModel.setExoPlayer(getActivity());
        detailViewModel.setStepClicked(true);
        initFullScreenDialog();
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        stepDetail = rootView.findViewById(R.id.step_full_description);
        simpleExoPlayerView = rootView.findViewById(R.id.playerView);

        stepDetail.setText(detailViewModel.getStepLongDescription());
        // Show player only if there's available network
        if(ConnectionUtils.isNetworkAvailable(getActivity())) {
            if (detailViewModel.isMediaAvailableForStep()) {
                simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.grey_background));
                detailViewModel.initialiseMediaSession(getActivity(), MEDIA_SESSION_TAG, detailViewModel.getExoPlayer());
                detailViewModel.initialisePlayer(simpleExoPlayerView, detailViewModel.getUri(), detailViewModel.getExoPlayer(), getActivity());
            } else {
                simpleExoPlayerView.setVisibility(View.GONE);
            }
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.no_network_message), Toast.LENGTH_SHORT).show();
        }
        if (savedInstanceState == null && !detailViewModel.isTwoPaneMode()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                openFullScreenDialog();
            }
        }
        setUpButtonViews(rootView.findViewById(R.id.previous_button),
                rootView.findViewById(R.id.next_button));

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailViewModel.releasePlayer();
        detailViewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        detailViewModel.releasePlayer();
        detailViewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !detailViewModel.isTwoPaneMode()) {
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
        if (detailViewModel.isMediaAvailableForStep()) {
            ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
            fullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            fullScreenDialog.show();
        }
    }

    private void closeFullScreenDialog(Fragment fragment, String fragmentTag) {
        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        fullScreenDialog.dismiss();
        replaceFragment(fragment, fragmentTag);
        detailViewModel.releasePlayer();
    }

    private void replaceFragment(Fragment fragment, String fragmentTag) {
        detailViewModel.getFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_fragment_framelayout, fragment, fragmentTag)
                .addToBackStack(FROM_STEP_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void setUpButtonViews(final View preButton, final View nextButton) {
        final List<Step> stepList = detailViewModel.getCurrentRecipe().getStepsList();
        setButtonsVisibility(preButton, nextButton, stepList.size());
        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailViewModel.releasePlayer();
                detailViewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
                if (detailViewModel.getCurrentStepPosition() - 1 > 0) {
                    detailViewModel.setCurrentStep(stepList.get(detailViewModel.getCurrentStepPosition() - 1));
                    detailViewModel.setSelectedStepPosition(detailViewModel.getCurrentStepPosition());
                } else {
                    detailViewModel.setCurrentStep(stepList.get(0));
                    detailViewModel.setSelectedStepPosition(0);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailViewModel.releasePlayer();
                detailViewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
                if (detailViewModel.getCurrentStepPosition() + 1 < stepList.size()) {
                    detailViewModel.setCurrentStep(stepList.get(detailViewModel.getCurrentStepPosition() + 1));
                    detailViewModel.setSelectedStepPosition(detailViewModel.getCurrentStepPosition());
                } else {
                    nextButton.setVisibility(View.INVISIBLE);
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
}
