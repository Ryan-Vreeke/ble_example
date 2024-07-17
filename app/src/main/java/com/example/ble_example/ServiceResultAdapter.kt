package com.example.ble_example

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.ble_example.ServiceResultAdapter.ViewHolder
import com.example.ble_example.databinding.ActivityMainBinding

class ServiceResultAdapter(
    private val services: List<BluetoothGattService>,
    private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
) : RecyclerView.Adapter<ServiceResultAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.service_discover_result,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount(): Int {
       return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ) : RecyclerView.ViewHolder(view) {
        private var expand = false

        private lateinit var charView : RecyclerView
        private val charList = mutableListOf<BluetoothGattCharacteristic>()
        val charResultAdapter: CharacteristicAdapter by lazy{
            CharacteristicAdapter(charList, onClickListener)
        }

        fun bind(service: BluetoothGattService) {
            view.findViewById<TextView>(R.id.service_name).text = "service"
            view.findViewById<TextView>(R.id.uuid).text = service.uuid.toString()
            setupRecyclerView(view)
            charView = view.findViewById(R.id.characteristic_recycler);
            charView.visibility = View.GONE

            charList.addAll(service.characteristics)
            charResultAdapter.notifyDataSetChanged()

            view.setOnClickListener {
                expand = !expand
                charView.visibility = if(expand) View.VISIBLE else View.GONE
            }
        }

        @UiThread
        private fun setupRecyclerView(view: View) {

            view.findViewById<RecyclerView>(R.id.characteristic_recycler).apply {
                adapter = charResultAdapter
                layoutManager = LinearLayoutManager(
                    view.context,
                    RecyclerView.VERTICAL,
                    false
                )
                isNestedScrollingEnabled = false
                itemAnimator.let {
                    if (it is SimpleItemAnimator) {
                        it.supportsChangeAnimations = false
                    }
                }
            }
        }
    }

}
