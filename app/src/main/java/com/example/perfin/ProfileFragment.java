package com.example.perfin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private android.widget.TextView tvUserName, tvUserEmail;
    private EditText etSavings, etBudget;
    private ExpenseViewModel viewModel;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        etSavings = view.findViewById(R.id.etSavings);
        etBudget = view.findViewById(R.id.etBudget);
        Button btnSave = view.findViewById(R.id.btnSaveFinance);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        viewModel = new ViewModelProvider(requireActivity())
                .get(ExpenseViewModel.class);

        loadUserProfile();
        loadFinanceSettings();

        btnSave.setOnClickListener(v -> saveFinanceSettings());
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    // 🔥 Load user basic profile (name/email)
    private void loadUserProfile() {
        String uid = auth.getUid();
        if (uid == null) {
            applyUserProfile(null);
            return;
        }

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this::applyUserProfile)
                .addOnFailureListener(e -> applyUserProfile(null));
    }

    private void applyUserProfile(DocumentSnapshot doc) {
        String name = null;
        String email = null;

        if (doc != null && doc.exists()) {
            name = doc.getString("name");
            email = doc.getString("email");
        }

        FirebaseUser current = auth.getCurrentUser();
        if ((name == null || name.trim().isEmpty()) && current != null) {
            name = current.getDisplayName();
        }
        if ((email == null || email.trim().isEmpty()) && current != null) {
            email = current.getEmail();
        }

        if (name == null || name.trim().isEmpty()) name = "Guest";
        if (email == null || email.trim().isEmpty()) email = "--";

        tvUserName.setText("Name: " + name);
        tvUserEmail.setText("Email: " + email);
    }

    // 🔥 Load existing values from Firestore
    private void loadFinanceSettings() {

        String uid = auth.getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("settings")
                .document("finance")
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        Double budget = doc.getDouble("monthlyBudget");
                        Double savings = doc.getDouble("monthlySavings");

                        if (budget != null) {
                            etBudget.setText(String.valueOf(budget));
                            viewModel.setMonthlyBudget(budget);
                        }

                        if (savings != null) {
                            etSavings.setText(String.valueOf(savings));
                            viewModel.setMonthlySavings(savings);
                        }
                    }
                });
    }

    // 🔥 Save budget & savings
    private void saveFinanceSettings() {

        String budgetStr = etBudget.getText().toString().trim();
        String savingsStr = etSavings.getText().toString().trim();

        if (TextUtils.isEmpty(budgetStr) || TextUtils.isEmpty(savingsStr)) {
            Toast.makeText(getContext(), "Enter both values", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = Double.parseDouble(budgetStr);
        double savings = Double.parseDouble(savingsStr);

        String uid = auth.getUid();
        if (uid == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("monthlyBudget", budget);
        data.put("monthlySavings", savings);

        db.collection("users")
                .document(uid)
                .collection("settings")
                .document("finance")
                .set(data)
                .addOnSuccessListener(unused -> {

                    viewModel.setMonthlyBudget(budget);
                    viewModel.setMonthlySavings(savings);

                    Toast.makeText(getContext(),
                            "Budget updated successfully",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // 🔓 Logout
    private void logoutUser() {

        auth.signOut();

        GoogleSignIn.getClient(requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
