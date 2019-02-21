package com.movies.mybakingapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.fragments.RecipeDetailFragment;
import com.movies.mybakingapp.modal.Recipe;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener {

    public static final String RECIPE_OBJECT_FLAG = "recipe_flag";
    private Intent intent;
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        intent = getIntent();
        if(intent.hasExtra(RECIPE_OBJECT_FLAG)) {
            recipe = intent.getParcelableExtra(RECIPE_OBJECT_FLAG);
        }
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        detailFragment.setRecipe(recipe);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.recipe_detail_fragment_framelayout, detailFragment)
                .commit();
    }

    @Override
    public void onStepSelected(int position) {
        Toast.makeText(this, "Position " + position, Toast.LENGTH_SHORT).show();
    }
}
