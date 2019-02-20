package com.movies.mybakingapp.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class Recipe {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("ingredients")
    private List<Ingredients> ingredientsList;

    @SerializedName("steps")
    private List<Steps> stepsList;

    @SerializedName("servings")
    private int servings;

    @SerializedName("image")
    private String recipeImage;

}
