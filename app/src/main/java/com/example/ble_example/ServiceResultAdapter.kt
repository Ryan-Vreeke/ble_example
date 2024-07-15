package com.example.ble_example

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ble_example.ServiceResultAdapter.ViewHolder


class ServiceResultAdapter(
    private val services: List<BluetoothGattService>,
    private val onClickListener:((service: BluetoothGattService) -> Unit)
) : RecyclerView.Adapter<ServiceResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.service_discover_result,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = services.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((service: BluetoothGattService) -> Unit)
    ): RecyclerView.ViewHolder(view){

        fun bind(service: BluetoothGattService){
            view.findViewById<TextView>(R.id.service_name).text = "service"
            view.findViewById<TextView>(R.id.uuid).text = service.uuid.toString()
            view.findViewById<TextView>(R.id.prop).text = "PROP"

            view.setOnClickListener{onClickListener.invoke(service)}
        }

    }

}

class CharacteristicAdapter(
    private val characteristics: List<BluetoothGattCharacteristic>,
    private val onClickListener:((characteristic: BluetoothGattCharacteristic) -> Unit)

) : RecyclerView.Adapter<CharacteristicAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacteristicAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.characteristic_result,
            parent,
            false
        )
        return CharacteristicAdapter.ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = characteristics.size

    override fun onBindViewHolder(holder: CharacteristicAdapter.ViewHolder, position: Int) {
        val characteristic = characteristics[position]
        holder.bind(characteristic)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ): RecyclerView.ViewHolder(view){
        fun bind(characteristic: BluetoothGattCharacteristic){
            view.setOnClickListener{onClickListener.invoke(characteristic)}
        }
    }
}