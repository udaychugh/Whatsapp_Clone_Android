package com.udaychugh.utalk.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.udaychugh.utalk.fragments.ChatsFragment
import com.udaychugh.utalk.fragments.UsersFragment


class ViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ChatsFragment()
        else -> UsersFragment()
    }
}