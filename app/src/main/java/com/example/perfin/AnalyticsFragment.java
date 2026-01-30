package com.example.perfin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment {

    private ExpenseViewModel viewModel;
    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        pieChart = view.findViewById(R.id.pieChart);

        viewModel = new ViewModelProvider(requireActivity())
                .get(ExpenseViewModel.class);

        observeExpenses();

        return view;
    }

    private void observeExpenses() {
        viewModel.getExpenses().observe(getViewLifecycleOwner(), this::setupPieChart);
    }

    private void setupPieChart(List<Expense> expenses) {

        if (expenses == null || expenses.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No expenses to display");
            return;
        }

        // 🔥 Category aggregation
        Map<String, Float> categoryMap = new HashMap<>();

        for (Expense e : expenses) {
            String category = e.getCategory();
            float amount = (float) e.getAmount();

            float current = categoryMap.getOrDefault(category, 0f);
            categoryMap.put(category, current + amount);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (String category : categoryMap.keySet()) {
            entries.add(new PieEntry(categoryMap.get(category), category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setColors(new int[]{
                Color.parseColor("#4CAF50"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#F44336"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#03A9F4")
        });

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);

        pieChart.invalidate(); // refresh
    }
}
