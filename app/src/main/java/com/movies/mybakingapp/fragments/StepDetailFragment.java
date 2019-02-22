package com.movies.mybakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.RecipeInstructionsActivity;
import com.movies.mybakingapp.modal.Steps;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class StepDetailFragment extends Fragment {
    public static final String MEDIA_SESSION_TAG = StepDetailFragment.class.getSimpleName();
    private RecipeDetailViewModel viewModel;

    public void setStep(Steps step) {
        this.step = step;
    }

    private Steps step;

    public StepDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        viewModel.setExoPlayer(getActivity());
        if (savedInstanceState != null) {
            step = savedInstanceState.getParcelable(RecipeInstructionsActivity.STEP_KEY);
        }
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        final TextView stepDetail = rootView.findViewById(R.id.step_full_description);
        SimpleExoPlayerView simpleExoPlayerView = rootView.findViewById(R.id.playerView);

        stepDetail.setText(step.getDescription());
        viewModel.initialiseMediaSession(getActivity(), MEDIA_SESSION_TAG, viewModel.getExoPlayer());
        viewModel.initialisePlayer(simpleExoPlayerView, Uri.parse(step.getVideoURL()), viewModel.getExoPlayer(), getActivity());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(RecipeInstructionsActivity.STEP_KEY, step);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.releasePlayer(viewModel.getExoPlayer());
        viewModel.getMediaSession(getActivity(), MEDIA_SESSION_TAG).setActive(false);
    }
}
