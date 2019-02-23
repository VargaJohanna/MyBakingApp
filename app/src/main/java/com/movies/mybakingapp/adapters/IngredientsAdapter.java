package com.movies.mybakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.modal.Ingredients;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {
    private List<Ingredients> ingredientsList;

    public IngredientsAdapter(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public IngredientsAdapter.IngredientsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_ingredients, viewGroup, false);
        return new IngredientsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.IngredientsAdapterViewHolder viewHolder, int i) {
        String quantity = String.valueOf(ingredientsList.get(i).getQuantity());
        String measure = ingredientsList.get(i).getMeasure().toLowerCase();
        String ingredient = ingredientsList.get(i).getIngredient();
        viewHolder.quantityText.setText(String.format("%s %s %s", quantity, measure, ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView quantityText;

        public IngredientsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityText = itemView.findViewById(R.id.ingredient_text);
        }
    }
}
