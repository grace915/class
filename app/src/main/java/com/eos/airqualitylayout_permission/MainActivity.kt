package com.eos.airqualitylayout_permission

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eos.airqualitylayout_permission.databinding.ActivityMainBinding

// https://www.iqair.com/ko/ -> API key 받기


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // 런타임 권한 요청 시 필요한 요청 코드
    private val PERMISSION_REQUEST_CODE = 100
    // 요청할 권한 목록
    var REQUIRED_PERMISSION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 위치 서비스 요청 시 필요한 런처
    lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAllPermission()
    }

    private fun checkAllPermission() {
        if (!isLocationServiceAvailable()) {
            // GPS가 켜져있지 않은 경우
            showDialogForLocationServiceSetting()
        } else {
            // GPS가 켜져있는 경우, 앱 권한이 모두 허용되었는지 확인
            isRuntimePermissionGranted()
        }
    }

    private fun showDialogForLocationServiceSetting() {
        // 먼저 ActivityResultLauncher 를 설정해줍니다.
        // 이 런처를 이용하여 결괏값을 반환해야 하는 Intent 를 실행할 수 있습니다.
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            // 결괏값을 받았을 때
            if (result.resultCode == Activity.RESULT_OK) {
                if (isLocationServiceAvailable()) {
                    isRuntimePermissionGranted()
                } else {
                    // 허용되지 않았다면 종료
                    Toast.makeText(
                        this@MainActivity,
                        "위치 서비스를 사용할 수 없습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스 비활성화. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        }
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
            Toast.makeText(this@MainActivity,
                "GPS를 설정 후 사용하십시오.", Toast.LENGTH_SHORT).show()
            })
        builder.create().show()
    }

    private fun isRuntimePermissionGranted() {
        // 위치 퍼미션을 가지고 있는지 체크
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE)
        }
    }

    private fun isLocationServiceAvailable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as
                LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE
            && grantResults.size == REQUIRED_PERMISSION.size) {
            // 요청 코드가 PERMISSION_REQUEST_CODE 이고,
            // 요천한 PERMISSION 개수만큼 수신되었다면
            var checkResult = true

            // 모든 권한을 허용했는지 체크
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }

            if (checkResult) {
                // 위치값을 가져올 수 있음
            } else {
                // 위치값을 가져올 수 없으면 앱 종료
                Toast.makeText(
                    this@MainActivity,
                    "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}