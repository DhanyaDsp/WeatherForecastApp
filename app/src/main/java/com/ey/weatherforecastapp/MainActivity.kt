package com.ey.weatherforecastapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ey.weatherforecastapp.ui.main.model.WeatherInfoImpl
import com.ey.weatherforecastapp.ui.main.model.WeatherInfoInterface
import com.ey.weatherforecastapp.ui.main.model.data.Weather
import com.ey.weatherforecastapp.ui.main.model.data.WeatherData
import com.ey.weatherforecastapp.ui.main.model.viewmodel.WeatherInfoViewModel
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.weather_info.*
import kotlinx.coroutines.NonCancellable.cancel
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var model: WeatherInfoInterface
    private lateinit var viewModel: WeatherInfoViewModel
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null
    private val allWeatherItem: MutableList<Weather>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        model = WeatherInfoImpl(applicationContext)
        viewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel::class.java)
        setLiveDataListeners()


    }

    private fun setLiveDataListeners() {
        val selectedCityId = 7778677
        viewModel.getWeatherInfo(selectedCityId, model)


        viewModel.weatherInfoLiveData.observe(this, Observer { weatherData ->
            setWeatherInfo(weatherData)
        })
    }


    @SuppressLint("SetTextI18n")
    private fun setWeatherInfo(commonWeatherData: WeatherData) {
        txt_temperature?.text = commonWeatherData.temperature
        txt_city?.text = commonWeatherData.cityAndCountry
        txt_humidity?.text = "Humidity ${commonWeatherData.humidity}"
        txt_pressure?.text = "Pressure ${commonWeatherData.pressure}"
        txt_visibility?.text = "Visibility ${commonWeatherData.visibility}"
        txt_sunrise?.text = commonWeatherData.sunrise
        txt_sunset?.text = commonWeatherData.sunset
        cloud_description?.text = commonWeatherData.description
        wind_speed?.text = "Speed ${commonWeatherData.windSpeed}"
        wind_degree?.text = "Degree ${commonWeatherData.windDegree}"
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        lat = location.latitude
                        lon = location.longitude
                        val location = getAddressFromLatLon(this, lat!!, lon!!)
                        txt_current_location.text = location
                        Log.d("getLastLocation", location)


                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            /*findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()*/
            lat = mLastLocation.latitude
            lon = mLastLocation.longitude
            val location = getAddressFromLatLon(applicationContext, lat!!, lon!!)
            txt_current_location.text = location
            Log.d("onLocationResult", location)

        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }
}

private fun getAddressFromLatLon(context: Context, lat: Double, lon: Double): String {
    val geoCoder = Geocoder(context, Locale.getDefault())
    val addresses = geoCoder.getFromLocation(
        lat,
        lon,
        1
    )

    val address = addresses.get(0)
        .getAddressLine(0)
    val city = addresses.get(0).getLocality()
    val country = addresses.get(0).countryName
    return "$city,$country"
}



