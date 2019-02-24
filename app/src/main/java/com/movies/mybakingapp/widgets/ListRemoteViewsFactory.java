package com.movies.mybakingapp.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.activities.MainRecipeListActivity;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.network.GetRecipesService;
import com.movies.mybakingapp.network.RetrofitInstance;
import com.movies.mybakingapp.utilities.AppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Recipe> recipeList;
    private int recipePosition = 0;

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        recipePosition = MainRecipeListActivity.recipePosition;
        fetchRecipeData();
    }

    private void fetchRecipeData() {
        final GetRecipesService service = RetrofitInstance.getInstance().create(GetRecipesService.class);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                Call<List<Recipe>> call = service.getRecipeList();
                call.enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                        recipeList = response.body();
                        Log.d("RECIPESERVICE", "Success");
                    }

                    @Override
                    public void onFailure(Call<List<Recipe>> call, Throwable t) {
                        //Handle failure
                        Log.d("RECIPESERVICE", "Failure" + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        recipePosition = MainRecipeListActivity.recipePosition;
        if (recipeList != null) {
            return recipeList.get(recipePosition).getIngredientsList().size();
        } else return 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public RemoteViews getViewAt(int i) {
        float quantity = recipeList.get(recipePosition).getIngredientsList().get(i).getQuantity();
        String measure = recipeList.get(recipePosition).getIngredientsList().get(i).getMeasure().toLowerCase();
        String ingredient = recipeList.get(recipePosition).getIngredientsList().get(i).getIngredient();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.widget_ingredient_text, String.format("%.1f %s %s", quantity, measure, ingredient));
        views.setTextViewText(R.id.widget_recipe_name, recipeList.get(recipePosition).getName());
        views.setViewVisibility(R.id.widget_recipe_name, View.GONE);

        if (i == 0) {
            views.setViewVisibility(R.id.widget_recipe_name, View.VISIBLE);
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
