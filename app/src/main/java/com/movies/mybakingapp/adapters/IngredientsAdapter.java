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
        viewHolder.quantityText.setText(String.valueOf(ingredientsList.get(i).getQuantity()));
        viewHolder.measureText.setText(ingredientsList.get(i).getMeasure());
        viewHolder.ingredientText.setText(ingredientsList.get(i).getIngredient());
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView quantityText;
        private final TextView measureText;
        private final TextView ingredientText;

        public IngredientsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityText = itemView.findViewById(R.id.quantity_text);
            measureText = itemView.findViewById(R.id.measure_text);
            ingredientText = itemView.findViewById(R.id.ingredient_text);
        }
    }
}
