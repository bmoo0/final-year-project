package com.bitcoinwallet.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.bitcoinwallet.R

/**
 * Created by Ben Moore on 07/03/2019.
 */

class SettingsAdapter(private val dataSet: Array<String>) : RecyclerView.Adapter<SettingsAdapter.SettingsAdapterHolder>() {

    class SettingsAdapterHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsAdapterHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_view_settings, parent, false) as TextView

        return SettingsAdapterHolder(textView)
    }

    override fun onBindViewHolder(holder: SettingsAdapterHolder, position: Int) {
        holder.textView.text = dataSet[position]
    }

    override fun getItemCount(): Int { return dataSet.size }
}
