package com.example.weatherapp

import android.app.DownloadManager.Query
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//b2e2090743d7ebaeb616a2d6c47095d0
class MainActivity : AppCompatActivity() {
    private  val  binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchweatherdata("Delhi")
        searchCity();
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               if(query!= null){
                   fetchweatherdata(query)
               }
                return true;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }


        })

    }

    private fun fetchweatherdata(cityName:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getweatherdata(cityName,"b2e2090743d7ebaeb616a2d6c47095d0", "metric")
        response.enqueue(object:Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!= null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity= responseBody.main.humidity
                    val windspeed= responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val sealevel=responseBody.main.pressure
                    val condition =responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxtemp =responseBody.main.temp_max
                    val mintemp =responseBody.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.Humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = "${time(sunrise)} "
                    binding.sunset.text = "${time(sunset)} "
                    binding.sea.text = "$sealevel hPa"
                    binding.maxTemp.text ="Max Temp: $maxtemp °C"
                    binding.minTemp.text = "Min Temp: $mintemp °C"
                    binding.condition.text = condition
                    binding.day.text=dayname(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"
                    
                    changeWeatherContionsAccord(condition)

                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeWeatherContionsAccord(conditions: String) {
    when(conditions){
        "Clear Sky" ,"Sunny" ,"Clear" ->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnim.setAnimation(R.raw.sun)
        }
        "Partly Clouds" ,"Clouds","Overcast" ,"Mist","Foggy","Wind" ->{
            binding.root.setBackgroundResource(R.drawable.colud_background)
            binding.lottieAnim.setAnimation(R.raw.cloud)
        }
        "Light Rain" ,"Drizzle" ,"Moderate Rain","Showers","Heavy Rain","Rain" ->{
            binding.root.setBackgroundResource(R.drawable.rain_background)
            binding.lottieAnim.setAnimation(R.raw.rain)
        }
        "Light Snow" ,"Moderate Snow" ,"Heavy Snow","Blizzard","Snow"->{
            binding.root.setBackgroundResource(R.drawable.snow_background)
            binding.lottieAnim.setAnimation(R.raw.snow)
        }
    }
        binding.lottieAnim.playAnimation()
    }

    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }


    fun dayname(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}