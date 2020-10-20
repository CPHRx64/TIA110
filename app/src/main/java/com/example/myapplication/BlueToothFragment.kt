package com.example.myapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myapplication.databinding.FragmentBluetoothBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class BlueToothFragment : Fragment() {

    private lateinit var binding: FragmentBluetoothBinding

    //bluetooth adapter
    lateinit var bAdapter: BluetoothAdapter

    private val REQUEST_CODE_ENABLE_BT: Int = 1;
    private val REQUEST_CODE_DISCOVERABLE_BT: Int = 2;
    private var sType: String = ""
    private var lat0: Double = 0.0
    private var long0: Double = 0.0
    private var lat1: Double = 0.0
    private var long1: Double = 0.0
    private var flag: Int = 0;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentBluetoothBinding.inflate(inflater)

        binding.navigateButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_blueToothFragment_to_dashboardFragment)
        }


        setHasOptionsMenu(true)
        Log.i("TitleFragment", "onCreateView called")

        // non focusable edit text
        binding.geoSource.isFocusable = false
        binding.geoSource.setOnClickListener {
            // define type
            sType = "source"
            // init place field list
            val placeFields: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // create intent
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(requireActivity().applicationContext)

            // start activity result
            startActivityForResult(intent, 100)
        }

        // set destination
        binding.geoDestination.isFocusable = false
        binding.geoDestination.setOnClickListener {
            // define type
            sType = "destination"

            // init places
            val placeFields: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // create intent
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(requireActivity().applicationContext)

            // start activity result
            startActivityForResult(intent, 100)

        }

        // set distance view
        binding.geoDistance.text = "0.0 Km"


        //-------------------------------------------------------------------------------//

        // init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check bluetooth status
        if (bAdapter == null) {
            binding.bluetoothStatusTv.text = "Bluetooth is not available"
        } else {
        }

        // set icon accordingly
        if (bAdapter.isEnabled) {
            // bluetooth is On
            binding.bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_on)
        } else {
            // bluetooth is off
            binding.bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        // turn on/off bluetooth
        binding.turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled) {
                //already enabled
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Already on",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // turn on bluetooth
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        binding.turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled) {
                //already enabled
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Already off",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // turn on bluetooth
                bAdapter.disable()
                binding.bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Bluetooth turned off",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // discoverable
        binding.discoverableBtn.setOnClickListener {
            if (bAdapter.isDiscovering) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Making your device discoverable",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }

        // get list of paired devices
        binding.pairedBtn.setOnClickListener {
            if (bAdapter.isEnabled) {
                binding.pairedTv.text = "Paired devices"
                // get list of paired devices
                val devices = bAdapter.bondedDevices
                for (device in devices) {
                    val deviceName = device.name
                    binding.pairedTv.append("\nDevice: $deviceName, $device")
                }
            } else {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "turn on bluetooth first",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {

                    binding.bluetoothStatusIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "Bluetooth is on",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // user denied to turn on bluetooth from popup
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "Couldn't turn on bluetooth",
                        Toast.LENGTH_LONG
                    ).show()
                }

            100 ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // when success
                    // init places
                    val place: Place = Autocomplete.getPlaceFromIntent(data)
                    // check condition
                    if (sType == "source") {
                        // when type is source
                        // increase flag
                        flag++
                        //set address on edit text
                        binding.geoSource.text = place.address
                        // get lat and long
                        var sSource: String = place.latLng.toString()
                        sSource = sSource.replace("lat/lng: ", "")
                        sSource = sSource.replace("(", "")
                        sSource = sSource.replace(")", "")
                        val split: List<String> = sSource.split(",")
                        lat0 = split[0].toDouble()
                        long0 = split[1].toDouble()
                    } else {
                        // when type is destination
                        // increase flag value
                        flag++
                        // set address on edit text
                        binding.geoDestination.text = place.address
                        // get lat and long
                        var sDestination: String = place.latLng.toString()
                        sDestination = sDestination.replace("lat/lng: ", "")
                        sDestination = sDestination.replace("(", "")
                        sDestination = sDestination.replace(")", "")
                        val split: List<String> = sDestination.split(",")
                        lat1 = split[0].toDouble()
                        long1 = split[1].toDouble()
                    }

                    // check condition
                    if (flag >= 2) {
                        distance(lat0, long0, lat1, long1);
                    }
                } else if (requestCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                    // Error
                    // init status
                    val status: Status = Autocomplete.getStatusFromIntent(data)

                    //Display Toast
                    Toast.makeText(
                        requireActivity().applicationContext,
                        status.statusMessage,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
        }
    }

    private fun distance(lat0: Double, long0: Double, lat1: Double, long1: Double) {
        val longDiff: Double = long0 - long1
        var dist: Double =
            sin(deg2rad(lat0)) * sin(deg2rad(lat1)) + cos(deg2rad(lat0)) * cos(
                deg2rad(lat1)
            ) * cos(deg2rad(longDiff))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 1.609344
        binding.geoSource.text = "$dist Km"
    }

    private fun rad2deg(dist: Double): Double {
        return dist * 180.0 / Math.PI
    }

    private fun deg2rad(lat0: Double): Double {
        return lat0 * Math.PI / 180.0
    }
}



