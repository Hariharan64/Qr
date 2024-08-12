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

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView employeeName, employeeNumber, employeeId, phoneNumber;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.textViewEmployeeName);
            employeeNumber = itemView.findViewById(R.id.textViewEmployeeNumber);
            employeeId = itemView.findViewById(R.id.textViewEmployeeId);
            phoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
        }
    }

    public EmployeeAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_item, parent, false);
        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.employeeName.setText(employee.getEmployeeName());
        holder.employeeNumber.setText(employee.getEmployeeNumber());
        holder.employeeId.setText(employee.getEmployeeId());
        holder.phoneNumber.setText(employee.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }
}
