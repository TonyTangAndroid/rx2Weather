package mu.node.rexweather.app.services;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import mu.node.rexweather.app.BuildConfig;
import mu.node.rexweather.app.models.CurrentWeatherBean;
import mu.node.rexweather.app.models.CurrentWeatherDataDto;
import mu.node.rexweather.app.models.WeatherBean;
import mu.node.rexweather.app.dto.WeatherDataDto;
import mu.node.rexweather.app.models.WeatherForecastBean;
import mu.node.rexweather.app.dto.WeatherForecastListDataDto;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
	// We are implementing against version 2.5 of the Open Weather Map web service.
	public static final String WEB_SERVICE_BASE_URL = "http://api.openweathermap.org/data/2.5/";
	public static final String API_KEY = "df428a47de54da7b4923388da7c91c50";
	private final OpenWeatherMapWebService mWebService;

	public RestClient() {


		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(WEB_SERVICE_BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.build();


		mWebService = retrofit.create(OpenWeatherMapWebService.class);


	}

	public Observable<CurrentWeatherBean> fetchCurrentWeather(final double longitude,
															  final double latitude) {
		return mWebService.fetchCurrentWeather(longitude, latitude)
				.flatMap((Function<CurrentWeatherDataDto, ObservableSource<? extends WeatherDataDto>>) WeatherDataDto::filterWebServiceErrors)
				.map(weatherDataDto -> getCurrentWeatherBean((CurrentWeatherDataDto) weatherDataDto));
	}

	@NonNull
	private CurrentWeatherBean getCurrentWeatherBean(CurrentWeatherDataDto weatherDataDto) {
		return new CurrentWeatherBean(weatherDataDto.locationName, weatherDataDto.timestamp,
				weatherDataDto.weather.get(0).description, weatherDataDto.main.temp,
				weatherDataDto.main.temp_min, weatherDataDto.main.temp_max);
	}

	public Observable<List<WeatherForecastBean>> fetchWeatherForecasts(final double longitude,
																	   final double latitude) {
		return mWebService.fetchWeatherForecasts(longitude, latitude)
				.flatMap((Function<WeatherForecastListDataDto, ObservableSource<? extends WeatherDataDto>>) WeatherDataDto::filterWebServiceErrors)
				.map(weatherDataDto -> getWeatherForecastBeen((WeatherForecastListDataDto) weatherDataDto));
	}

	@NonNull
	private List<WeatherForecastBean> getWeatherForecastBeen(WeatherForecastListDataDto weatherDataDto) {
		final ArrayList<WeatherForecastBean> weatherForecastBeen =
				new ArrayList<>();

		for (WeatherBean data : weatherDataDto.list) {
			final WeatherForecastBean weatherForecastBean = new WeatherForecastBean(
					weatherDataDto.city.name, data.timestamp, data.weather.get(0).description,
					data.temp.min, data.temp.max);
			weatherForecastBeen.add(weatherForecastBean);
		}

		return weatherForecastBeen;
	}

}
