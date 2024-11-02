package com.example.skycast.network

sealed class FetchingState {
    object LoadingCurrent : FetchingState()
    data class SuccessCurrent(val data: Any) : FetchingState()
    data class ErrorCurrent(val message: String) : FetchingState()

    object LoadingForecast : FetchingState()
    data class SuccessForecast(val data: Any) : FetchingState()
    data class ErrorForecast(val message: String) : FetchingState()
}