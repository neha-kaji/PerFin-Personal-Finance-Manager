package com.example.perfin;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpenseViewModel expenseViewModel;
    private FirebaseFirestore db;

    private boolean budgetAlertShown = false;
    private boolean savingsAlertShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔔 Notification setup
        NotificationUtil.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
        }

        expenseViewModel = new ViewModelProvider(this)
                .get(ExpenseViewModel.class);

        db = FirebaseFirestore.getInstance();

        // 🔐 Ensure user logged in
        FirebaseUtil.ensureAuth(() -> {
            loadUserFinance();
            loadExpensesFromFirestore();
        });

        observeLimits();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_expenses) {
                fragment = new ExpensesFragment();
            } else if (id == R.id.nav_analytics) {
                fragment = new AnalyticsFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else {
                return false;
            }

            loadFragment(fragment);
            return true;
        });
    }

    // ---------------- LOAD EXPENSES ----------------

    private void loadExpensesFromFirestore() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("expenses")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Expense> list = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot) {
                        Expense e = doc.toObject(Expense.class);
                        if (e != null) list.add(e);
                    }

                    expenseViewModel.setExpenses(list);
                });
    }

    // ---------------- LOAD BUDGET & SAVINGS ----------------

    private void loadUserFinance() {

        String uid = FirebaseAuth.getInstance().getUid();
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

                        if (budget != null)
                            expenseViewModel.setMonthlyBudget(budget);

                        if (savings != null)
                            expenseViewModel.setMonthlySavings(savings);
                    }
                });
    }

    // ---------------- NOTIFICATION OBSERVERS ----------------

    private void observeLimits() {

        expenseViewModel.isBudgetExceeded().observe(this, exceeded -> {
            if (Boolean.TRUE.equals(exceeded) && !budgetAlertShown) {
                NotificationUtil.showBudgetExceeded(this);
                budgetAlertShown = true;
            }
            if (Boolean.FALSE.equals(exceeded)) {
                budgetAlertShown = false;
            }
        });

        expenseViewModel.isSavingsExceeded().observe(this, exceeded -> {
            if (Boolean.TRUE.equals(exceeded) && !savingsAlertShown) {
                NotificationUtil.showSavingsAlert(this);
                savingsAlertShown = true;
            }
            if (Boolean.FALSE.equals(exceeded)) {
                savingsAlertShown = false;
            }
        });
    }

    // ---------------- NAV ----------------

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
