package com.example.petmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.petmanagement.utils.CurrencyFormatter;

public class HomeFragment extends Fragment {
    private TextView totalPetsCount;
    private TextView petsCountTrend;
    private TextView lastUpdateTime;
    private RecyclerView recentActivityList;
    // Add new view references
    private TextView stockCount;
    private TextView stockStatus;
    private TextView monthlyRevenue;
    private TextView revenueTrend;
    private TextView lowStockCount;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        initializeViews(view);
        setupDashboard();
        return view;
    }

    private void initializeViews(View view) {
        totalPetsCount = view.findViewById(R.id.totalPetsCount);
        petsCountTrend = view.findViewById(R.id.petsCountTrend);
        lastUpdateTime = view.findViewById(R.id.lastUpdateTime);
        recentActivityList = view.findViewById(R.id.recentActivityList);

        MaterialButton addPetButton = view.findViewById(R.id.addPetButton);
        addPetButton.setOnClickListener(v -> navigateToPets());

        // Initialize new views
        stockCount = view.findViewById(R.id.stockCount);
        stockStatus = view.findViewById(R.id.stockStatus);
        monthlyRevenue = view.findViewById(R.id.monthlyRevenue);
        revenueTrend = view.findViewById(R.id.revenueTrend);
        lowStockCount = view.findViewById(R.id.lowStockCount);
    }

    private void setupDashboard() {
        // Update last refresh time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        lastUpdateTime.setText("Last updated: " + sdf.format(new Date()));

        // Update metrics
        updateMetrics();

        // Setup recent activity
        setupRecentActivity();
    }

    private void updateMetrics() {
        try {
            // Total Pets
            int totalPets = dbHelper.getTotalPetsCount();
            int availablePets = dbHelper.getAvailablePetsCount();
            int soldPets = dbHelper.getSoldPetsCount();
            double totalRevenue = dbHelper.getTotalRevenue();
            
            // Update UI
            totalPetsCount.setText(String.valueOf(totalPets));
            
            // Calculate percentage change (example: based on sold vs available)
            float percentChange = totalPets > 0 ? 
                    (float) soldPets / totalPets * 100 : 0;
            petsCountTrend.setText(String.format("%.1f%% sold", percentChange));
            
            // Stock count (available pets)
            stockCount.setText(String.valueOf(availablePets));
            stockStatus.setText("Pets available");
            
            // Revenue
            monthlyRevenue.setText(CurrencyFormatter.formatFCFA(totalRevenue));
            
            // Revenue trend (example calculation)
            double averagePrice = totalPets > 0 ? totalRevenue / soldPets : 0;
            revenueTrend.setText("Avg. " + CurrencyFormatter.formatFCFA(averagePrice) + " per pet");
            
            // Low stock alert (if available pets are less than 5)
            int lowStockAlert = availablePets < 5 ? availablePets : 0;
            lowStockCount.setText(String.valueOf(lowStockAlert));
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), 
                "Error updating metrics: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecentActivity() {
        recentActivityList.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set up RecyclerView adapter with actual data
    }

    private void navigateToPets() {
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new PetsFragment())
            .addToBackStack(null)
            .commit();
    }
}
