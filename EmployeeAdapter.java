package com.example.qrstaff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;

    public EmployeeAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.nameTextView.setText(employee.getName());
        holder.designationTextView.setText(employee.getDesignation());
        holder.phoneTextView.setText(employee.getPhone());
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView designationTextView;
        public TextView phoneTextView;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_employee_name);
            designationTextView = itemView.findViewById(R.id.tv_employee_designation);
            phoneTextView = itemView.findViewById(R.id.tv_employee_phone);
        }
    }
}
