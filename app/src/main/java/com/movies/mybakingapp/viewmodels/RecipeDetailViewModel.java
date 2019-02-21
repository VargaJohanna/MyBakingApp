package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.movies.mybakingapp.modal.Ingredients;
import com.movies.mybakingapp.network.GetRecipesService;
import com.movies.mybakingapp.utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailViewModel extends AndroidViewModel {

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
    }


}
