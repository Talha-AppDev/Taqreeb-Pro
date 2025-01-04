package com.official.taqreebpro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.official.taqreebpro.fragments.ChatsFragment
import com.official.taqreebpro.fragments.DashboardFragment
import com.official.taqreebpro.fragments.HomeFragment


class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{
    private val fragments = listOf(
        HomeFragment(), ChatsFragment(),
        DashboardFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
