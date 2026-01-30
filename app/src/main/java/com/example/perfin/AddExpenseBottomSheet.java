package com.example.perfin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddExpenseBottomSheet extends BottomSheetDialogFragment {

    private EditText etTitle, etAmount;
    private Spinner spinnerCategory;
    private Button btnSave;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Expense editingExpense; // 🔥 EDIT MODE

    // 🔹 Factory method for edit mode
    public static AddExpenseBottomSheet newInstance(Expense expense) {
        AddExpenseBottomSheet sheet = new AddExpenseBottomSheet();
        sheet.editingExpense = expense;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.bottomsheet_add_expense, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        spinnerCategory = view.findViewById(R.id.spCategory);
        btnSave = view.findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupCategorySpinner();

        // 🔥 Pre-fill fields if editing
        if (editingExpense != null) {
            etTitle.setText(editingExpense.getTitle());
            etAmount.setText(String.valueOf(editingExpense.getAmount()));
            setSpinnerSelection(editingExpense.getCategory());
            btnSave.setText("Update Expense");
        }

        btnSave.setOnClickListener(v -> saveExpense());

        return view;
    }

    // ---------------- CATEGORY SPINNER ----------------

    private void setupCategorySpinner() {
        String[] categories = {
                "Food",
                "Transport",
                "Shopping",
                "Bills",
                "Entertainment",
                "Health",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategory.setAdapter(adapter);
    }

    private void setSpinnerSelection(String category) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
        int position = adapter.getPosition(category);
        if (position >= 0) spinnerCategory.setSelection(position);
    }

    // ---------------- SAVE / UPDATE EXPENSE ----------------

    private void saveExpense() {

        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        btnSave.setEnabled(false);

        ExpenseViewModel viewModel =
                new ViewModelProvider(requireActivity())
                        .get(ExpenseViewModel.class);

        // 🔥 EDIT MODE
        if (editingExpense != null && editingExpense.getId() != null) {

            Expense updated = new Expense(
                    editingExpense.getId(),
                    title,
                    category,
                    amount,
                    editingExpense.getTimestamp()
            );

            db.collection("users")
                    .document(uid)
                    .collection("expenses")
                    .document(editingExpense.getId())
                    .set(updated)
                    .addOnSuccessListener(unused -> {

                        viewModel.updateExpense(updated);
                        Toast.makeText(getContext(),
                                "Expense updated",
                                Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });

        } else {
            // 🔥 ADD MODE

            Expense expense = new Expense(
                    title,
                    category,
                    amount,
                    System.currentTimeMillis()
            );

            db.collection("users")
                    .document(uid)
                    .collection("expenses")
                    .add(expense)
                    .addOnSuccessListener(doc -> {

                        expense.setId(doc.getId()); // 🔥 CRITICAL
                        viewModel.addExpense(expense);

                        Toast.makeText(getContext(),
                                "Expense added",
                                Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }
}
