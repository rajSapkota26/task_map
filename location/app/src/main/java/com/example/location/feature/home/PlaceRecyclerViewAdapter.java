package com.example.location.feature.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.location.R;
import com.example.location.databinding.SampleAddedPlaceBinding;

import java.util.List;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<AddedPlace> places;
    private OnItemClickListener onItemClickListener;

    public PlaceRecyclerViewAdapter(Context context, List<AddedPlace> places, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.places = places;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PlaceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SampleAddedPlaceBinding binding = SampleAddedPlaceBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceRecyclerViewAdapter.ViewHolder holder, int position) {
        AddedPlace place = places.get(position);
        holder.binding.placeItem.setText(place.getName());
        holder.binding.rvItem.setOnClickListener(view -> {
             onItemClickListener.onRecyclerViewItemClicked(position,place);
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SampleAddedPlaceBinding binding;

        public ViewHolder(@NonNull SampleAddedPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
