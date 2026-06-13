package com.travelsync.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.travelsync.core.domain.model.Place

fun openPlaceInMaps(context: Context, place: Place) {
    val query = if (place.latitude != 0.0 && place.longitude != 0.0) {
        "${place.latitude},${place.longitude}(${Uri.encode(place.name)})"
    } else {
        Uri.encode("${place.name} ${place.address}")
    }
    val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
    geoIntent.setPackage("com.google.android.apps.maps")
    if (geoIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(geoIntent)
    } else {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.openstreetmap.org/search?query=${Uri.encode(place.name + " " + place.address)}")
        )
        context.startActivity(webIntent)
    }
}

fun openExternalLink(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun dialPhone(context: Context, phone: String) {
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
}
