package com.example.myapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.datatransport.runtime.backends.BackendResponse
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.net.ssl.SSLEngineResult


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 2;
    private var sType : String = ""
    private var lat0 : Double = 0.0
    private var long0 : Double = 0.0
    private var lat1 : Double = 0.0
    private var long1 : Double = 0.0
    private var flag : Int = 0;


    //bluetooth adapter
    lateinit var bAdapter:BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init google places:
        Places.initialize(applicationContext, "AIzaSyD7su_-5oHGyppir4oCcwG3jUFPMjVshMw")

        // non focusable edit text
        geo_source.isFocusable = false
        geo_source.setOnClickListener{
            // define type
            sType = "source"
            // init place field list
            val placeFields: List<Place.Field> = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // create intent
            val intent : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields).build(this)

            // start activity result
            startActivityForResult(intent, 100)
        }

        // set destination
        geo_destination.isFocusable = false
        geo_destination.setOnClickListener {
            // define type
            sType = "destination"

            // init places
            val placeFields: List<Place.Field> = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // create intent
            val intent : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields).build(this)

            // start activity result
            startActivityForResult(intent, 100)

        }

        // set distance view
        geo_distance.setText("0.0 Km")


        //-------------------------------------------------------------------------------//

        // init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check bluetooth status
        if (bAdapter == null) {
            bluetoothStatusTv.text = "Bluetooth is not available"
        } else {
        }

        // set icon accordingly
        if (bAdapter.isEnabled) {
            // bluetooth is On
            bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_on)
        }else{
            // bluetooth is off
            bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        // turn on/off bluetooth
        turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled){
                //already enabled
                Toast.makeText(this, "Already on", Toast.LENGTH_LONG).show()
            }else{
                // turn on bluetooth
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled){
                //already enabled
                Toast.makeText(this, "Already off", Toast.LENGTH_LONG).show()
            }else{
                // turn on bluetooth
                bAdapter.disable()
                bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_LONG).show()
            }
        }

        // discoverable
        discoverableBtn.setOnClickListener {
            if(bAdapter.isDiscovering) {
                Toast.makeText(this, "Making your device discoverable", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }

        // get list of paired devices
        pairedBtn.setOnClickListener{
            if (bAdapter.isEnabled) {
                pairedTv.text="Paired devices"
                // get list of paired devices
                val devices = bAdapter.bondedDevices
                for (device in devices){
                    val deviceName = device.name
                    pairedTv.append("\nDevice: $deviceName, $device")
                }
            }else{
                Toast.makeText(this, "turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if(resultCode == Activity.RESULT_OK) {
                    bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this, "Bluetooth is on", Toast.LENGTH_LONG).show()
                }else{
                    // user denied to turn on bluetooth from popup
                    Toast.makeText(this, "Couldn't turn on bluetooth", Toast.LENGTH_LONG).show()
                }

            100 ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // when success
                    // init places
                    var place : Place = Autocomplete.getPlaceFromIntent(data)
                    // check condition
                    if (sType.equals("source")){
                        // when type is source
                        // increase flag
                        flag++
                        //set address on edit text
                        geo_source.setText(place.address)
                        // get lat and long
                        var sSource : String = place.latLng.toString()
                        sSource = sSource.replace("lat/lng: ","")
                        sSource = sSource.replace("(","")
                        sSource = sSource.replace(")","")
                        val split : List<String> = sSource.split(",")
                        lat0 = split[0].toDouble()
                        long0 = split[1].toDouble()
                    }else{
                        // when type is destination
                        // increase flag value
                        flag++
                        // set address on edit text
                        geo_destination.setText(place.address)
                        // get lat and long
                        var sDestination : String = place.latLng.toString()
                        sDestination = sDestination.replace("lat/lng: ","")
                        sDestination = sDestination.replace("(","")
                        sDestination = sDestination.replace(")","")
                        val split : List<String> = sDestination.split(",")
                        lat1 = split[0].toDouble()
                        long1 = split[1].toDouble()
                    }

                    // check condition
                    if (flag >= 2) {
                        distance(lat0,long0,lat1,long1);
                    }
                } else if (requestCode == AutocompleteActivity.RESULT_ERROR && data != null){
                    // Error
                    // init status
                    var status : Status = Autocomplete.getStatusFromIntent(data)

                    //Display Toast
                    Toast.makeText(applicationContext,status.statusMessage, Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun distance (lat0 : Double, long0 : Double, lat1 : Double, long1 : Double) {
        val longDiff : Double = long0 - long1
        var dist : Double = Math.sin(deg2rad(lat0)) * Math.sin(deg2rad(lat1)) + Math.cos(deg2rad(lat0)) * Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(longDiff))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 1.609344
        geo_distance.setText(dist.toString() + " Km")
    }

    private fun rad2deg (dist : Double) : Double {
        return dist * 180.0 / Math.PI
    }

    private fun deg2rad (lat0 : Double) : Double {
        return lat0*Math.PI/180.0
    }

}

