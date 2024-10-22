import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skycast.model.remote.WetherForeCastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse

class SharedWeatherViewModel : ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val _weatherForecast = MutableLiveData<WetherForeCastResponse>()
    val weatherForecast: LiveData<WetherForeCastResponse> get() = _weatherForecast

    fun setWeatherData(current: CurrentWetherResponse, forecast: WetherForeCastResponse) {
        _currentWeather.value = current
        _weatherForecast.value = forecast
    }
}

