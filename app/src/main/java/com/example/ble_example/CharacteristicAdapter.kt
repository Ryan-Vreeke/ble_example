package com.example.ble_example

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
            view.findViewById<TextView>(R.id.char_uuid).text = characteristic.uuid.toString()
            view.findViewById<TextView>(R.id.char_name).text = "Name"
            view.findViewById<TextView>(R.id.prop).text = getProperties(characteristic.properties)


            view.setOnClickListener{onClickListener.invoke(characteristic)}
        }

        private fun getProperties(properties: Int): String{
            val propertyList = mutableListOf<String>()

            if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                propertyList.add("Read")
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
                propertyList.add("Write")
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
                propertyList.add("Write Without Response")
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                propertyList.add("Notify")
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
                propertyList.add("Indicate")
            }

            return if (propertyList.isNotEmpty()) {
                propertyList.joinToString(", ")
            } else {
                "Unknown Properties"
            }

        }
    }
}
