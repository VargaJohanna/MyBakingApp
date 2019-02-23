package com.movies.mybakingapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.adapters.StepsAdapter;
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
    public static boolean isStepClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent intent = getIntent();
        detailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        detailViewModel.setFragmentManager(getSupportFragmentManager());
        isStepClicked = false;
        detailViewModel.setTwoPane(false);
        if (intent.hasExtra(RECIPE_OBJECT_FLAG)) {
            Recipe recipe = intent.getParcelableExtra(RECIPE_OBJECT_FLAG);
            detailViewModel.setCurrentRecipe(recipe);
        }
        if (findViewById(R.id.step_detail_fragment_framelayout) != null) {
            detailViewModel.setTwoPane(true);
        }
        if (savedInstanceState == null) {
            detailViewModel.getFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_fragment_framelayout, new RecipeDetailFragment(), RECIPE_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            if(detailViewModel.isTwoPane()) {
                detailViewModel.setSavedStep(detailViewModel.getFirstStep());
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
                detailViewModel.setSavedStep(step);
                if (detailViewModel.isTwoPane()) {
                    detailViewModel.getFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_fragment_framelayout, new StepDetailFragment(), STEP_FRAGMENT)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                } else if (!detailViewModel.isTwoPane() && isStepClicked){
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
        if (!detailViewModel.isTwoPane()) {
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
