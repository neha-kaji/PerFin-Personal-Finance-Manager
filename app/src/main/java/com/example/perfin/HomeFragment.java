package com.example.perfin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private TextView tvTotalSpent;
    private TextView tvBudget;
    private TextView tvSavings;
    private TextView tvEffectiveBudget;
    private TextView tvBudgetRemaining;

    private ExpenseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 Bind Views
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvBudget = view.findViewById(R.id.tvBudget);
        tvSavings = view.findViewById(R.id.tvSavings);
        tvEffectiveBudget = view.findViewById(R.id.tvEffectiveBudget);
        tvBudgetRemaining = view.findViewById(R.id.tvBudgetRemaining);

        // 🔹 Shared ViewModel (Activity scope)
        viewModel = new ViewModelProvider(requireActivity())
                .get(ExpenseViewModel.class);

        observeViewModel();

        return view;
    }

    private void observeViewModel() {

        // 🔥 Any expense change → update everything
        viewModel.getExpenses().observe(getViewLifecycleOwner(), list -> updateUI());

        // 🔥 Budget change
        viewModel.getMonthlyBudget().observe(getViewLifecycleOwner(), budget -> updateUI());

        // 🔥 Savings change
        viewModel.getMonthlySavings().observe(getViewLifecycleOwner(), savings -> updateUI());

        // 🔥 Effective budget (Budget − Savings) change
        viewModel.getEffectiveBudget().observe(getViewLifecycleOwner(), effective -> updateUI());
    }

    private void updateUI() {

        double spent = viewModel.getTotalAmount();

        Double budget = viewModel.getMonthlyBudget().getValue();
        Double savings = viewModel.getMonthlySavings().getValue();
        Double effectiveBudget = viewModel.getEffectiveBudget().getValue();

        // ---------------- TOTAL SPENT ----------------
        tvTotalSpent.setText("₹ " + spent);

        // ---------------- MONTHLY BUDGET ----------------
        if (budget != null && budget > 0) {
            tvBudget.setText("₹ " + budget);
        } else {
            tvBudget.setText("Not set");
        }

        // ---------------- SAVINGS ----------------
        if (savings != null && savings > 0) {
            tvSavings.setText("₹ " + savings);
        } else {
            tvSavings.setText("Not set");
        }

        // ---------------- EFFECTIVE BUDGET ----------------
        if (effectiveBudget != null && effectiveBudget > 0) {

            tvEffectiveBudget.setText("₹ " + effectiveBudget);

            double remaining = effectiveBudget - spent;
            if (remaining < 0) remaining = 0;

            tvBudgetRemaining.setText("₹ " + remaining);

        } else {
            tvEffectiveBudget.setText("--");
            tvBudgetRemaining.setText("--");
        }
    }
}
