package com.movies.mybakingapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.fragments.RecipeDetailFragment;
import com.movies.mybakingapp.fragments.StepDetailFragment;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class RecipeInstructionsActivity extends AppCompatActivity {

    public static final String RECIPE_OBJECT_FLAG = "recipe_flag";
    public static final String RECIPE_FRAGMENT = "RecipeDetailFragment";
    public static final String STEP_FRAGMENT = "StepDetailFragment";
    public static final String FROM_STEP_TAG = "FromStep";
    private RecipeDetailViewModel detailViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent intent = getIntent();
        detailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        detailViewModel.setFragmentManager(getSupportFragmentManager());
        detailViewModel.setStepClicked(false);
        detailViewModel.setTwoPaneMode(false);
        if (intent.hasExtra(RECIPE_OBJECT_FLAG)) {
            Recipe recipe = intent.getParcelableExtra(RECIPE_OBJECT_FLAG);
            detailViewModel.setCurrentRecipe(recipe);
        }
        if (findViewById(R.id.step_detail_fragment_framelayout) != null) {
            detailViewModel.setTwoPaneMode(true);
        }
//        observeActiveStep();
        if (savedInstanceState == null) {
            detailViewModel.getFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_fragment_framelayout, new RecipeDetailFragment(), RECIPE_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            if (detailViewModel.isTwoPaneMode()) {
                // setCurrentStep() is not triggering the onChanged() in getCurrentStep(), so I set the video urls manually.
                detailViewModel.setCurrentStep(detailViewModel.getFirstStep());
                detailViewModel.setThumbnailURL(detailViewModel.getFirstStep().getThumbnailURL());
                detailViewModel.setVideoURL(detailViewModel.getFirstStep().getVideoURL());
                detailViewModel.setSelectedStepPosition(0);
                detailViewModel.getFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_fragment_framelayout, new StepDetailFragment(), STEP_FRAGMENT)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }
        observeActiveStep();
        setTitle(detailViewModel.getCurrentRecipe().getName());
    }

    private void observeActiveStep() {
        detailViewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                detailViewModel.setVideoURL(step.getVideoURL());
                detailViewModel.setThumbnailURL(step.getThumbnailURL());
                detailViewModel.setStepLongDescription(step.getDescription());
                detailViewModel.setCurrentStepPosition(step.getId());

                if (detailViewModel.isTwoPaneMode()) {
                    detailViewModel.getFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_fragment_framelayout, new StepDetailFragment(), STEP_FRAGMENT)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                } else if (!detailViewModel.isTwoPaneMode() && detailViewModel.isStepClicked()) {
                    detailViewModel.getFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_fragment_framelayout, new StepDetailFragment(), STEP_FRAGMENT)
                            .addToBackStack(FROM_STEP_TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!detailViewModel.isTwoPaneMode()) {
            if (getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT) != null) {
                getSupportFragmentManager().popBackStack(FROM_STEP_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
