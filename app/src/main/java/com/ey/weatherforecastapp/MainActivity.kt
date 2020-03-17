package com.ey.weatherforecastapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.os.Messenger
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.ey.weatherforecastapp.ui.main.model.WeatherInfoImpl
import com.ey.weatherforecastapp.ui.main.model.WeatherInfoInterface
import com.ey.weatherforecastapp.ui.main.model.data.Weather
import com.ey.weatherforecastapp.ui.main.model.data.WeatherData
import com.ey.weatherforecastapp.ui.main.model.database.WeatherDatabase
import com.ey.weatherforecastapp.ui.main.model.viewmodel.WeatherInfoViewModel
import com.ey.weatherforecastapp.ui.main.receiver.IncomingMessageHandler
import com.ey.weatherforecastapp.ui.main.receiver.TestJobService
import com.ey.weatherforecastapp.ui.main.utils.MESSENGER_INTENT_KEY
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.weather_info.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var model: WeatherInfoInterface
    lateinit private var handler: IncomingMessageHandler
    private lateinit var viewModel: WeatherInfoViewModel
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null
    private val allWeatherItem: MutableList<Weather>? = null
    private var weatherDatabase: WeatherDatabase? = null
    lateinit private var serviceComponent: ComponentName
    private var jobId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (isDBNull() != null) {
            scheduleJob()
        }
        serviceComponent = ComponentName(this, TestJobService::class.java)
        handler = IncomingMessageHandler(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        model = WeatherInfoImpl(applicationContext)
        viewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel::class.java)
        setLiveDataListeners()

        weatherDatabase = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java, "sample-db"
        ).build()

    }

    override fun onStart() {
        super.onStart()
        val startServiceIntent = Intent(this, TestJobService::class.java)
        val messengerIncoming = Messenger(handler)
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
        startService(startServiceIntent)
    }

    private fun scheduleJob() {
        val builder = JobInfo.Builder(jobId++, serviceComponent)
        builder.setOverrideDeadline(5000)
        fetchData()
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
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

        WeatherInfoDbAsync(commonWeatherData).execute()

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

    private fun getAddressFromLatLon(context: Context, lat: Double, lon: Double): String {

        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    @SuppressLint("StaticFieldLeak")
    inner class WeatherInfoDbAsync(private var info: WeatherData?) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            weatherDatabase?.weatherDao()?.insertWeatherData(info!!)
            var room = weatherDatabase?.weatherDao()?.insertWeatherData(info!!)
            Log.d("sos", "database ${weatherDatabase?.weatherDao()?.insertWeatherData(info!!)}")
            return null
        }
    }

    private fun fetchData() {
        val weatherData: LiveData<WeatherData>? = weatherDatabase?.weatherDao()?.getWeather()
        weatherData?.observe(this,
            Observer<WeatherData> { t ->
                //Update your UI here.

                Log.d("sos", "database retrieval${t?.cityAndCountry}")
            })
    }

    private fun isDBNull(): Boolean? {
        val weatherData: LiveData<WeatherData>? = weatherDatabase?.weatherDao()?.getWeather()
        return weatherData?.hasObservers()
    }
}



