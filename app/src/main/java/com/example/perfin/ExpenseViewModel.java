package com.example.perfin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExpenseViewModel extends ViewModel {

    // ---------------- EXPENSES ----------------

    private final MutableLiveData<List<Expense>> expenses =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Expense>> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> list) {
        expenses.setValue(list != null ? new ArrayList<>(list) : new ArrayList<>());
        recalculate();
    }

    public void addExpense(Expense expense) {
        List<Expense> current = expenses.getValue();
        List<Expense> updated = current != null
                ? new ArrayList<>(current)
                : new ArrayList<>();

        updated.add(expense);
        expenses.setValue(updated);
        recalculate();
    }

    public double getTotalAmount() {
        double total = 0;
        List<Expense> list = expenses.getValue();
        if (list != null) {
            for (Expense e : list) {
                total += e.getAmount();
            }
        }
        return total;
    }

    // ---------------- BUDGET & SAVINGS ----------------

    private final MutableLiveData<Double> monthlyBudget =
            new MutableLiveData<>(0.0);

    private final MutableLiveData<Double> monthlySavings =
            new MutableLiveData<>(0.0);

    private final MutableLiveData<Double> effectiveBudget =
            new MutableLiveData<>(0.0);

    // ---------------- ALERT STATES ----------------

    private final MutableLiveData<Boolean> budgetExceeded =
            new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> savingsExceeded =
            new MutableLiveData<>(false);

    // ---------------- GETTERS ----------------

    public LiveData<Double> getMonthlyBudget() {
        return monthlyBudget;
    }

    public LiveData<Double> getMonthlySavings() {
        return monthlySavings;
    }

    public LiveData<Double> getEffectiveBudget() {
        return effectiveBudget;
    }

    public LiveData<Boolean> isBudgetExceeded() {
        return budgetExceeded;
    }

    public LiveData<Boolean> isSavingsExceeded() {
        return savingsExceeded;
    }

    // ---------------- SETTERS ----------------

    public void setMonthlyBudget(double budget) {
        monthlyBudget.setValue(budget);
        recalculate();
    }

    public void setMonthlySavings(double savings) {
        monthlySavings.setValue(savings);
        recalculate();
    }

    // ---------------- CORE LOGIC ----------------

    private void recalculate() {

        double budget = monthlyBudget.getValue() != null
                ? monthlyBudget.getValue()
                : 0.0;

        double savings = monthlySavings.getValue() != null
                ? monthlySavings.getValue()
                : 0.0;

        double spent = getTotalAmount();

        // 🔹 Effective budget = Budget − Savings
        double usableBudget = budget - savings;
        if (usableBudget < 0) usableBudget = 0;

        effectiveBudget.setValue(usableBudget);

        // 🔔 Breach detection
        budgetExceeded.setValue(
                usableBudget > 0 && spent > usableBudget
        );

        savingsExceeded.setValue(
                savings > 0 && spent > savings
        );
    }

    // ---------------- DELETE ----------------

    public void removeExpense(String expenseId) {
        if (expenseId == null) return;

        List<Expense> list = expenses.getValue();
        if (list == null) return;

        List<Expense> updated = new ArrayList<>();
        for (Expense e : list) {
            if (e.getId() != null && !e.getId().equals(expenseId)) {
                updated.add(e);
            }
        }

        expenses.setValue(updated);
        recalculate();
    }

    // ---------------- UPDATE ----------------

    public void updateExpense(Expense updatedExpense) {
        List<Expense> current = expenses.getValue();
        if (current == null) return;

        List<Expense> updatedList = new ArrayList<>();

        for (Expense e : current) {
            if (e.getId().equals(updatedExpense.getId())) {
                updatedList.add(updatedExpense);
            } else {
                updatedList.add(e);
            }
        }

        expenses.setValue(updatedList); // 🔥 NEW LIST forces observers
        recalculate(); // 🔥 Recalculate budget & savings
    }

}
