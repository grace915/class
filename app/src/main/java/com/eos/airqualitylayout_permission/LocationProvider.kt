package com.eos.airqualitylayout_permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.content.ContextCompat

class LocationProvider(private val context: Context) : LocationListener {
    private var location: Location? = null
    private var locationManager: LocationManager? = null

    init {
        location = getLocation()
    }

    private fun getLocation() : Location? {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation: Location? = null
            var networkLocation: Location? = null

            //provider 두개가 활성화되어 있는지 확인
            val isGPSEnabled: Boolean = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled : Boolean = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGPSEnabled && !isNetworkEnabled){
                // gps, network provider 모두 사용 불가능
                return null
            } else {
                // 정밀한 위치정보
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

                if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                    // 둘다 권한이 없을 떄
                    return null
                }
                if(isNetworkEnabled){
                    //네트워크를 통한 위치 파악 가능할떄
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                if(isGPSEnabled){
                    // gps 통한 위치 파악 가능할 때
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                // 정확도 비교
                return if(gpsLocation != null && networkLocation != null){
                    if(gpsLocation.accuracy > networkLocation.accuracy){
                        location = gpsLocation
                        gpsLocation
                    }else {
                        location = networkLocation
                        networkLocation
                    }
                } else {
                    // 가능한 위치 정보가 한개만 있을 때
                    gpsLocation ?: networkLocation

                    // gpsLocation ? return gpsLocation : return networkLocation
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return location
    }
    // 위치 정보를 가져오는 함수
    fun getLocationLatitude() : Double {
        return location?.latitude ?: 0.0
    }
    fun getLocationLongitude() : Double {
        return location?.longitude ?: 0.0
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}