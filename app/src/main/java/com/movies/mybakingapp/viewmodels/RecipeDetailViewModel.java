package com.movies.mybakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.movies.mybakingapp.database.AppDatabase;
import com.movies.mybakingapp.database.RecipeEntry;
import com.movies.mybakingapp.modal.Ingredients;
import com.movies.mybakingapp.modal.Recipe;
import com.movies.mybakingapp.modal.Step;
import com.movies.mybakingapp.utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailViewModel extends AndroidViewModel {
    private MutableLiveData<Step> currentStep = new MutableLiveData<>();
    private int currentStepPosition;
    private MutableLiveData<Recipe> liveRecipe = new MutableLiveData<>();
    private Recipe lastAvailableRecipe;

    private int selectedStepPosition = RecyclerView.NO_POSITION;
    private FragmentManager fragmentManager;
    private boolean twoPaneMode;
    private String stepLongDescription;
    private String videoURL;
    private String thumbnailURL;
    private boolean isStepClicked;
    private AppDatabase database;
    private long currentPlayerPosition;
    private boolean isPlayerPlaying = true;
    private SimpleExoPlayer player;

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public void setCurrentRecipeValueFromDB() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                liveRecipe.postValue(convertRecipeEntryToRecipe(database.recipeDao().getCurrentRecipe()));
                lastAvailableRecipe = convertRecipeEntryToRecipe(database.recipeDao().getCurrentRecipe());
            }
        });
    }

    public Recipe getLatestAvailableRecipe() {
        return lastAvailableRecipe;
    }

    public LiveData<Recipe> getCurrentRecipe() {
        return liveRecipe;
    }

    // Recipe Data
    private Recipe convertRecipeEntryToRecipe(RecipeEntry entry) {
        return new Recipe(
                entry.getRecipeId(),
                entry.getName(),
                getIngredientsList(entry),
                getStepsListFromEntry(entry),
                entry.getServings(),
                entry.getRecipeImage());
    }

    private List<Step> getStepsListFromEntry(RecipeEntry entry) {
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < entry.getStepIdList().size(); i++) {
            steps.add(new Step(Integer.valueOf(entry.getStepIdList().get(i)),
                    entry.getStepShortDescriptionList().get(i),
                    entry.getStepLongDescriptionList().get(i),
                    entry.getStepVideoUrlList().get(i),
                    entry.getStepThumbnailUrlList().get(i)));
        }
        return steps;
    }

    private List<Ingredients> getIngredientsList(RecipeEntry entry) {
        List<Ingredients> ingredients = new ArrayList<>();
        for (int i = 0; i < entry.getIngredientsQuantityList().size(); i++) {
            ingredients.add(new Ingredients(Float.valueOf(entry.getIngredientsQuantityList().get(i)),
                    entry.getIngredientsMeasureList().get(i),
                    entry.getIngredientsNameList().get(i)));
        }
        return ingredients;
    }

    // Step Data
    public LiveData<Step> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStepDetails(Step currentStep) {
        setThumbnailURL(currentStep.getThumbnailURL());
        setVideoURL(currentStep.getVideoURL());
        setStepLongDescription(currentStep.getDescription());
        this.currentStep.setValue(currentStep);
    }

    public int getCurrentStepPosition() {
        return currentStepPosition;
    }

    public void setCurrentStepPosition(int currentStepPosition) {
        this.currentStepPosition = currentStepPosition;
    }

    public int getSelectedStepPosition() {
        return selectedStepPosition;
    }

    public void setSelectedStepPosition(int selectedStepPosition) {
        this.selectedStepPosition = selectedStepPosition;
    }

    public boolean isStepClicked() {
        return isStepClicked;
    }

    public void setStepClicked(boolean stepClicked) {
        isStepClicked = stepClicked;
    }

    // Fragment Manager
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    // Share the information about two pane view with the fragments
    public boolean isTwoPaneMode() {
        return twoPaneMode;
    }

    public void setTwoPaneMode(boolean twoPaneMode) {
        this.twoPaneMode = twoPaneMode;
    }

    // Data from current step
    public String getStepLongDescription() {
        return stepLongDescription;
    }

    public void setStepLongDescription(String stepLongDescription) {
        this.stepLongDescription = stepLongDescription;
    }

    private String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    private String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public boolean isMediaAvailableForStep() {
        return !getThumbnailURL().isEmpty() || !getVideoURL().isEmpty();
    }

    public Uri getUri() {
        if (!getVideoURL().isEmpty() && !getThumbnailURL().isEmpty()) {
            return Uri.parse(getVideoURL());
        } else if (!getThumbnailURL().isEmpty()) {
            return Uri.parse(getThumbnailURL());
        } else if (!getVideoURL().isEmpty()) {
            return Uri.parse(getVideoURL());
        } else {
            return Uri.EMPTY;
        }
    }

    public boolean isUrlMp4() {
        return getThumbnailURL().contains(".mp4") || getVideoURL().contains(".mp4");
    }

    public long getCurrentPlayerPosition() {
        return currentPlayerPosition;
    }

    public void setCurrentPlayerPosition(long currentPlayerPosition) {
        this.currentPlayerPosition = currentPlayerPosition;
    }

    public boolean isPlayerPlaying() {
        return isPlayerPlaying;
    }

    public void setPlayerPlaying(boolean playerPlaying) {
        isPlayerPlaying = playerPlaying;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }
}
