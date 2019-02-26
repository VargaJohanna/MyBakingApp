package com.movies.mybakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.adapters.IngredientsAdapter;
import com.movies.mybakingapp.adapters.StepsAdapter;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class RecipeDetailFragment extends Fragment implements StepsAdapter.ItemClickListenerSteps {
    private RecipeDetailViewModel detailViewModel;

    public RecipeDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        detailViewModel = ViewModelProviders.of(requireActivity()).get(RecipeDetailViewModel.class);

        setupIngredientsList(rootView);
        setupStepList(rootView);

        return rootView;
    }

    private void setupStepList(View rootView) {
        RecyclerView.LayoutManager stepLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView stepsRecyclerView = rootView.findViewById(R.id.steps_recycler_view);
        final StepsAdapter stepsAdapter = new StepsAdapter(this, detailViewModel);
        stepsRecyclerView.setLayoutManager(stepLayoutManager);
        stepsRecyclerView.setAdapter(stepsAdapter);
        detailViewModel.getCurrentRecipe().observe(requireActivity(), new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    stepsAdapter.setSteps(recipe.getStepsList());
                }
            }
        });
    }

    private void setupIngredientsList(View rootView) {
        RecyclerView.LayoutManager ingredientsLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.ingredients_recycler_view);
        final IngredientsAdapter ingredientsAdapter = new IngredientsAdapter();
        ingredientsRecyclerView.setLayoutManager(ingredientsLayout);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        detailViewModel.getCurrentRecipe().observe(requireActivity(), new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    ingredientsAdapter.setList(recipe.getIngredientsList());
                }
            }
        });
    }

    @Override
    public void onItemClick(Step step) {
        detailViewModel.setStepClicked(true);
        detailViewModel.setCurrentStepDetails(step);
    }
}
