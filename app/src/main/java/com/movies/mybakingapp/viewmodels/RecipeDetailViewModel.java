package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Steps;

public class RecipeDetailViewModel extends AndroidViewModel {
    private static RecipeDetailViewModel instance;
    public static RecipeDetailViewModel getInstance(Context context) {
        if(instance == null) {
            instance = ViewModelProviders.of((FragmentActivity) context).get(RecipeDetailViewModel.class);
        }
        return instance;
    }
    private MutableLiveData<Steps> currentStep;
    private Recipe currentRecipe;

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        this.currentStep = new MutableLiveData<>();
    }

    public LiveData<Steps> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Steps currentStep) {
        this.currentStep.setValue(currentStep);
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }
}
