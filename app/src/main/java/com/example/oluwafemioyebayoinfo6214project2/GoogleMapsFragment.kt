package com.example.oluwafemioyebayoinfo6214project2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException
import java.util.Arrays
import java.util.Locale

class GoogleMapsFragment : Fragment(), OnMapReadyCallback {
//    Fanshawe  43.012440, -81.200180
    private lateinit var mGoogleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val PERMISSION_ID = 100
    var mapFragment : SupportMapFragment? = null

    private var mPlacesClient: PlacesClient? = null
    private val M_MAX_ENTRIES = 10
    private lateinit var mLikelyPlaceNames: Array<String>
    private lateinit var mLikelyPlaceAddresses: ArrayList<String>
    private lateinit var mLikelyPlaceLatLngs: ArrayList<LatLng>
    private var  listOfPlaces: MutableList<PlacesObject> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_google_maps, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = getString(R.string.google_map_api_key)
        Places.initialize(requireContext(), apiKey)
        mPlacesClient = Places.createClient(requireContext())
        mLikelyPlaceNames = arrayOf<String>("","","","","","","","","","")
        mLikelyPlaceAddresses = ArrayList<String>()
        mLikelyPlaceLatLngs = ArrayList<LatLng>()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                requestLocation()
                getCurrentPlaceLikelihoods()
            }
        }
    }
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mGoogleMap.isMyLocationEnabled = true
            requestLocation()
            getCurrentPlaceLikelihoods()
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        }
    }
    private fun checkPermission(): Boolean{
        if(
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    fun requestLocation(){
    if(checkPermission()){
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            var location: Location? = task.result

            if (location == null) {
                newLocation()
            } else {
                val locationLatLng = LatLng(location.latitude,location.longitude)

                var address = getAddress(locationLatLng)

                (requireActivity() as MainActivity).longitude = location?.longitude.toString()
                (requireActivity() as MainActivity).latitude = location?.latitude.toString()
                (requireActivity() as MainActivity).address = address!!

                val currentLatLng = LatLng(location!!.latitude, location!!.longitude)

                mGoogleMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("${location?.latitude.toString()}, ${location?.longitude.toString()}")
                        .snippet(address)
                )
                mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                )
            }
        }
    }
    }
    @SuppressLint("MissingPermission")
    fun newLocation(){

            var locationRequest =  LocationRequest()

            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5000
            locationRequest.fastestInterval = 3000
//            locationRequest.numUpdates = 1

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()
            )

    }

    override fun onResume() {
        super.onResume()
        requestLocation()
        getCurrentPlaceLikelihoods()
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {

                val currentLatLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                var address = getAddress(currentLatLng)


                mGoogleMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("${lastLocation.latitude.toString()}, ${lastLocation.longitude.toString()}")
                        .snippet(address)
                )
                mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                )

            }else{
                Log.i("Debug:","Its null")

            }
        }
    }

    private fun getAddress(loc:LatLng): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(loc!!.latitude, loc!!.longitude, 1)
        } catch (e1: IOException) {
            Log.e("Geocoding", "Problem with the geolocation service", e1)
        } catch (e2: IllegalArgumentException) {
            Log.e("Geocoding", "Invalid LatLng"+
                    "Latitude = " + loc!!.latitude +
                    ", Longitude = " +
                    loc!!.longitude, e2)
        }

        if (addresses != null) {
            // Get the first address
            val address = addresses[0]
            val addressText = String.format(
                "%s, %s, %s",
                address.getAddressLine(0),
                address.locality,
                address.countryName)
            return addressText
        }
        else
        {
            Log.e("Geocoding", "No address found")
            return ""
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentPlaceLikelihoods() {

        val placeFields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse: Task<FindCurrentPlaceResponse> =
            mPlacesClient!!.findCurrentPlace(request)

        placeResponse.addOnCompleteListener(requireActivity(),
            OnCompleteListener<FindCurrentPlaceResponse?> { task ->
                if (task.isSuccessful) {
                    val response = task.result

                    val count: Int

                    if (response.placeLikelihoods.size < M_MAX_ENTRIES) {
                        count = response.placeLikelihoods.size
                    } else {
                        count = M_MAX_ENTRIES
                    }
                    println("Found a place")
                    var i = 0
                    for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                        val currPlace = placeLikelihood.place
                        mLikelyPlaceNames[i] = (currPlace.name)
                        Log.i("Places",currPlace.name)
                        mLikelyPlaceAddresses.add(currPlace.address)
                        mLikelyPlaceLatLngs.add(currPlace.latLng)
                        listOfPlaces.add(PlacesObject(currPlace.name))

                        val currLatLng =
                            if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()

                        Log.i(
                            "Places", String.format(
                                "Place " + currPlace.name
                                        + " has likelihood: " + placeLikelihood.likelihood
                                        + " at " + currLatLng
                            )
                        )
                        i++
                        if (i > (count - 1)) {
                            break
                        }
                    }

                    (requireActivity() as MainActivity).theList = listOfPlaces

                } else {
                    val exception: Exception? = task.getException()
                    if (exception is ApiException) {
                        Log.e("Places", "Place not found: " + exception.statusCode)
                    }
                }
            })
    }
    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0;

        enableMyLocation()
        requestLocation()
        getCurrentPlaceLikelihoods()
    }

}
