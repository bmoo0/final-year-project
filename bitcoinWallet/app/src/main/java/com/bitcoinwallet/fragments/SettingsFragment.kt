package com.bitcoinwallet.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bitcoinwallet.R
import com.bitcoinwallet.activities.DisplayRecoverySeedActivity
import com.bitcoinwallet.adapters.SettingsAdapter
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass. Showing the settings screen.
 */

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dataset = arrayOf("View Recovery Seed", "Set Password")

        recyclerSettings.layoutManager = LinearLayoutManager(context)
        recyclerSettings.adapter = SettingsAdapter(context!!, dataset)
    }

    companion object {
        fun newInstance() : SettingsFragment = SettingsFragment()
    }
}
