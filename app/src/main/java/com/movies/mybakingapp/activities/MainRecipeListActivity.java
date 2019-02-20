package com.movies.mybakingapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.adapters.RecipeAdapter;
import com.movies.mybakingapp.databinding.ActivityMainRecipeListBinding;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.network.GetRecipesService;
import com.movies.mybakingapp.network.RetrofitInstance;
import com.movies.mybakingapp.viewmodals.MainRecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainRecipeListActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener {
    ActivityMainRecipeListBinding mBinding;
    private MainRecipeListViewModel viewModel;
    private RecipeAdapter mainAdapter;
    private GetRecipesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_recipe_list);
        viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        mainAdapter = new RecipeAdapter(new ArrayList<Recipe>(), this);
        service = RetrofitInstance.getInstance().create(GetRecipesService.class);
        viewModel.fetchRecipeList(service);
        observeRecipeList();
        generateRecipeList();
    }

    private void generateRecipeList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_list_columns));
        mBinding.recipeRecycleList.setLayoutManager(layoutManager);
        mBinding.recipeRecycleList.setAdapter(mainAdapter);
    }

    private void observeRecipeList() {
        mBinding.progressBarRecipeList.setVisibility(View.VISIBLE);
        viewModel.getRecipeList().observe(MainRecipeListActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if(!recipeList.isEmpty()) {
                    mainAdapter.updateList(recipeList);
                } else {
                    Toast.makeText(MainRecipeListActivity.this, getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
                }
                mBinding.progressBarRecipeList.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(Recipe recipe) {
        Toast.makeText(this, recipe.getName(), Toast.LENGTH_SHORT).show();
    }
}
