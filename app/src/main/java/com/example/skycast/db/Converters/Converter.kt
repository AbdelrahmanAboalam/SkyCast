//package com.example.skycast.db.Converters
//
//import com.example.skycast.db.tables.CurrentWeatherEntity
//import com.example.skycast.db.tables.WeatherForecastEntity
//import com.example.skycast.model.remote.City
//import com.example.skycast.model.remote.Clouds
//import com.example.skycast.model.remote.Coord
//import com.example.skycast.model.remote.Weather
//import com.example.skycast.model.remote.WeatherData
//import com.example.skycast.model.remote.WeatherForecastResponse
//import com.example.skycast.model.remote.Wind
//import com.example.skycast.model.remote.current.CurrentWetherResponse
//import com.example.skycast.model.remote.current.Main
//import com.example.skycast.model.remote.current.Sys
//
//class Converter {
//
//    companion object {
//        // Function to convert CurrentWeatherResponse to CurrentWeatherEntity
//        fun fromResponseToEntity(response: CurrentWetherResponse): CurrentWeatherEntity {
//            return CurrentWeatherEntity(
//                base = response.base,
//                cod = response.cod,
//                dt = response.dt,
//                id = response.id,
//                name = response.name,
//                timezone = response.timezone,
//                visibility = response.visibility,
//                cloudsAll = response.clouds.all,
//                coordLat = response.coord.lat, // Assuming coord has a lat attribute
//                coordLon = response.coord.lon, // Assuming coord has a lon attribute
//                mainFeelsLike = response.main.feels_like,
//                mainGrndLevel = response.main.grnd_level,
//                mainHumidity = response.main.humidity,
//                mainPressure = response.main.pressure,
//                mainSeaLevel = response.main.sea_level,
//                mainTemp = response.main.temp,
//                mainTempMax = response.main.temp_max,
//                mainTempMin = response.main.temp_min,
//                sysCountry = response.sys.country,
//                sysId = response.sys.id,
//                sysSunrise = response.sys.sunrise,
//                sysSunset = response.sys.sunset,
//                sysType = response.sys.type,
//                weatherMain = response.weather.firstOrNull()?.main ?: "", // Safely get first or default to empty string
//                weatherDescription = response.weather.firstOrNull()?.description ?: "",
//                weatherIcon = response.weather.firstOrNull()?.icon ?: "",
//                weatherId = response.weather.firstOrNull()?.id ?: 0,
//                windDeg = response.wind.deg,
//                windGust = response.wind.gust,
//                windSpeed = response.wind.speed
//            )
//        }
//
//        // Function to convert CurrentWeatherEntity to CurrentWeatherResponse
//        fun fromEntityToResponse(entity: CurrentWeatherEntity): CurrentWetherResponse {
//            return CurrentWetherResponse(
//                base = entity.base,
//                clouds = Clouds(all = entity.cloudsAll), // Create Clouds instance
//                cod = entity.cod,
//                coord = Coord(lat = entity.coordLat, lon = entity.coordLon), // Create Coord instance
//                dt = entity.dt,
//                id = entity.id,
//                main = Main(
//                    feels_like = entity.mainFeelsLike,
//                    grnd_level = entity.mainGrndLevel,
//                    humidity = entity.mainHumidity,
//                    pressure = entity.mainPressure,
//                    sea_level = entity.mainSeaLevel,
//                    temp = entity.mainTemp,
//                    temp_max = entity.mainTempMax,
//                    temp_min = entity.mainTempMin
//                ),
//                name = entity.name,
//                sys = Sys(
//                    country = entity.sysCountry,
//                    id = entity.sysId,
//                    sunrise = entity.sysSunrise,
//                    sunset = entity.sysSunset,
//                    type = entity.sysType
//                ),
//                timezone = entity.timezone,
//                visibility = entity.visibility,
//                weather = listOf( // Create a list with one Weather item
//                    Weather(
//                        description = entity.weatherDescription,
//                        icon = entity.weatherIcon,
//                        id = entity.weatherId,
//                        main = entity.weatherMain
//                    )
//                ),
//                wind = Wind(
//                    deg = entity.windDeg,
//                    gust = entity.windGust,
//                    speed = entity.windSpeed
//                )
//            )
//        }
//    }
//
//    fun fromWeatherForecastEntity(entity: WeatherForecastEntity): WeatherForecastResponse {
//        val city = City(
//            coord = com.example.skycast.model.remote.Coord(
//                lat = entity.cityLat,
//                lon = entity.cityLon
//            ),
//            country = entity.cityCountry,
//            id = entity.cityId,
//            name = entity.cityName,
//            population = entity.cityPopulation,
//            sunrise = entity.citySunrise,
//            sunset = entity.citySunset,
//            timezone = entity.cityTimezone
//        )
//
//        // Convert list of WeatherDataEntity to WeatherData
//        val weatherDataList = entity.weatherDataList.map { weatherDataEntity ->
//            WeatherData(
//                clouds = com.example.skycast.model.remote.Clouds(all = weatherDataEntity.cloudsAll),
//                dt = weatherDataEntity.dt,
//                main = com.example.skycast.model.remote.Main(
//                    feels_like = weatherDataEntity.mainFeelsLike,
//                    grnd_level = weatherDataEntity.mainGrndLevel,
//                    humidity = weatherDataEntity.mainHumidity,
//                    pressure = weatherDataEntity.mainPressure,
//                    sea_level = weatherDataEntity.mainSeaLevel,
//                    temp = weatherDataEntity.mainTemp,
//                    temp_kf = weatherDataEntity.mainTempKf,
//                    temp_max = weatherDataEntity.mainTempMax,
//                    temp_min = weatherDataEntity.mainTempMin
//                ),
//                pop = weatherDataEntity.pop,
//                visibility = weatherDataEntity.visibility,
//                wind = com.example.skycast.model.remote.Wind(
//                    deg = weatherDataEntity.windDeg,
//                    gust = weatherDataEntity.windGust,
//                    speed = weatherDataEntity.windSpeed
//                ),
//                weather = listOf( // Create a list of Weather objects
//                    com.example.skycast.model.remote.Weather(
//                        description = weatherDataEntity.weatherDescription,
//                        icon = weatherDataEntity.weatherIcon,
//                        id = weatherDataEntity.weatherId,
//                        main = weatherDataEntity.weatherMain
//                    )
//                ),
//                dt_txt = null,
//                rain = null // Handle rain if necessary
//            )
//        }
//
//        return WeatherForecastResponse(
//            city = city,
//            cnt = entity.cnt,
//            cod = entity.cod,
//            list = weatherDataList,
//            message = entity.message
//        )
//    }
//
//
//}
