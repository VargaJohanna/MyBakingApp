package com.movies.mybakingapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.io.InputStream;
import java.util.List;

@Entity(tableName = "recipe")
public class RecipeEntry {
    @PrimaryKey()
    private int id;
    private String name;
    private int recipeId;
    private int servings;
    @ColumnInfo(name = "recipe_image")
    private String recipeImage;

    // Ingredients
    @ColumnInfo(name = "ingredients_quantity_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> ingredientsQuantityList;

    @ColumnInfo(name = "ingredients_measure_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> ingredientsMeasureList;

    @ColumnInfo(name = "ingredients_name_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> ingredientsNameList;

    //Steps
    @ColumnInfo(name = "step_id_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> stepIdList;

    @ColumnInfo(name = "step_short_description_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> stepShortDescriptionList;

    @ColumnInfo(name = "step_long_description_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> stepLongDescriptionList;

    @ColumnInfo(name = "step_video_url_list")
    @TypeConverters(ListTypeConverters.class)

    private List<String> stepVideoUrlList;

    public int getId() {
        return id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public List<String> getIngredientsQuantityList() {
        return ingredientsQuantityList;
    }

    public void setIngredientsQuantityList(List<String> ingredientsQuantityList) {
        this.ingredientsQuantityList = ingredientsQuantityList;
    }

    public List<String> getIngredientsMeasureList() {
        return ingredientsMeasureList;
    }

    public void setIngredientsMeasureList(List<String> ingredientsMeasureList) {
        this.ingredientsMeasureList = ingredientsMeasureList;
    }

    public List<String> getIngredientsNameList() {
        return ingredientsNameList;
    }

    public void setIngredientsNameList(List<String> ingredientsNameList) {
        this.ingredientsNameList = ingredientsNameList;
    }

    public List<String> getStepIdList() {
        return stepIdList;
    }

    public void setStepIdList(List<String> stepIdList) {
        this.stepIdList = stepIdList;
    }

    public List<String> getStepShortDescriptionList() {
        return stepShortDescriptionList;
    }

    public void setStepShortDescriptionList(List<String> stepShortDescriptionList) {
        this.stepShortDescriptionList = stepShortDescriptionList;
    }

    public List<String> getStepLongDescriptionList() {
        return stepLongDescriptionList;
    }

    public void setStepLongDescriptionList(List<String> stepLongDescriptionList) {
        this.stepLongDescriptionList = stepLongDescriptionList;
    }

    public List<String> getStepVideoUrlList() {
        return stepVideoUrlList;
    }

    public void setStepVideoUrlList(List<String> stepVideoUrlList) {
        this.stepVideoUrlList = stepVideoUrlList;
    }

    public List<String> getStepThumbnailUrlList() {
        return stepThumbnailUrlList;
    }

    public void setStepThumbnailUrlList(List<String> stepThumbnailUrlList) {
        this.stepThumbnailUrlList = stepThumbnailUrlList;
    }

    @ColumnInfo(name = "step_thumbnail_url_list")
    @TypeConverters(ListTypeConverters.class)
    private List<String> stepThumbnailUrlList;

    @Ignore
    public RecipeEntry(String name,
                       int recipeId,
                       int servings,
                       String recipeImage,
                       List<String> ingredientsQuantityList,
                       List<String> ingredientsMeasureList,
                       List<String> ingredientsNameList,
                       List<String> stepIdList,
                       List<String> stepShortDescriptionList,
                       List<String> stepLongDescriptionList,
                       List<String> stepVideoUrlList,
                       List<String> stepThumbnailUrlList){
        this.name = name;
        this.recipeId = recipeId;
        this.servings = servings;
        this.recipeImage = recipeImage;
        this.ingredientsQuantityList = ingredientsQuantityList;
        this.ingredientsMeasureList = ingredientsMeasureList;
        this.ingredientsNameList = ingredientsNameList;
        this.stepIdList = stepIdList;
        this.stepShortDescriptionList = stepShortDescriptionList;
        this.stepLongDescriptionList = stepLongDescriptionList;
        this.stepVideoUrlList = stepVideoUrlList;
        this.stepThumbnailUrlList = stepThumbnailUrlList;
    }

    public RecipeEntry(int id,
                       int recipeId,
                       String name,
                       int servings,
                       String recipeImage,
                       List<String> ingredientsQuantityList,
                       List<String> ingredientsMeasureList,
                       List<String> ingredientsNameList,
                       List<String> stepIdList,
                       List<String> stepShortDescriptionList,
                       List<String> stepLongDescriptionList,
                       List<String> stepVideoUrlList,
                       List<String> stepThumbnailUrlList){
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.servings = servings;
        this.recipeImage = recipeImage;
        this.ingredientsQuantityList = ingredientsQuantityList;
        this.ingredientsMeasureList = ingredientsMeasureList;
        this.ingredientsNameList = ingredientsNameList;
        this.stepIdList = stepIdList;
        this.stepShortDescriptionList = stepShortDescriptionList;
        this.stepLongDescriptionList = stepLongDescriptionList;
        this.stepVideoUrlList = stepVideoUrlList;
        this.stepThumbnailUrlList = stepThumbnailUrlList;
    }

}
