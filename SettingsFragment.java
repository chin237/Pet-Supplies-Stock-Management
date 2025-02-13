package com.example.petmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Setup preference clicks
        setupPreferenceClicks();
        
        // Set static version number
        Preference versionPreference = findPreference("version");
        if (versionPreference != null) {
            versionPreference.setSummary("1.0.0");
        }
    }

    private void setupPreferenceClicks() {
        // Export Data
        findPreference("export_data").setOnPreferenceClickListener(preference -> {
            exportData();
            return true;
        });

        // Import Data
        findPreference("import_data").setOnPreferenceClickListener(preference -> {
            importData();
            return true;
        });

        // Clear Data
        findPreference("clear_data").setOnPreferenceClickListener(preference -> {
            showClearDataDialog();
            return true;
        });

        // Privacy Policy
        findPreference("privacy_policy").setOnPreferenceClickListener(preference -> {
            showPrivacyPolicy();
            return true;
        });

        // Terms & Conditions
        findPreference("terms_conditions").setOnPreferenceClickListener(preference -> {
            showTermsAndConditions();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dark_mode")) {
            boolean darkMode = sharedPreferences.getBoolean(key, false);
            updateTheme(darkMode);
        } else if (key.equals("notifications")) {
            boolean notifications = sharedPreferences.getBoolean(key, true);
            updateNotifications(notifications);
        } else if (key.equals("business_name")) {
            String businessName = sharedPreferences.getString(key, "Pet Store");
            updateBusinessName(businessName);
        }
    }

    private void exportData() {
        // TODO: Implement data export
        Toast.makeText(getContext(), "Export feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void importData() {
        // TODO: Implement data import
        Toast.makeText(getContext(), "Import feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to delete all data? This action cannot be undone.")
            .setPositiveButton("Clear", (dialog, which) -> clearAllData())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void clearAllData() {
        try {
            // Clear database
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            dbHelper.clearAllData();

            // Clear preferences
            SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(requireContext()).edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "All data cleared successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error clearing data: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void showPrivacyPolicy() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Privacy Policy")
            .setMessage("Your privacy policy text here")
            .setPositiveButton("Close", null)
            .show();
    }

    private void showTermsAndConditions() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Terms & Conditions")
            .setMessage("Your terms and conditions text here")
            .setPositiveButton("Close", null)
            .show();
    }

    private void updateTheme(boolean darkMode) {
        // TODO: Implement theme switching
        Toast.makeText(getContext(), 
            "Theme will be " + (darkMode ? "dark" : "light") + " on next launch", 
            Toast.LENGTH_SHORT).show();
    }

    private void updateNotifications(boolean enabled) {
        // TODO: Implement notification handling
        Toast.makeText(getContext(), 
            "Notifications " + (enabled ? "enabled" : "disabled"), 
            Toast.LENGTH_SHORT).show();
    }

    private void updateBusinessName(String name) {
        // TODO: Implement business name update
        Toast.makeText(getContext(), 
            "Business name updated to: " + name, 
            Toast.LENGTH_SHORT).show();
    }
}
