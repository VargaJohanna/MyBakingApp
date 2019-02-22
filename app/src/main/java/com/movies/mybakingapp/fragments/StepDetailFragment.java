package com.movies.mybakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.RecipeInstructionsActivity;
import com.movies.mybakingapp.modal.Steps;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class StepDetailFragment extends Fragment {
    private RecipeDetailViewModel detailViewModel;

    public void setStep(Steps step) {
        this.step = step;
    }

    private Steps step;
    public StepDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        detailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        if(savedInstanceState != null) {
            step = savedInstanceState.getParcelable(RecipeInstructionsActivity.STEP_KEY);
        }
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        final TextView stepDetail = rootView.findViewById(R.id.step_full_description);
        stepDetail.setText(step.getDescription());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(RecipeInstructionsActivity.STEP_KEY, step);
    }
}
