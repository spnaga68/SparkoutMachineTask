package com.imtamila.sparkoutmachinetask.employees.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.imtamila.sparkoutmachinetask.R
import com.imtamila.sparkoutmachinetask.databinding.ActivityMainBinding
import com.imtamila.sparkoutmachinetask.employees.view.fragments.ContactsFragment
import com.imtamila.sparkoutmachinetask.employees.view.fragments.EmployeesFragment
import com.imtamila.sparkoutmachinetask.employees.view.fragments.FavouritesFragment
import com.imtamila.sparkoutmachinetask.map.view.MapActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tabTitlesArray: Array<String> by lazy {
        arrayOf<String>(
            getString(R.string.employee),
            getString(R.string.contacts),
            getString(R.string.favourites)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main

        )
    }

    override fun onStart() {
        super.onStart()
        binding.viewPager.adapter = ScreenSlidePagerAdapter(this, tabTitlesArray)
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = tabTitlesArray[position]
            }).attach()

    }

    override fun onResume() {
        super.onResume()
        binding.fabMoveToMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    private class ScreenSlidePagerAdapter(
        fragmentActivity: FragmentActivity,
        val tabTitlesArray: Array<String>
    ) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = tabTitlesArray.size

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> EmployeesFragment()
            1 -> ContactsFragment()
            else -> FavouritesFragment()
        }
    }
}
