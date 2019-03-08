package com.bitcoinwallet.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Window
import com.bitcoinwallet.R
import com.bitcoinwallet.fragments.HomeFragment
import com.bitcoinwallet.fragments.SendFragment
import com.bitcoinwallet.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.homeItem -> {
                Log.d("BTC WALLET", "home pressed")
                val homeFragment = HomeFragment.newInstance()
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.sendItem -> {
                Log.d("BTC WALLET", "send pressed")
                val sendFragment = SendFragment.newInstance()
                openFragment(sendFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.setttingsItem -> {
                Log.d("BTC WALLET", "settings pressed")
                val settingsFragment = SettingsFragment.newInstance()
                openFragment(settingsFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_home)

        openFragment(HomeFragment.newInstance())
        homeNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.homeScreenContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
