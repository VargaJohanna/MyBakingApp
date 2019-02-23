package com.movies.mybakingapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.fragments.RecipeDetailFragment;
import com.movies.mybakingapp.fragments.StepDetailFragment;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Steps;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class RecipeInstructionsActivity extends AppCompatActivity {

    public static final String RECIPE_OBJECT_FLAG = "recipe_flag";
    public static final String STEP_KEY = "step";
    public static final String RECIPE_FRAGMENT = "RecipeDetailFragment";
    public static final String STEP_FRAGMENT = "StepDetailFragment";
    public static final String FROM_STEP_TAG = "FromStep";
    private Intent intent;
    private Recipe recipe;
    private RecipeDetailViewModel detailViewModel;
    private FragmentManager fragmentManager;
    private RecipeDetailFragment recipeFragment;
    private boolean twoPane;
    public static boolean isStepClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        intent = getIntent();
        detailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        fragmentManager = getSupportFragmentManager();
        isStepClicked = false;
        twoPane = false;
        if (intent.hasExtra(RECIPE_OBJECT_FLAG)) {
            recipe = intent.getParcelableExtra(RECIPE_OBJECT_FLAG);
            detailViewModel.setCurrentRecipe(recipe);
        }
        if (findViewById(R.id.step_detail_fragment_framelayout) != null) {
            twoPane = true;
        }
        if (savedInstanceState == null) {
            recipeFragment = new RecipeDetailFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_detail_fragment_framelayout, recipeFragment, RECIPE_FRAGMENT)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        observeActiveStep();
        setTitle(detailViewModel.getCurrentRecipe().getName());
    }

    private void observeActiveStep() {
        detailViewModel.getCurrentStep().observe(this, new Observer<Steps>() {
            @Override
            public void onChanged(@Nullable Steps step) {
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setStep(step);
                if (twoPane) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.step_detail_fragment_framelayout, stepDetailFragment, STEP_FRAGMENT)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                } else if (!twoPane && isStepClicked){
                    fragmentManager.beginTransaction()
                            .replace(R.id.recipe_detail_fragment_framelayout, stepDetailFragment, STEP_FRAGMENT)
                            .addToBackStack(FROM_STEP_TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!twoPane) {
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
