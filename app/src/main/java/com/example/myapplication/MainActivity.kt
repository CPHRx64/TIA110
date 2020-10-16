package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //bluetooth adapter
    lateinit var bAdapter:BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check bluetooth status
        if (bAdapter == null) {
            bluetoothStatusTv.text = "Bluetooth is not available"
        } else {
            bluetoothStatusTv.text = "Bluetooth is available"
        }

        // set icon accordingly
        if (bAdapter.isEnabled) {
            // bluetooth is On
            bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_on)
        }else{
            // bluetooth is off
            bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        //val connectButton: Button = findViewById(R.id.connectButton)
        //connectButton.setOnClickListener { connect() }
    }

    private fun connect() {
        val pName = "String" // parse name from bluetooth
        val pStatus : Int = 0 // parse status from bluetooth
        val fence : Fence = Fence(pName, pStatus)
    }

}

