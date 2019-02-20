package com.movies.mybakingapp.modal;

import com.google.gson.annotations.SerializedName;

class Ingredients {
    @SerializedName("quantity")
    private int quantity;

    @SerializedName("measure")
    private String measure;

    @SerializedName("ingredient")
    private String ingredient;

}
