import com.example.skycast.model.FakeLocalDataSource
import com.example.skycast.model.FakeRemoteDataSource
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith


class WeatherRepositoryImplTest {

    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        weatherRepository = WeatherRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun getWeatherForecast_RemoteSuccessful_ReturnsWeatherData() = runTest {
        // Given
        val expectedResponse = WeatherForecastResponse()
        fakeRemoteDataSource.weatherForecastResponse = expectedResponse

        // When
        val result = weatherRepository.getWeatherForecast(0.0, 0.0, "en", "metric")

        // Then
        assertThat(result, `is`(expectedResponse))
    }

    @Test
    fun getWeatherForecast_RemoteFailed_ThrowsException() = runTest {
        // Given
        fakeRemoteDataSource.shouldReturnError = true

        // When
        val exception = assertFailsWith<Exception> {
            weatherRepository.getWeatherForecast(0.0, 0.0, "en", "metric")
        }

        // Then
        assertTrue(exception.message?.contains("Failed to fetch weather forecast") == true)
    }

    @Test
    fun getCurrentWeather_RemoteSuccessful_ReturnsCurrentWeather() = runTest {
        // Given
        val expectedResponse = CurrentWetherResponse()
        fakeRemoteDataSource.currentWeatherResponse = expectedResponse

        // When
        val result = weatherRepository.getCurrentWeather(0.0, 0.0, "en", "metric")

        // Then
        assertThat(result, `is`(expectedResponse))
    }

}
