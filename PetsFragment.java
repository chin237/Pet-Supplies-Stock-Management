package com.example.petmanagement;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetsFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    private String selectedImageUri = null;
    private ImageView imagePreview;

    private RecyclerView petsRecyclerView;
    private TextInputEditText searchInput;
    private MaterialButton filterButton;
    private MaterialButton sortButton;
    private PetAdapter petAdapter;
    private List<Pet> petsList;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            dbHelper = new DatabaseHelper(requireContext());
            View view = inflater.inflate(R.layout.fragment_pets, container, false);
            initializeViews(view);
            checkPermissions();
            setupRecyclerView();
            setupSearch();
            setupFilterAndSort();
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return new View(requireContext());
        }
    }

    private void initializeViews(View view) {
        petsRecyclerView = view.findViewById(R.id.petsRecyclerView);
        searchInput = view.findViewById(R.id.searchInput);
        filterButton = view.findViewById(R.id.filterButton);
        sortButton = view.findViewById(R.id.sortButton);
        view.findViewById(R.id.addPetFab).setOnClickListener(v -> showAddPetDialog());
    }

    private void checkPermissions() {
        if (getContext() == null) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST
                );
            }
        } else {
            // Below Android 13 uses READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST
                );
            }
        }
    }

    private boolean hasRequiredPermissions() {
        if (getContext() == null) return false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open image picker
                openImagePicker();
            } else {
                Toast.makeText(getContext(), 
                    "Permission denied. Cannot select images without permission.", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupRecyclerView() {
        try {
            if (petsRecyclerView == null || getContext() == null) return;
            
            petsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            petsList = dbHelper.getAllPets();
            
            if (petAdapter == null) {
                petAdapter = new PetAdapter(petsList, new PetAdapter.PetClickListener() {
                    @Override
                    public void onPetClick(Pet pet) {
                        showEditPetDialog(pet);
                    }

                    @Override
                    public void onPetLongClick(Pet pet) {
                        deletePet(pet);
                    }
                });
                petsRecyclerView.setAdapter(petAdapter);
            } else {
                petAdapter.updateList(petsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading pets: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPets(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterAndSort() {
        filterButton.setOnClickListener(v -> showFilterDialog());
        sortButton.setOnClickListener(v -> showSortDialog());
    }

    private void filterPets(String query) {
        petsList = dbHelper.searchPets(query);
        petAdapter.updateList(petsList);
    }

    private void showFilterDialog() {
        String[] filters = {"All", "Available", "Sold", "Dogs", "Cats", "Others"};
        new MaterialAlertDialogBuilder(getContext())
            .setTitle("Filter Pets")
            .setItems(filters, (dialog, which) -> {
                // Implement filter logic
            })
            .show();
    }

    private void showSortDialog() {
        String[] sortOptions = {"Name (A-Z)", "Name (Z-A)", "Price (Low-High)", "Price (High-Low)"};
        new MaterialAlertDialogBuilder(getContext())
            .setTitle("Sort Pets")
            .setItems(sortOptions, (dialog, which) -> {
                // Implement sorting logic
            })
            .show();
    }

    private void showAddPetDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_pet, null);
        imagePreview = dialogView.findViewById(R.id.petImagePreview);
        MaterialButton selectImageButton = dialogView.findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(v -> openImagePicker());

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
            .setTitle("Add New Pet")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                if (savePet(dialogView, -1)) {
                    dialog.dismiss();
                    setupRecyclerView(); // Refresh list
                }
            });
        });

        dialog.show();
    }

    private void openImagePicker() {
        if (getContext() == null) return;

        if (hasRequiredPermissions()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            checkPermissions();
            Toast.makeText(getContext(), "Please grant permission to access images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData().toString();
            imagePreview.setImageURI(Uri.parse(selectedImageUri));
        }
    }

    private boolean savePet(View dialogView, long petId) {
        TextInputEditText nameInput = dialogView.findViewById(R.id.petNameInput);
        TextInputEditText breedInput = dialogView.findViewById(R.id.petBreedInput);
        TextInputEditText priceInput = dialogView.findViewById(R.id.petPriceInput);
        TextInputEditText detailsInput = dialogView.findViewById(R.id.petDetailsInput);
        SwitchMaterial availableSwitch = dialogView.findViewById(R.id.petAvailableSwitch);

        String name = nameInput.getText().toString().trim();
        String breed = breedInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();
        String details = detailsInput.getText().toString().trim();
        boolean available = availableSwitch.isChecked();

        if (name.isEmpty() || breed.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (petId == -1) {
            // Add new pet
            long newId = dbHelper.insertPet(name, breed, price, available, details, selectedImageUri);
            if (newId != -1) {
                Toast.makeText(getContext(), "Pet added successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // Update existing pet
            Pet pet = new Pet(petId, name, breed, price, available, details, selectedImageUri); // Fixed: Added imageUri parameter
            int result = dbHelper.updatePet(pet);
            if (result > 0) {
                Toast.makeText(getContext(), "Pet updated successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        Toast.makeText(getContext(), "Error saving pet", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void showEditPetDialog(Pet pet) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_pet, null);
        
        // Pre-fill the form
        ((TextInputEditText) dialogView.findViewById(R.id.petNameInput)).setText(pet.getName());
        ((TextInputEditText) dialogView.findViewById(R.id.petBreedInput)).setText(pet.getBreed());
        ((TextInputEditText) dialogView.findViewById(R.id.petPriceInput)).setText(String.valueOf(pet.getPrice()));
        ((TextInputEditText) dialogView.findViewById(R.id.petDetailsInput)).setText(pet.getDetails());
        ((SwitchMaterial) dialogView.findViewById(R.id.petAvailableSwitch)).setChecked(pet.isAvailable());

        imagePreview = dialogView.findViewById(R.id.petImagePreview);
        if (pet.getImageUri() != null && !pet.getImageUri().isEmpty()) {
            selectedImageUri = pet.getImageUri();
            imagePreview.setImageURI(Uri.parse(selectedImageUri));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
            .setTitle("Edit Pet")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                if (savePet(dialogView, pet.getId())) {
                    dialog.dismiss();
                    setupRecyclerView(); // Refresh list
                }
            });
        });

        dialog.show();
    }

    private void deletePet(Pet pet) {
        new MaterialAlertDialogBuilder(getContext())
            .setTitle("Delete Pet")
            .setMessage("Are you sure you want to delete this pet?")
            .setPositiveButton("Delete", (dialog, which) -> {
                int result = dbHelper.deletePet(pet.getId());
                if (result > 0) {
                    Toast.makeText(getContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                    setupRecyclerView(); // Refresh list
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
