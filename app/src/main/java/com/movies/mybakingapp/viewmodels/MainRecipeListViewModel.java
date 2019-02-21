package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.network.GetRecipesService;
import com.movies.mybakingapp.utilities.AppExecutors;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRecipeListViewModel extends AndroidViewModel {
    MutableLiveData<List<Recipe>> recipeList;

    public MainRecipeListViewModel(@NonNull Application application) {
        super(application);
        this.recipeList = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return recipeList;
    }

    public void fetchRecipeList(final GetRecipesService service) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                Call<List<Recipe>> call = service.getRecipeList();
                call.enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                        recipeList.postValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Recipe>> call, Throwable t) {
                        recipeList.postValue(Collections.<Recipe>emptyList());
                    }
                });
            }
        });
    }
}
