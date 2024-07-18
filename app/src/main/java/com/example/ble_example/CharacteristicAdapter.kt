package com.example.ble_example

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class CharacteristicAdapter(
    private val characteristics: List<BluetoothGattCharacteristic>,
    private val fragmentManager: FragmentManager,
    private val connectDialogFragment: ConnectDialogFragment,
    private val onClickListener:((characteristic: BluetoothGattCharacteristic) -> Unit)

) : RecyclerView.Adapter<CharacteristicAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacteristicAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.characteristic_result,
            parent,
            false
        )
        return CharacteristicAdapter.ViewHolder(view,fragmentManager, connectDialogFragment, onClickListener)
    }

    override fun getItemCount() = characteristics.size

    override fun onBindViewHolder(holder: CharacteristicAdapter.ViewHolder, position: Int) {
        val characteristic = characteristics[position]
        holder.bind(characteristic)
    }

    class ViewHolder(
        private val view: View,
        private val fragmentManager: FragmentManager,
        private val connectDialogFragment: ConnectDialogFragment,
        private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ): RecyclerView.ViewHolder(view){

        fun bind(characteristic: BluetoothGattCharacteristic){

            view.findViewById<TextView>(R.id.char_uuid).text = characteristic.uuid.toString()
            view.findViewById<TextView>(R.id.char_name).text = "Name"
            view.findViewById<TextView>(R.id.prop).text = getProperties(characteristic.properties)
            view.findViewById<Button>(R.id.writebtn).setOnClickListener{
                connectDialogFragment.show(fragmentManager, connectDialogFragment.tag)
            }


            view.setOnClickListener{onClickListener.invoke(characteristic)}
        }

        private fun getProperties(properties: Int): String{
            val propertyList = mutableListOf<String>()

            if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                propertyList.add("Read")
                view.findViewById<Button>(R.id.readbtn).visibility = View.VISIBLE
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
                propertyList.add("Write")
                view.findViewById<Button>(R.id.writebtn).visibility = View.VISIBLE
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
                propertyList.add("Write Without Response")
                view.findViewById<Button>(R.id.writebtn).visibility = View.VISIBLE
            }
            if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                propertyList.add("Notify")
                view.findViewById<Button>(R.id.subbtn).visibility = View.VISIBLE
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
