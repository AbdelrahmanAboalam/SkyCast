package com.example.skycast.network

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse

sealed class FetchingState {
    object LoadingCurrent : FetchingState()
    data class SuccessCurrent(val data: CurrentWetherResponse) : FetchingState()
    data class ErrorCurrent(val message: String) : FetchingState()

    object LoadingForecast : FetchingState()
    data class SuccessForecast(val data: WeatherForecastResponse) : FetchingState()
    data class ErrorForecast(val message: String) : FetchingState()
}