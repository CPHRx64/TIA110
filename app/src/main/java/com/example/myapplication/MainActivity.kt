package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.libraries.places.api.Places


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val api_key: String = "nope"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //init google places:
        Places.initialize(applicationContext, api_key)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragmentManager = supportFragmentManager
        fragmentManager.findFragmentById(R.id.blueToothFragment)
            ?.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
    }

}


