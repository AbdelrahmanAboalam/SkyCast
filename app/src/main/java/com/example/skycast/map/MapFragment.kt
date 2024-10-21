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
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var locationMarker: Marker
    private lateinit var locationGetter: LocationGetter

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
            icon = resources.getDrawable(R.drawable.add_location, null) // Your custom icon
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            isDraggable = false // Prevent dragging if you want to keep it fixed
        }
        mapView.overlays.add(locationMarker)

        // Check for location permission
        if (locationGetter.hasLocationPermission()) {
            fetchLocationAndSetMarker() // Set the initial marker location
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Handle map click events
        mapView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val geoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
                // Update the marker position
                updateMarkerPosition(geoPoint)
                // Zoom in when the user clicks on the map
                mapView.controller.setZoom(15.0) // Set the zoom level (15.0 is an example)
            }
            false
        }

        // Add other overlays (e.g., scale bar)
        addOtherOverlays()
    }

    private fun addOtherOverlays() {
        // Add a scale bar overlay
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        mapView.overlays.add(scaleBarOverlay)
    }

    private fun fetchLocationAndSetMarker() {
        lifecycleScope.launch {
            val location = locationGetter.getLocation()
            location?.let {
                // Set the marker position to the user's location
                val userLocation = GeoPoint(it.latitude, it.longitude)
                updateMarkerPosition(userLocation) // Update the marker
                mapView.controller.setCenter(userLocation) // Center the map on the user's location
                mapView.controller.setZoom(15.0) // Set the zoom level for the user's location
            } ?: run {
                // If location retrieval fails, set a default location
                val defaultLocation = GeoPoint(37.7749, -122.4194) // Example: San Francisco coordinates
                updateMarkerPosition(defaultLocation)
                mapView.controller.setCenter(defaultLocation) // Center on the default location
                mapView.controller.setZoom(15.0) // Set the zoom level for the default location
                Toast.makeText(requireContext(), "Unable to fetch location. Default location set.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMarkerPosition(geoPoint: IGeoPoint) {
        locationMarker.position = geoPoint as GeoPoint?
        locationMarker.title = "Location: ${geoPoint.latitude}, ${geoPoint.longitude}"
        locationMarker.showInfoWindow()

        // Display dimensions as a toast message
        Toast.makeText(requireContext(), "Coordinates: ${geoPoint.latitude}, ${geoPoint.longitude}", Toast.LENGTH_SHORT).show()
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

