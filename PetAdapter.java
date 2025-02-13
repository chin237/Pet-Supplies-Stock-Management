package com.example.petmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;
import java.util.ArrayList;
import android.net.Uri;
import com.example.petmanagement.utils.CurrencyFormatter;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> pets;
    private PetClickListener clickListener;

    public interface PetClickListener {
        void onPetClick(Pet pet);
        void onPetLongClick(Pet pet);
    }

    public PetAdapter(List<Pet> pets, PetClickListener listener) {
        this.pets = pets != null ? pets : new ArrayList<>();
        this.clickListener = listener;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.bind(pet);
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPetClick(pet);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPetLongClick(pet);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public void updateList(List<Pet> newList) {
        this.pets = newList;
        notifyDataSetChanged();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        private ImageView petImage;
        private TextView petName;
        private TextView petBreed;
        private TextView petPrice;
        private Chip statusChip;

        public PetViewHolder(View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.petImage);
            petName = itemView.findViewById(R.id.petName);
            petBreed = itemView.findViewById(R.id.petBreed);
            petPrice = itemView.findViewById(R.id.petPrice);
            statusChip = itemView.findViewById(R.id.statusChip);
        }

        public void bind(Pet pet) {
            petName.setText(pet.getName());
            petBreed.setText(pet.getBreed());
            petPrice.setText(CurrencyFormatter.formatFCFA(pet.getPrice()));
            
            statusChip.setText(pet.isAvailable() ? "Available" : "Sold");
            statusChip.setChipBackgroundColorResource(
                pet.isAvailable() ? R.color.success_green : R.color.text_secondary
            );

            if (pet.getImageUri() != null && !pet.getImageUri().isEmpty()) {
                petImage.setImageURI(Uri.parse(pet.getImageUri()));
            } else {
                petImage.setImageResource(R.drawable.ic_baseline_pets_24);
            }
        }
    }
}
