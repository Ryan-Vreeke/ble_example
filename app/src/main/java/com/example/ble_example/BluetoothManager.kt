package com.example.ble_example

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.UUID

private const val GATT_MAX_MTU_SIZE = 517

@SuppressLint("MissingPermission") // App's role to ensure permissions are available
class BluetoothManager (private val context: Context){
    var bluetoothGatt : BluetoothGatt? = null

    private val _connectionState = MutableLiveData<Pair<List<BluetoothGattService>, String>>()
    val connectionState: LiveData<Pair<List<BluetoothGattService>, String>> = _connectionState

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

    fun connect(device: BluetoothDevice){
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect(){
        bluetoothGatt?.disconnect()
    }

    val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to $deviceAddress")

                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Disconnected from $deviceAddress")
                    gatt.close()
                    bluetoothGatt = null
                }
            } else { /* Omitted for brevity */ }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                Log.w("BluetoothGattCallback", "Discovered ${services.size} services for ${device.address}")

                bluetoothGatt = gatt
                gatt.requestMtu(GATT_MAX_MTU_SIZE)

                Handler(Looper.getMainLooper()).post {
                    _connectionState.postValue(Pair(gatt.services, device.name))
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i("BluetoothGattCallback", "Wrote to characteristic $uuid")
                    }
                    BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                        Log.e("BluetoothGattCallback", "Write exceeded connection ATT MTU!")
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Write not permitted for $uuid!")
                    }
                    else -> {
                        Log.e("BluetoothGattCallback", "Characteristic write failed for $uuid, error: $status")
                    }
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.w("BluetoothGattCallback", "ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")
        }
    }

    private fun BluetoothGattCharacteristic.isReadable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    private fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    private fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    private fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    fun isWritable(characteristic: BluetoothGattCharacteristic?): Boolean?{
        return characteristic?.isWritable()
    }

    fun write(characteristic: BluetoothGattCharacteristic){
        if(characteristic.isWritable() == true){
            val message = "HELLO"
            writeCharacteristic(characteristic, message.toByteArray())
            Log.i("GattWrite", "Message sent");
        }
    }


    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?, payload: ByteArray){
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