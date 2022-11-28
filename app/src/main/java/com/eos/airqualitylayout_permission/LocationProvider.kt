package com.eos.airqualitylayout_permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat

class LocationProvider(private val context: Context) : LocationListener {
    // Location 은 위도, 경도, 고도와 같이 위치에 관련된 정보를 가지고 있는 클래스
    // LocationManager 는 시스템 위치 서비스에 접근을 제공하는 클래스
    private var location: Location? = null
    private var locationManager: LocationManager? = null

    // private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10L
    // private val MIN_TIME_BW_UPDATES: Float = 1000*60F

    init {
        location = getLocation()

        Log.d("gaeun", "${getLocation()}")
    }

    private fun getLocation() : Location? {
        try {
            // 먼저 위치 시스템 서비스를 가져오기
            locationManager = context.getSystemService(
                Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation: Location? = null
            var networkLocation: Location? = null
            
            // GPS provider와 network provider가 활성화되어 있는지 확인
            val isGPSEnabled: Boolean = locationManager!!.isProviderEnabled(
                LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean = locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS, network provider 둘다 사용 불가능한 상황이면 null 반환
                return null
            } else {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                // 정말한 위치 정보
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                // 도시 block 단위

                // 위 두 개의 권한이 모두 없다면 null 을 반환
                if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED
                    || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    return null
                }

                // 네트워크를 통한 위치 파악이 가능한 경우에 위치를 가져오기
                if (isNetworkEnabled) {
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                // GPS 를 통한 위치 파악이 가능한 경우에 위치를 가져오기
                if (isGPSEnabled) {
                    /*
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        MIN_TIME_BW_UPDATES,
                        this
                    )
                     */
                    // 위치 가져오기
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                // 정확도 비교하기
                Log.e("gaeun", "$gpsLocation / $networkLocation")
                return if (gpsLocation != null && networkLocation != null) {
                    // 위치 두개이면 정확도 높은거 설정
                    if (gpsLocation.accuracy > networkLocation.accuracy) {
                        location = gpsLocation
                        gpsLocation
                    } else {
                       // location = networkLocation
                        // networkLocation
                        location = gpsLocation
                        gpsLocation
                    }
                } else {
                    // 가능한 위치 정보가 한개만 있을 때
                  //  gpsLocation ?: networkLocation
                    gpsLocation
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }
    
    // 위치 정보 가져오는 함수
    fun getLocationLatitude() : Double {

        return location?.latitude ?: 0.0
    }
    // 경도 정보 가져오는 함수
    fun getLocationLongitude() : Double {
        return location?.longitude ?: 0.0
    }

    override fun onLocationChanged(changedLocation: Location) {
      location = changedLocation
    }
}