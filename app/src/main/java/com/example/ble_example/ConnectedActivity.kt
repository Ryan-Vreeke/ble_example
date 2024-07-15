package com.example.ble_example

import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.ble_example.databinding.ConnectedBinding

object BluetoothServiceHolder {
    var bluetoothGattServices: List<BluetoothGattService>? = null
}

class ConnectedActivity : AppCompatActivity() {
    private lateinit var binding: ConnectedBinding
    private val serviceList = mutableListOf<BluetoothGattService>()
    private val serviceResultAdapter: ServiceResultAdapter by lazy{
        ServiceResultAdapter(serviceList){service ->
            Log.i("ServiceList", "clicked ${service.uuid.toString()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ConnectedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val deviceName = intent.getStringExtra("EXTRA_DEVICE_NAME")
        BluetoothServiceHolder.bluetoothGattServices?.let { serviceList.addAll(it) }
        serviceResultAdapter.notifyDataSetChanged()


        findViewById<TextView>(R.id.device_name).text = deviceName
    }

    @UiThread
    private fun setupRecyclerView(){
        binding.servicesResultRecyclerView.apply{
            adapter = serviceResultAdapter
            layoutManager = LinearLayoutManager(
                this@ConnectedActivity,
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