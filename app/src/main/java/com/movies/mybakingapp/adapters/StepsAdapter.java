package com.movies.mybakingapp.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.mybakingapp.R;
import com.movies.mybakingapp.modal.Steps;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsAdapterViewHolder> {
    List<Steps> stepsList;
    final private ItemClickListenerSteps itemClickListenerSteps;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public StepsAdapter(List<Steps> stepsList, ItemClickListenerSteps itemClickListenerSteps) {
        this.stepsList = stepsList;
        this.itemClickListenerSteps = itemClickListenerSteps;
    }

    @NonNull
    @Override
    public StepsAdapter.StepsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_steps, viewGroup, false);
        return new StepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsAdapter.StepsAdapterViewHolder viewHolder, int i) {
        viewHolder.stepShortDescription.setText(stepsList.get(i).getShortDescription());
        viewHolder.stepShortDescription.setSelected(selectedPosition == i);
        if(selectedPosition == i) {
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.stepShortDescription.getContext(), R.color.colorAccent));
            viewHolder.stepShortDescription.setTextColor(ContextCompat.getColor(viewHolder.stepShortDescription.getContext(), R.color.primary_text));
        } else {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.stepShortDescription.setTextColor(ContextCompat.getColor(viewHolder.stepShortDescription.getContext(), R.color.secondary_text));
        }
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    public class StepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView stepShortDescription;

        public StepsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            stepShortDescription = itemView.findViewById(R.id.step_short_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            notifyItemChanged(selectedPosition);
            selectedPosition = getLayoutPosition();
            notifyItemChanged(selectedPosition);
            itemClickListenerSteps.onItemClick(stepsList.get(getAdapterPosition()));

        }
    }

    public interface ItemClickListenerSteps {
        void onItemClick(Steps step);
    }
}
