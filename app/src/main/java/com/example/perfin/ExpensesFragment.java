package com.example.perfin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ExpenseAdapter adapter;
    private ExpenseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        recyclerView = view.findViewById(R.id.rvExpenses);
        fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity())
                .get(ExpenseViewModel.class);

        // 🔥 Adapter with edit/delete callbacks
        adapter = new ExpenseAdapter(
                new ArrayList<>(),
                new ExpenseAdapter.ExpenseActionListener() {

                    @Override
                    public void onEdit(Expense expense) {
                        AddExpenseBottomSheet sheet =
                                AddExpenseBottomSheet.newInstance(expense);
                        sheet.show(getParentFragmentManager(), "EditExpense");
                    }

                    @Override
                    public void onDelete(Expense expense) {

                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid == null || expense.getId() == null) {
                            Toast.makeText(getContext(),
                                    "Unable to delete expense",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .collection("expenses")
                                .document(expense.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    // 🔥 Update ViewModel
                                    viewModel.removeExpense(expense.getId());
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                getContext(),
                                                e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        // 🔥 Observe expenses from ViewModel
        viewModel.getExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.updateList(expenses);
        });

        fabAdd.setOnClickListener(v -> {
            AddExpenseBottomSheet sheet = new AddExpenseBottomSheet();
            sheet.show(getParentFragmentManager(), "AddExpense");
        });

        return view;
    }
}
