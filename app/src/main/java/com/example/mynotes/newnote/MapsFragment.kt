package com.example.mynotes.newnote

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task


class MapsFragment : Fragment() {

    private var locationPermissionGranted = false

    val viewModel: NewNoteViewModel by activityViewModels()
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var latLng:LatLng? = null

    private var map: GoogleMap? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var defaultLocation: LatLng = LatLng(-33.8523341, 151.2106085)
    private var DEFAULT_ZOOM = 15
    private var lastKnownLocation: Location? = null


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap

        map?.mapType = GoogleMap.MAP_TYPE_NORMAL;


        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true


        getDeviceLocation()

        map?.setOnMapClickListener {
            map?.clear()
            val lat = "%.3f".format(it.latitude)
            val long = "%.3f".format(it.longitude)
            val marker = MarkerOptions().position(it).title("$lat,$long")
            latLng = it
            map?.addMarker(marker)

        }

    }

    companion object {
        fun newInstance() = MapsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkGooglePlayServices()
        requestPermission()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.saveMap.setOnClickListener {
            viewModel.setLatLng(latLng)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode =
            googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 257)?.show()
            } else {
                Log.i(
                    javaClass.simpleName,
                    "This device must install Google Play Services."
                )
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPermissionGranted = true;
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Unable to show location - permission required",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult: Task<Location> = fusedLocationProviderClient!!.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {

                        map!!.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map!!.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}