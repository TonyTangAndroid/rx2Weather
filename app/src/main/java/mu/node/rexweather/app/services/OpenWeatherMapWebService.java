package mu.node.rexweather.app.services;

import io.reactivex.Observable;
import mu.node.rexweather.app.models.CurrentWeatherDataDto;
import mu.node.rexweather.app.dto.WeatherForecastListDataDto;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ztang on 11/15/16.
 */
interface OpenWeatherMapWebService {
	@GET("weather?units=metric&APPID=" + RestClient.API_KEY)
	Observable<CurrentWeatherDataDto> fetchCurrentWeather(@Query("lon") double longitude,
														  @Query("lat") double latitude);

	@GET("forecast/daily?units=metric&cnt=7&APPID=" + RestClient.API_KEY)
	Observable<WeatherForecastListDataDto> fetchWeatherForecasts(
			@Query("lon") double longitude, @Query("lat") double latitude);
}
