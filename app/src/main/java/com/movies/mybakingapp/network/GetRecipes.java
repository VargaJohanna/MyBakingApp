package com.movies.mybakingapp.network;

import com.movies.mybakingapp.modal.RecipeListModal;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetRecipes {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<RecipeListModal> getRecipeList();
}
