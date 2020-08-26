package com.imtamila.sparkoutmachinetask.map.view

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.imtamila.sparkoutmachinetask.R
import com.imtamila.sparkoutmachinetask.common.CommonViewModelFactory
import com.imtamila.sparkoutmachinetask.common.obtainViewModel
import com.imtamila.sparkoutmachinetask.databinding.ActivityMapBinding
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface
import com.imtamila.sparkoutmachinetask.map.data.UserLocation
import com.imtamila.sparkoutmachinetask.map.viewModel.MapViewModel
import com.imtamila.sparkoutmachinetask.utils.CommonAlertDialog
import com.imtamila.sparkoutmachinetask.utils.RouteUtils
import com.imtamila.sparkoutmachinetask.utils.SessionManager
import com.imtamila.sparkoutmachinetask.utils.USER_ID

private const val LOCATION_REQUEST_CODE: Int = 600
private const val REQUEST_CHECK_SETTINGS = 420
private const val LOCATION_FASTEST_INTERVAL = 5000L
private const val LOCATION_INTERVAL = LOCATION_FASTEST_INTERVAL * 2
private const val TAG = "AddressMapFragment"

class MapActivity : AppCompatActivity(), OnMapReadyCallback, DialogOnClickInterface {
    private lateinit var binding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationListener: ValueEventListener
    private val myDatabaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("user_location/locations")
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    location?.let {
                        sendLocationToDb(it)
                    }
                }
            }
        }
    }
    private val coimbatoreLatLng by lazy { LatLng(11.0168, 76.9558) }
    private val chennaiLatLng by lazy { LatLng(13.0827, 80.2707) }

    private var mDialog: Dialog? = null
    private var mMap: GoogleMap? = null
    private var blackPolyLine: Polyline? = null
    private var greyPolyLine: Polyline? = null
    private var userLocationMarker: Marker? = null

    private var polyLineAnimationListener: Animator.AnimatorListener =
        object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                val blackLatLng = blackPolyLine!!.points
                val greyLatLng = greyPolyLine!!.points
                greyLatLng.clear()
                greyLatLng.addAll(blackLatLng)
                blackLatLng.clear()
                blackPolyLine?.run {
                    points = blackLatLng
                    zIndex = 2f
                }
                greyPolyLine?.run {
                    points = greyLatLng
                }
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        viewModel = obtainViewModel(MapViewModel::class.java, CommonViewModelFactory(this))
        with(binding) {
            mapViewModel = viewModel
            lifecycleOwner = this@MapActivity
            executePendingBindings()
        }
        setUpGoogleMap()
    }

    override fun onStart() {
        super.onStart()
        setUserLocationListener()
    }

    override fun onStop() {
        super.onStop()
        mDialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
        if (::locationListener.isInitialized) {
            myDatabaseReference.removeEventListener(locationListener)
        }
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun setUserLocationListener() {
        locationListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userLocation = dataSnapshot.getValue<UserLocation>()
                userLocation?.let {
                    setUserLocationMarker(it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadLocation:onCancelled", databaseError.toException())

            }
        }
        myDatabaseReference.child(SessionManager.getSession(this, USER_ID, "-1"))
            .addValueEventListener(locationListener)
    }

    private fun setUserLocationMarker(userLocation: UserLocation) {
        mMap?.let {
            userLocationMarker?.remove()
            userLocationMarker = it.addMarker(
                MarkerOptions().position(
                    LatLng(userLocation.latitude, userLocation.longitude)
                ).title("Current location").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location)
                )
            )
        }
    }

    private fun sendLocationToDb(location: Location) {
        myDatabaseReference.child(SessionManager.getSession(this, USER_ID, "-1"))
            .setValue(
                UserLocation(
                    System.currentTimeMillis().toString(),
                    location.latitude, location.longitude
                )
            )
    }

    private fun setUpGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(binding.googleMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /*private fun getLastKnowLocation(animateCamera: Boolean = true) {
        if (checkPermissions()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val latlng = LatLng(location.latitude, location.longitude)
                    } else {
                        startLocationUpdates()
                    }
                }
        }
    }*/

    /**
     * Check location permissions if not granted it will request
     */
    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
            false
        } else
            true
    }


    /**
     * Request for location permission at runtime for above 21 api
     * user action callback falls on "onRequestPermissionsResult" function
     */
    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun startLocationUpdates() {
        if (checkPermissions()) {
            if (!::fusedLocationClient.isInitialized)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun makeLocationSettingsClient() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { _ ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this as FragmentActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private fun setMarkers(it: GoogleMap) {
        val latLngBuilder: LatLngBounds.Builder = LatLngBounds.Builder()

        it.addMarker(
            MarkerOptions().position(coimbatoreLatLng).title("Marker in Coimbatore").icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            )
        )
        it.addMarker(
            MarkerOptions().position(chennaiLatLng).title("Marker in Chennai").icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_ROSE
                )
            )
        )

        latLngBuilder.include(coimbatoreLatLng)
        latLngBuilder.include(chennaiLatLng)

        it.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 100))

        viewModel.drawRoute(
            "https://maps.googleapis.com/maps/api/directions/json?origin=${coimbatoreLatLng.latitude},${coimbatoreLatLng.longitude}&destination=${chennaiLatLng.latitude},${chennaiLatLng.longitude}&key=${getString(
                R.string.google_maps_key
            )}"
        ).observe(this,
            Observer { resource ->
                viewModel.showLoading.value = false
                resource?.let {
                    if (it.getString("status").equals("OK", true))
                        drawRoutePolyline(RouteUtils.parsePolylineFromPoints(it))
                }
            })
    }

    /**
     * on location permission granted this function will excecute register location receiver process
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (grantResults.isNotEmpty())
                        mDialog = CommonAlertDialog.alertDialog(
                            this, this, getString(
                                R.string.location_permission
                            ), negativeButtonText = "", isCancelable = false, alertType = 3
                        )
                } else {
                    makeLocationSettingsClient()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS ->
                if (resultCode == Activity.RESULT_OK)
                    startLocationUpdates()
                else
                    mDialog = CommonAlertDialog.alertDialog(
                        this,
                        this,
                        getString(R.string.prompt_location_enable),
                        negativeButtonText = "",
                        isCancelable = false,
                        alertType = 2
                    )
        }
    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        mGoogleMap?.let {
            mMap = it
            createLocationRequest()
            startLocationUpdates()
            setMarkers(it)
        }
    }

    override fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int) {
        when (alertType) {
            2 -> {
                makeLocationSettingsClient()
                dialog.cancel()
            }
            3 -> {
                makeRequest()
                dialog.cancel()
            }
            else -> dialog.cancel()
        }
    }

    override fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }

    private fun drawRoutePolyline(result: List<LatLng>) {
        val lineOptions = PolylineOptions()
        if (mMap != null) {
            lineOptions.apply {
                width(5f)
                color(Color.GRAY)
                startCap(SquareCap())
                endCap(SquareCap())
                jointType(JointType.ROUND)
            }
            blackPolyLine = mMap?.run {
                addPolyline(lineOptions)
            }
            val greyOptions = PolylineOptions().apply {
                width(5f)
                color(Color.BLACK)
                startCap(SquareCap())
                endCap(SquareCap())
                jointType(JointType.ROUND)
            }
            greyPolyLine = mMap?.run {
                addPolyline(greyOptions)
            }
            animatePolyLine(1000, result)
        }
    }

    private fun animatePolyLine(
        durations: Long,
        listLatLng: List<LatLng>
    ) {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.apply {
            duration = durations
            interpolator = LinearInterpolator()
        }
        animator.addUpdateListener { animator ->
            val latLngList = blackPolyLine!!.points
            val initialPointSize = latLngList.size
            val animatedValue = animator.animatedValue as Int
            val newPoints = animatedValue * listLatLng.size / 100

            if (initialPointSize < newPoints) {
                latLngList.addAll(listLatLng.subList(initialPointSize, newPoints))
                blackPolyLine!!.points = latLngList
            }
        }
        animator.addListener(polyLineAnimationListener)
        animator.start()
    }
}