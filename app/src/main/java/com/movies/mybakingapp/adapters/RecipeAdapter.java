package com.movies.mybakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.modal.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {
    private List<Recipe> recipeList;
    final private ItemClickListener itemClickListener;

    public RecipeAdapter(List<Recipe> recipeList, ItemClickListener itemClickListener) {
        this.recipeList = recipeList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_recipe, viewGroup, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterViewHolder holder, int i) {
        holder.recipeName.setText(recipeList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void updateList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView recipeName;

        RecipeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(recipeList.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(Recipe recipe, int recipePosition);
    }
}
