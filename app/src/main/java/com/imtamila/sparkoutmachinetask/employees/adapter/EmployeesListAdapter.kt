package com.imtamila.sparkoutmachinetask.employees.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imtamila.sparkoutmachinetask.databinding.EmployeeListItemBinding
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData

class EmployeesListAdapter : RecyclerView.Adapter<EmployeesListAdapter.EmployeeViewHolder>() {
    private var employeesList: ArrayList<EmployeeResponseData.EmployeeData>? = null

    internal fun submitList(employeesList: ArrayList<EmployeeResponseData.EmployeeData>) {
        this.employeesList = employeesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(
            EmployeeListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = employeesList?.size ?: 0

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        employeesList?.get(position)?.let {
            holder.bind(it, position)
        }
    }

    inner class EmployeeViewHolder(private val binding: EmployeeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(employeeData: EmployeeResponseData.EmployeeData, position: Int) {
            with(binding) {
                this.employeeData = employeeData
                executePendingBindings()
            }
        }
    }
}