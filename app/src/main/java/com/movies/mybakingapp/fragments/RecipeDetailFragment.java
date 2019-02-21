package com.movies.mybakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.RecipeDetailActivity;
import com.movies.mybakingapp.adapters.IngredientsAdapter;
import com.movies.mybakingapp.adapters.StepsAdapter;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Steps;

public class RecipeDetailFragment extends Fragment {
    OnStepClickListener mCallback;
    private Recipe recipe;

    public interface OnStepClickListener {

        void onStepSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    public RecipeDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        //Ingredients
        RecyclerView.LayoutManager ingredientsLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.ingredients_recycler_view);
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(recipe.getIngredientsList());
        ingredientsRecyclerView.setLayoutManager(ingredientsLayout);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        //Steps
        RecyclerView.LayoutManager stepLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView stepsRecyclerView = rootView.findViewById(R.id.steps_recycler_view);
        StepsAdapter stepsAdapter = new StepsAdapter(recipe.getStepsList());
        stepsRecyclerView.setLayoutManager(stepLayoutManager);
        stepsRecyclerView.setAdapter(stepsAdapter);

        return rootView;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
