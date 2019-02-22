package com.movies.mybakingapp.fragments;

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
import com.movies.mybakingapp.modal.Steps;
import com.movies.mybakingapp.viewmodels.RecipeDetailViewModel;

public class RecipeDetailFragment extends Fragment implements StepsAdapter.ItemClickListenerSteps {
    private RecipeDetailViewModel detailViewModel;

    public RecipeDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        detailViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);

        //Ingredients
        RecyclerView.LayoutManager ingredientsLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.ingredients_recycler_view);
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(detailViewModel.getCurrentRecipe().getIngredientsList());
        ingredientsRecyclerView.setLayoutManager(ingredientsLayout);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        //Steps
        RecyclerView.LayoutManager stepLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView stepsRecyclerView = rootView.findViewById(R.id.steps_recycler_view);
        StepsAdapter stepsAdapter = new StepsAdapter(detailViewModel.getCurrentRecipe().getStepsList(), this);
        stepsRecyclerView.setLayoutManager(stepLayoutManager);
        stepsRecyclerView.setAdapter(stepsAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(Steps step) {
        detailViewModel.setCurrentStep(step);
    }
}
