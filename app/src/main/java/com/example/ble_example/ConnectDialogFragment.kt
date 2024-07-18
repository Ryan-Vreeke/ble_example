package com.example.ble_example

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConnectDialogFragment() : BottomSheetDialogFragment() {
    var characteristic: BluetoothGattCharacteristic? = null
    private val _sendMSG = MutableLiveData<Pair<BluetoothGattCharacteristic?, String>>()
    val sendMSG: LiveData<Pair<BluetoothGattCharacteristic?, String>> = _sendMSG

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_connect_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.send_btn).setOnClickListener {
            // Handle button action here
            val sendMessage: String = view.findViewById<EditText>(R.id.send_msg).text.toString()

            _sendMSG.postValue(Pair(characteristic, sendMessage))
            dismiss()
        }
    }
}