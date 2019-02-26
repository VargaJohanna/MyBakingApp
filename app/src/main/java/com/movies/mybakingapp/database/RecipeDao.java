package com.movies.mybakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(RecipeEntry recipeEntry);

    @Query("SELECT * FROM recipe WHERE id = 0")
    RecipeEntry getCurrentRecipe();
}
