package com.example.perfin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    // ✅ THIS WAS MISSING — ADD THIS
    public interface ExpenseActionListener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    private List<Expense> expenseList;
    private final ExpenseActionListener listener;

    public ExpenseAdapter(List<Expense> expenseList,
                          ExpenseActionListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense expense = expenseList.get(position);

        holder.tvTitle.setText(expense.getTitle());
        holder.tvCategory.setText(expense.getCategory());
        holder.tvAmount.setText("₹ " + expense.getAmount());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(expense));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void updateList(List<Expense> newList) {
        expenseList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvAmount;
        ImageView btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
