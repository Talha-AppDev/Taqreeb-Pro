package com.official.taqreebpro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeFragment : Fragment(R.layout.fragment_home)
class ProfileFragment : Fragment(R.layout.fragment_dashboard)
class ChatsFragment : Fragment(R.layout.fragment_inbox)

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{
    private val fragments = listOf(HomeFragment(), ChatsFragment(), ProfileFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
