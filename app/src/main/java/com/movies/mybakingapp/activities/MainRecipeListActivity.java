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
import com.movies.mybakingapp.database.AppDatabase;
import com.movies.mybakingapp.database.RecipeEntry;
import com.movies.mybakingapp.databinding.ActivityMainRecipeListBinding;
import com.movies.mybakingapp.modal.Ingredients;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.network.GetRecipesService;
import com.movies.mybakingapp.network.RetrofitInstance;
import com.movies.mybakingapp.utilities.AppExecutors;
import com.movies.mybakingapp.viewmodels.MainRecipeListViewModel;
import com.movies.mybakingapp.widgets.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class MainRecipeListActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener {
    public static int recipePosition = 0;
    ActivityMainRecipeListBinding mBinding;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_list);
        database = AppDatabase.getInstance(this.getApplication());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_recipe_list);
        RecyclerView recyclerView = findViewById(R.id.recipe_recycle_list);
        MainRecipeListViewModel viewModel = ViewModelProviders.of(this).get(MainRecipeListViewModel.class);
        RecipeAdapter mainAdapter = new RecipeAdapter(new ArrayList<Recipe>(), this);
        GetRecipesService service = RetrofitInstance.getInstance().create(GetRecipesService.class);
        viewModel.fetchRecipeList(service);
        observeRecipeList(viewModel, mainAdapter);
        generateRecipeList(recyclerView, mainAdapter);
    }

    private void generateRecipeList(RecyclerView recyclerView, RecipeAdapter mainAdapter) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_list_columns));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainAdapter);
    }

    private void observeRecipeList(MainRecipeListViewModel viewModel, final RecipeAdapter mainAdapter) {
        mBinding.progressBarRecipeList.setVisibility(View.VISIBLE);
        viewModel.getRecipeList().observe(MainRecipeListActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                assert recipeList != null;
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
    public void onItemClick(final Recipe recipe, int recipePosition) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.recipeDao().insertRecipe(createRecipeEntry(recipe));
            }
        });
        MainRecipeListActivity.recipePosition = recipePosition;
        notifyWidget();
        Intent recipeDetailActivity = new Intent(this, RecipeInstructionsActivity.class);
        startActivity(recipeDetailActivity);
    }

    private void notifyWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredient_list);

    }

    private RecipeEntry createRecipeEntry(Recipe recipe) {
        return new RecipeEntry(
                recipe.getName(),
                recipe.getId(),
                recipe.getServings(),
                recipe.getRecipeImage(),
                getIngredientsQuantityList(recipe),
                getIngredientsMeasureList(recipe),
                getIngredientsNameList(recipe),
                getStepIdList(recipe),
                getStepShortDescriptionList(recipe),
                getStepDescriptionList(recipe),
                getStepVideoUrlList(recipe),
                getStepThumbUrlList(recipe));
    }

    private List<String> getStepThumbUrlList(Recipe recipe) {
        List<String> stepThumbnailUrlList = new ArrayList<>();
        for (Step step: recipe.getStepsList()) {
            stepThumbnailUrlList.add(String.valueOf(step.getThumbnailURL()));
        }
        return stepThumbnailUrlList;
    }

    private List<String> getStepVideoUrlList(Recipe recipe) {
        List<String> stepVideoUrlList = new ArrayList<>();
        for (Step step: recipe.getStepsList()) {
            stepVideoUrlList.add(String.valueOf(step.getVideoURL()));
        }
        return stepVideoUrlList;
    }

    private List<String> getStepDescriptionList(Recipe recipe) {
        List<String> stepDescrList = new ArrayList<>();
        for (Step step: recipe.getStepsList()) {
            stepDescrList.add(String.valueOf(step.getDescription()));
        }
        return stepDescrList;
    }

    private List<String> getStepShortDescriptionList(Recipe recipe) {
        List<String> stepShortDescrList = new ArrayList<>();
        for (Step step: recipe.getStepsList()) {
            stepShortDescrList.add(String.valueOf(step.getShortDescription()));
        }
        return stepShortDescrList;
    }

    private List<String> getStepIdList(Recipe recipe) {
        List<String> stepIdList = new ArrayList<>();
        for (Step step: recipe.getStepsList()) {
            stepIdList.add(String.valueOf(step.getId()));
        }
        return stepIdList;
    }

    private List<String> getIngredientsNameList(Recipe recipe) {
        List<String> ingredientNameList = new ArrayList<>();
        for (Ingredients ingredients: recipe.getIngredientsList()) {
            ingredientNameList.add(String.valueOf(ingredients.getIngredient()));
        }
        return ingredientNameList;
    }

    private List<String> getIngredientsMeasureList(Recipe recipe) {
        List<String> measureList = new ArrayList<>();
        for (Ingredients ingredients: recipe.getIngredientsList()) {
            measureList.add(String.valueOf(ingredients.getMeasure()));
        }
        return measureList;
    }

    private List<String> getIngredientsQuantityList(Recipe recipe) {
        List<String> quantityList = new ArrayList<>();
        for (Ingredients ingredients: recipe.getIngredientsList()) {
            quantityList.add(String.valueOf(ingredients.getQuantity()));
        }
        return quantityList;
    }
}
