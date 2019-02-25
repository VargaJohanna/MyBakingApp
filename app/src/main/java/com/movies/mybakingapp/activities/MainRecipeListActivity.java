package com.movies.mybakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.movies.mybakingapp.viewmodels.MainRecipeListViewModel;
import com.movies.mybakingapp.widgets.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class MainRecipeListActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener {
    public static int recipePosition = 0;
    ActivityMainRecipeListBinding mBinding;
    private RecyclerView recyclerView;
    private MainRecipeListViewModel viewModel;
    private RecipeAdapter mainAdapter;
    private GetRecipesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_recipe_list);
        recyclerView = findViewById(R.id.recipe_recycle_list);
        viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        mainAdapter = new RecipeAdapter(new ArrayList<Recipe>(), this);
        service = RetrofitInstance.getInstance().create(GetRecipesService.class);
        viewModel.fetchRecipeList(service);
        observeRecipeList();
        generateRecipeList();
    }

    private void generateRecipeList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_list_columns));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainAdapter);
    }

    private void observeRecipeList() {
        mBinding.progressBarRecipeList.setVisibility(View.VISIBLE);
        viewModel.getRecipeList().observe(MainRecipeListActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if (!recipeList.isEmpty()) {
                    mainAdapter.updateList(recipeList);
                } else {
                    mBinding.noRecipeMessage.setVisibility(View.VISIBLE);
                    Toast.makeText(MainRecipeListActivity.this, getString(R.string.no_network_message), Toast.LENGTH_SHORT).show();
                }
                mBinding.progressBarRecipeList.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(Recipe recipe, int recipePosition) {
        MainRecipeListActivity.recipePosition = recipePosition;
        notifyWidget();
        Intent recipeDetailActivity = new Intent(this, RecipeInstructionsActivity.class);
        recipeDetailActivity.putExtra(RecipeInstructionsActivity.RECIPE_OBJECT_FLAG, recipe);
        startActivity(recipeDetailActivity);
    }

    private void notifyWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredient_list);

    }
}
