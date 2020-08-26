package com.imtamila.sparkoutmachinetask.employees.view.fragments

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
import com.imtamila.sparkoutmachinetask.databinding.FragmentFavouritesBinding
import com.imtamila.sparkoutmachinetask.employees.adapter.ADAPTER_TYPE_FAVORITES_LIST
import com.imtamila.sparkoutmachinetask.employees.adapter.ContactAdapter
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.employees.interfaces.OnFavoriteChanged
import com.imtamila.sparkoutmachinetask.employees.viewModel.FavoritesViewModel
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment(), OnFavoriteChanged, DialogOnClickInterface {
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var mObserver: Observer<List<ContactData>>

    private val contactListAdapter: ContactAdapter by lazy {
        ContactAdapter(
            requireActivity(),
            this, ADAPTER_TYPE_FAVORITES_LIST
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        viewModel = (requireActivity() as AppCompatActivity).obtainViewModel(
            FavoritesViewModel::class.java,
            CommonViewModelFactory(requireContext(), 2)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = this@FavouritesFragment
            executePendingBindings()
        }
        setAdapter()
        subscribeObservers()
    }

    private fun setAdapter() {
        binding.mRecyclerView.apply {
            setHasFixedSize(true)
            adapter = contactListAdapter
        }
    }

    private fun subscribeObservers() {
        mObserver = Observer {
            if (!it.isNullOrEmpty()) {
                contactListAdapter.submitList(it as ArrayList<ContactData>)
            }
        }
        viewModel.loadAllFavoriteContacts().observe(viewLifecycleOwner, mObserver)
    }

    override fun onFavoriteChanged(contactData: ContactData, isFavorite: Boolean) {
        //TODO("Not yet implemented")
    }

    override fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }

    override fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }
}
