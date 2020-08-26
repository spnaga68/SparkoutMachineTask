package com.imtamila.sparkoutmachinetask.employees.view.fragments

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.imtamila.sparkoutmachinetask.R
import com.imtamila.sparkoutmachinetask.common.CommonViewModelFactory
import com.imtamila.sparkoutmachinetask.common.obtainViewModel
import com.imtamila.sparkoutmachinetask.databinding.FragmentContactsBinding
import com.imtamila.sparkoutmachinetask.employees.adapter.ContactAdapter
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.employees.interfaces.OnFavoriteChanged
import com.imtamila.sparkoutmachinetask.employees.viewModel.ContactsViewModel
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface
import com.imtamila.sparkoutmachinetask.utils.CommonAlertDialog

/**
 * A simple [Fragment] subclass.
 */

private val CONTACTS_SUMMARY_PROJECTION: Array<String> = arrayOf(
    ContactsContract.CommonDataKinds.Phone._ID,
    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS,
    ContactsContract.CommonDataKinds.Phone.NUMBER,
    ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
)
const val PERMISSIONS_REQUEST_READ_CONTACTS = 100

class ContactsFragment : Fragment(), OnFavoriteChanged, DialogOnClickInterface {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var viewModel: ContactsViewModel
    private lateinit var mObserver: Observer<List<ContactData>>
    private lateinit var searchObserver: Observer<List<ContactData>>

    private val select: String by lazy {
        "((${Contacts.DISPLAY_NAME} NOTNULL) AND (" +
                "${Contacts.HAS_PHONE_NUMBER}=1) AND (" +
                "${Contacts.DISPLAY_NAME} != ''))"
    }
    private val contactListAdapter: ContactAdapter by lazy {
        ContactAdapter(
            requireActivity(),
            this
        )
    }
    private var canObserveData = true
    private var canObserveSearchData = true
    private var mDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        viewModel = (requireActivity() as AppCompatActivity).obtainViewModel(
            ContactsViewModel::class.java,
            CommonViewModelFactory(requireContext(), 1)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = this@ContactsFragment
            executePendingBindings()
        }
        setAdapter()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length > 2) {
                    onContactsSearched(s.toString())
                } else {
                    viewModel.localContactsList?.let {
                        contactListAdapter.submitList(it)
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mDialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
    }

    private fun subscribeObservers() {
        mObserver = Observer {
            if (!it.isNullOrEmpty()) {
                if (canObserveData) {
                    with(it as ArrayList<ContactData>) {
                        viewModel.localContactsList = this
                        contactListAdapter.submitList(this)
                    }
                    canObserveData = false
                }
            } else {
                canObserveData = true
                loadContacts()
            }
        }
        searchObserver = Observer {
            if (!it.isNullOrEmpty()) {
                if (canObserveSearchData) {
                    contactListAdapter.submitList(it as ArrayList<ContactData>)
                    canObserveSearchData = false
                }
            }
        }
        viewModel.loadAllContacts().observe(viewLifecycleOwner, mObserver)
    }

    private fun checkReadContactsPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestContactAccessPermission()
            false
        } else true
    }

    private fun requestContactAccessPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSIONS_REQUEST_READ_CONTACTS
        )
    }

    private fun loadContacts() {
        if (!checkReadContactsPermission())
            return
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            CONTACTS_SUMMARY_PROJECTION,
            select,
            null,
            "${Contacts.DISPLAY_NAME} COLLATE LOCALIZED ASC"
        )
        cursor?.let {
            val contactsList = ArrayList<ContactData>()
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(ContactData(name, phoneNumber, 0))
            }
            it.close()
            viewModel.insertAllContacts(contactsList)
        }
    }

    private fun setAdapter() {
        binding.mRecyclerView.apply {
            setHasFixedSize(true)
            adapter = contactListAdapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else
                mDialog = CommonAlertDialog.alertDialog(
                    requireContext(),
                    this,
                    getString(R.string.contact_permission_needed),
                    negativeButtonText = "",
                    alertType = 1
                )

        }
    }

    override fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int) {
        when (alertType) {
            1 -> {
                dialog.dismiss()
                requestContactAccessPermission()
            }
            else -> dialog.dismiss()
        }
    }

    override fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }

    override fun onFavoriteChanged(contactData: ContactData, isFavorite: Boolean) {
        canObserveSearchData = false
        viewModel.updateFavorite(
            contactData.copy(isFavourite = if (isFavorite) 1 else 0),
            contactData
        )
    }

    fun onContactsSearched(constraint: String) {
        canObserveSearchData = true
        viewModel.searchContacts(constraint).observe(viewLifecycleOwner, searchObserver)
    }
}
