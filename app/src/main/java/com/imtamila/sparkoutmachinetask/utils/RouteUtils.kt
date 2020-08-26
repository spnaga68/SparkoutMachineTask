package com.imtamila.sparkoutmachinetask.utils

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object RouteUtils {
    /**
     * Get a list of latlng from polyline by decode
     *
     * @param jObject
     * @return
     */
    fun parsePolylineFromPoints(jObject: JSONObject): List<LatLng> {
        var path: List<LatLng> = ArrayList()
        try {
            val jRoutes: JSONArray = jObject.getJSONArray("routes")

            /** Traversing all routes  */
            val jOverviewPoly: JSONObject =
                (jRoutes.get(0) as JSONObject).getJSONObject("overview_polyline")
            path = decodePoly(jOverviewPoly.getString("points"))
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        return path
    }

    private fun decodePoly(encodedPath: String): List<LatLng> {
        val len = encodedPath.length
        val path: ArrayList<LatLng> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 31)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 31)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat.toDouble() * 1.0E-5, lng.toDouble() * 1.0E-5))
        }
        return path
    }
}