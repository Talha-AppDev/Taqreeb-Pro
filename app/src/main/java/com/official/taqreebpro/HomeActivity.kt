package com.official.taqreebpro

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.official.taqreebpro.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var company: String
    private lateinit var binding:ActivityHomeBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        bottomNavigationView = binding.bottomNavigation

        // Set up ViewPager2 with adapter
        viewPager.adapter = ViewPagerAdapter(this)

        // Handle BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.miHome -> viewPager.setCurrentItem(0, false) // Directly navigate to Home
                R.id.chat -> viewPager.setCurrentItem(1, false) // Directly navigate to Profile
                R.id.prof -> viewPager.setCurrentItem(2, false) // Directly navigate to Settings
            }
            true
        }

        // Handle ViewPager2 page changes to sync with BottomNavigationView
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.miHome
                    1 -> bottomNavigationView.selectedItemId = R.id.chat
                    2 -> bottomNavigationView.selectedItemId = R.id.prof
                }
            }
        })
    }

    override fun onStart() {
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // For older Android versions, use the older method
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        super.onStart()
    }
}