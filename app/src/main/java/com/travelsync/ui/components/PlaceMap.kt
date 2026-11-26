package com.travelsync.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.travelsync.core.domain.model.Place
import java.util.concurrent.atomic.AtomicBoolean
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

// OpenFreeMap may change or stop without notice, so the style URL lives in one place.
private const val STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
private const val SOURCE_ID = "places-source"
private const val LAYER_ID = "places-layer"
private const val PIN_IMAGE_ID = "place-pin"
private const val NAME_PROPERTY = "name"
private const val SINGLE_PLACE_ZOOM = 14.0
private const val BOUNDS_PADDING_PX = 120
private const val PIN_SIZE_DP = 28
private const val PIN_COLOR = 0xFFE53935.toInt()
private const val PIN_BORDER_COLOR = 0xFFFFFFFF.toInt()

/** Map of [places] on OpenFreeMap tiles; places without coordinates are skipped. */
@Composable
fun PlaceMap(places: List<Place>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).also { it.onCreate(null) }
    }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var style by remember { mutableStateOf<Style?>(null) }
    var selectedName by remember { mutableStateOf<String?>(null) }
    // MapLibre callbacks can fire after the map left composition and was destroyed.
    val destroyed = remember { AtomicBoolean(false) }
    val located = remember(places) { places.filter { it.hasCoordinates } }

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            // The observer may not have seen the stop events before the map leaves composition.
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onPause()
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStop()
            destroyed.set(true)
            mapView.onDestroy()
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { mapLibreMap ->
            if (destroyed.get()) return@getMapAsync
            mapLibreMap.setStyle(STYLE_URL) { loaded ->
                if (destroyed.get()) return@setStyle
                loaded.addImage(PIN_IMAGE_ID, createPinBitmap(context.resources.displayMetrics.density))
                loaded.addSource(GeoJsonSource(SOURCE_ID))
                loaded.addLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                        iconImage(PIN_IMAGE_ID),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true)
                    )
                )
                map = mapLibreMap
                style = loaded
            }
            mapLibreMap.addOnMapClickListener { latLng ->
                val point = mapLibreMap.projection.toScreenLocation(latLng)
                selectedName = mapLibreMap.queryRenderedFeatures(point, LAYER_ID)
                    .firstOrNull()
                    ?.getStringProperty(NAME_PROPERTY)
                true
            }
        }
    }

    LaunchedEffect(style, located) {
        val loaded = style ?: return@LaunchedEffect
        val features = located.map { place ->
            Feature.fromGeometry(Point.fromLngLat(place.longitude, place.latitude)).also {
                it.addStringProperty(NAME_PROPERTY, place.name)
            }
        }
        loaded.getSourceAs<GeoJsonSource>(SOURCE_ID)?.setGeoJson(FeatureCollection.fromFeatures(features))
        selectedName = null
        map?.let { fitCamera(it, located) }
    }

    Box(modifier = modifier) {
        AndroidView(factory = { mapView }, modifier = Modifier.matchParentSize())
        if (located.isEmpty()) {
            Text(
                text = "No places with coordinates to show on the map.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
        selectedName?.let { name ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private fun fitCamera(map: MapLibreMap, located: List<Place>) {
    // LatLngBounds needs at least two distinct points.
    val points = located.map { LatLng(it.latitude, it.longitude) }.distinct()
    when (points.size) {
        0 -> Unit
        1 -> map.moveCamera(CameraUpdateFactory.newLatLngZoom(points.first(), SINGLE_PLACE_ZOOM))
        else -> {
            val bounds = LatLngBounds.Builder().includes(points).build()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING_PX))
        }
    }
}

private fun createPinBitmap(density: Float): Bitmap {
    val size = (PIN_SIZE_DP * density).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = size / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = PIN_BORDER_COLOR
    canvas.drawCircle(center, center, center, paint)
    paint.color = PIN_COLOR
    canvas.drawCircle(center, center, center * 0.7f, paint)
    return bitmap
}
