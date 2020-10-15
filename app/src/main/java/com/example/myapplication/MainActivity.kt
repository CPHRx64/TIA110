package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton: Button = findViewById(R.id.connectButton)
        connectButton.setOnClickListener { connect() }
    }

    private fun connect() {
        val pName = "String" // parse name from bluetooth
        val pStatus : Int = 0 // parse status from bluetooth
        val fence : Fence = Fence(pName, pStatus)

    }

}

