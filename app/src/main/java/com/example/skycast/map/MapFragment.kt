package com.example.skycast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.skycast.location.LocationBottomSheetFragment
//import com.example.skycast.map.LocationBottomSheetFragment
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var locationMarker: Marker
    private lateinit var locationGetter: LocationGetter
    private var lastClickedLocation: GeoPoint? = null
    private var clickedLocationMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize LocationGetter
        locationGetter = LocationGetter(requireContext())

        // Initialize the map view
        mapView = view.findViewById(R.id.map_view)
        Configuration.getInstance().userAgentValue = requireActivity().packageName
        mapView.setMultiTouchControls(true)

        // Initialize the location marker
        locationMarker = Marker(mapView).apply {
            icon = resources.getDrawable(R.drawable.add_location, null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(locationMarker)

        // Check for location permission
        if (locationGetter.hasLocationPermission()) {
            fetchLocationAndSetMarker()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Set up map click listener
        setupMapClickListener()

        // Add other overlays (e.g., scale bar)
        addOtherOverlays()
    }

    private fun addOtherOverlays() {
        // Add a scale bar overlay
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        mapView.overlays.add(scaleBarOverlay)
    }

    private fun setupMapClickListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                mapView.overlays.remove(locationMarker)
                if (lastClickedLocation == null || p != lastClickedLocation) {
                    lastClickedLocation = p
                    addClickedLocationMarker(p)
                }
                val dialog = LocationBottomSheetFragment.newInstance(p.latitude, p.longitude)
                dialog.show(parentFragmentManager, "weatherDialog")

                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }

    private fun addClickedLocationMarker(point: GeoPoint) {
        clickedLocationMarker?.let {
            mapView.overlays.remove(it)
        }

        clickedLocationMarker = Marker(mapView).apply {
            position = point
            icon = resources.getDrawable(R.drawable.add_location, null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        mapView.overlays.add(clickedLocationMarker)
        mapView.invalidate()
    }


    private fun showLocationBottomSheet(point: GeoPoint) {
        val locationDetails = "Lat: ${point.latitude}, Lon: ${point.longitude}"
        // Show the bottom sheet or display location details
        Toast.makeText(requireContext(), locationDetails, Toast.LENGTH_SHORT).show()
    }

    private fun fetchLocationAndSetMarker() {
        lifecycleScope.launch {
            val location = locationGetter.getLocation()
            location?.let {
                val userLocation = GeoPoint(it.latitude, it.longitude)
                updateMarkerPosition(userLocation)
                mapView.controller.setCenter(userLocation)
                mapView.controller.setZoom(9.0)

            } ?: run {
                val defaultLocation = locationGetter.getLocation() as GeoPoint
                updateMarkerPosition(defaultLocation)
                mapView.controller.setCenter(defaultLocation)
                mapView.controller.setZoom(10.0)
                Toast.makeText(requireContext(), "Unable to fetch location. Default location set.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMarkerPosition(geoPoint: IGeoPoint) {
        locationMarker.position = geoPoint as GeoPoint?
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndSetMarker()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
