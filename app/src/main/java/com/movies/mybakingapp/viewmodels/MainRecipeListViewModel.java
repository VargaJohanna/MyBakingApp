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
    private MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();

    public MainRecipeListViewModel(@NonNull Application application) {
        super(application);
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
                    public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                        recipeList.postValue(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                        recipeList.postValue(Collections.<Recipe>emptyList());
                    }
                });
            }
        });
    }
}
