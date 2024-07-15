package com.example.ble_example

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import java.util.UUID



@SuppressLint("MissingPermission") // App's role to ensure permissions are available
class BluetoothManager {
    public var bluetoothGatt : BluetoothGatt? = null

    private fun BluetoothGatt.printGattTable(){
        if (services.isEmpty()) {
            Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?")
            return
        }

        services.forEach { service ->
            val characteristicsTable = service.characteristics.joinToString(
                separator = "\n|--",
                prefix = "|--"
            ) { it.uuid.toString() }
            Log.i("printGattTable", "\nService ${service.uuid}\nCharacteristics:\n$characteristicsTable")
        }

    }

    fun BluetoothGattCharacteristic.isReadable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    public fun isWritable(characteristic: BluetoothGattCharacteristic?): Boolean?{

        return characteristic?.isWritable()
    }


    public fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?, payload: ByteArray){
        if(characteristic == null)
            return

        val writeType = when {
            characteristic.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> error("Characteristic ${characteristic.uuid} cannot be written to")
        }
        bluetoothGatt?.let {
                gatt-> if(Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU){
            gatt.writeCharacteristic(characteristic, payload, writeType)
        } else{
            gatt.legacyCharacteristicWrite(characteristic, payload, writeType)
        }
        } ?: error("Not Connected to a ble device!")
    }


    @TargetApi(Build.VERSION_CODES.S)
    @Suppress("DEPRECATION")
    private fun BluetoothGatt.legacyCharacteristicWrite(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        writeType: Int
    ) {
        characteristic.writeType = writeType
        characteristic.value = value
        writeCharacteristic(characteristic)
    }

    fun printGattTable() {
        bluetoothGatt?.printGattTable()
    }


}