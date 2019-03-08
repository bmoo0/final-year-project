package com.bitcoinwallet.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitcoinwallet.R
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.text_view_settings.view.*

/**
 * Created by Ben Moore on 07/03/2019.
 */

class SettingsAdapter(private val dataSet: Array<String>) : RecyclerView.Adapter<SettingsAdapter.SettingsAdapterHolder>() {

    class SettingsAdapterHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsAdapterHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_view_settings, parent, false) as ConstraintLayout

        return SettingsAdapterHolder(layout)
    }

    override fun onBindViewHolder(holder: SettingsAdapterHolder, position: Int) {
        holder.layout.txtSettingsAdapter.text = dataSet[position]
        
        holder.layout.setOnClickListener {
            Log.d(Globals.LOG_TAG, "${dataSet[position]} has been selected")
        }
    }

    override fun getItemCount(): Int { return dataSet.size }
}
