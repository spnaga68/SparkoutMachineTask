package com.imtamila.sparkoutmachinetask.employees.view.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.imtamila.sparkoutmachinetask.common.CommonViewModelFactory
import com.imtamila.sparkoutmachinetask.common.obtainViewModel
import com.imtamila.sparkoutmachinetask.databinding.FragmentEmployeesBinding
import com.imtamila.sparkoutmachinetask.employees.adapter.EmployeesListAdapter
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData
import com.imtamila.sparkoutmachinetask.employees.viewModel.EmployeeViewModel
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface
import com.imtamila.sparkoutmachinetask.utils.CommonAlertDialog
import com.imtamila.sparkoutmachinetask.utils.Resource
import com.imtamila.sparkoutmachinetask.utils.Status

/**
 * A simple [Fragment] subclass.
 */
class EmployeesFragment : Fragment(), DialogOnClickInterface {
    private lateinit var binding: FragmentEmployeesBinding
    private lateinit var viewModel: EmployeeViewModel

    private val employeesListAdapter: EmployeesListAdapter by lazy { EmployeesListAdapter() }
    private var mDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeesBinding.inflate(inflater, container, false)
        viewModel = (requireActivity() as AppCompatActivity).obtainViewModel(
            EmployeeViewModel::class.java,
            CommonViewModelFactory(requireContext())
        )
        with(binding) {
            employeeViewModel = viewModel
            lifecycleOwner = this@EmployeesFragment
            executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getEmployeesList().observe(viewLifecycleOwner, Observer {
            handleEmployeeListResponse(it)
        })
        setEmployeeListAdapter()
    }

    private fun setEmployeeListAdapter() {
        binding.rvEmployeesList.apply {
            setHasFixedSize(true)
            adapter = employeesListAdapter
        }
    }

    private fun handleEmployeeListResponse(resource: Resource<EmployeeResponseData>) {
        when (resource.status) {
            Status.LOADING -> {
                viewModel.showLoading.value = true
            }
            Status.ERROR -> {
                viewModel.showLoading.value = false
                showDialog(resource.message)
            }
            else -> {
                viewModel.showLoading.value = false
                resource.data?.data?.let { employeesListAdapter.submitList(it) }
            }
        }
    }

    private fun showDialog(message: String?) {
        mDialog =
            message?.let {
                CommonAlertDialog.alertDialog(
                    requireContext(), this,
                    it, negativeButtonText = ""
                )
            }
    }

    override fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }

    override fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }
}
