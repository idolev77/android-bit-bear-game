package com.example.myapplication.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.data.HighScoreManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * HighScoreMapFragment - displays a Google Map with markers for all high scores
 * Part 2: Can zoom to a specific location when a score is selected in the list
 * Uses custom marker icon and shows score on marker click
 */
class HighScoreMapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var pendingZoom: Triple<Double, Double, String>? = null
    private var customMarkerIcon: BitmapDescriptor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_high_score_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create custom marker icon from drawable
        customMarkerIcon = getMarkerIconFromDrawable(R.drawable.map_marker)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * Convert drawable resource to BitmapDescriptor for map marker
     */
    private fun getMarkerIconFromDrawable(drawableId: Int): BitmapDescriptor? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null

        // Set size for the marker (adjust as needed)
        val width = 80
        val height = 80

        drawable.setBounds(0, 0, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Setup marker click listener to show score info
        googleMap?.setOnMarkerClickListener { marker ->
            // Show the info window with score
            marker.showInfoWindow()
            // Return false to allow default behavior (centering on marker)
            false
        }

        // Load all high scores and add markers
        loadMarkersFromHighScores()

        // If there was a pending zoom request, execute it now
        pendingZoom?.let { (lat, lng, name) ->
            zoomToLocationInternal(lat, lng, name)
            pendingZoom = null
        }
    }

    /**
     * Load all high scores and add markers to the map
     */
    private fun loadMarkersFromHighScores() {
        val highScoreManager = HighScoreManager(requireContext())
        val scores = highScoreManager.getHighScores()

        scores.forEachIndexed { index, score ->
            val position = LatLng(score.latitude, score.longitude)

            val markerOptions = MarkerOptions()
                .position(position)
                .title("${score.playerName}")
                .snippet("🏆 Score: ${score.score} pts | Rank: #${index + 1}")

            // Apply custom icon if available
            customMarkerIcon?.let { icon ->
                markerOptions.icon(icon)
            }

            googleMap?.addMarker(markerOptions)
        }

        // Zoom to first score location if available
        if (scores.isNotEmpty()) {
            val firstScore = scores.first()
            val position = LatLng(firstScore.latitude, firstScore.longitude)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
        } else {
            // Default to Israel if no scores
            val defaultLocation = LatLng(31.7683, 35.2137)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 7f))
        }
    }

    /**
     * Zoom the map to a specific location
     * Called when a score is selected in the list fragment
     */
    fun zoomToLocation(latitude: Double, longitude: Double, playerName: String) {
        if (googleMap != null) {
            zoomToLocationInternal(latitude, longitude, playerName)
        } else {
            // Map not ready yet, save for later
            pendingZoom = Triple(latitude, longitude, playerName)
        }
    }

    private fun zoomToLocationInternal(latitude: Double, longitude: Double, playerName: String) {
        val position = LatLng(latitude, longitude)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
    }
}

