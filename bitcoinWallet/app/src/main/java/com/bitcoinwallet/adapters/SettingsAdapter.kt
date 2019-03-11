package com.bitcoinwallet.adapters

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitcoinwallet.R
import com.bitcoinwallet.activities.DisplayRecoverySeedActivity
import com.bitcoinwallet.utilities.Globals
import kotlinx.android.synthetic.main.text_view_settings.view.*

/**
 * Created by Ben Moore on 07/03/2019.
 */

class SettingsAdapter(
    private val context: Context,
    private val dataSet: Array<String>
) :
    RecyclerView.Adapter<SettingsAdapter.SettingsAdapterHolder>() {

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

            when (dataSet[position]) {
                "View Recovery Seed" -> {
                    val viewRecoverySeedIntent = Intent(context, DisplayRecoverySeedActivity::class.java)
                    viewRecoverySeedIntent.putExtra("IS_FROM_SETTINGS", true)
                    startActivity(context, viewRecoverySeedIntent, null)
                }
                //"Set Password" ->
            }
        }
    }

    override fun getItemCount(): Int { return dataSet.size }
}
