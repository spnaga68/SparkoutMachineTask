package com.imtamila.sparkoutmachinetask

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.imtamila.sparkoutmachinetask.databinding.ActivityExpandableListBinding

class ExpandableListActivity : AppCompatActivity() {
    private val expandableListAdapter: ExpandableListAdapter by lazy { ExpandableListAdapter(this) }

    private lateinit var binding: ActivityExpandableListBinding
    private lateinit var viewModel: EquipmentListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expandable_list)
        viewModel = ViewModelProvider(
            this,
            EquipmentListViewModelFactory(this)
        ).get(EquipmentListViewModel::class.java)
        with(binding) {
            equipmentListViewModel = viewModel
            lifecycleOwner = this@ExpandableListActivity
            executePendingBindings()
        }
        subscribeObserver()
    }

    override fun onStart() {
        super.onStart()
        binding.rvExpandableList.apply {
            adapter = expandableListAdapter
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getEquipmentsList()
    }

    private fun subscribeObserver() {
        viewModel.equipmentResponseLiveData.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    1 -> {
                        expandableListAdapter.submitList(it.Equipment)
                    }
                    else -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}