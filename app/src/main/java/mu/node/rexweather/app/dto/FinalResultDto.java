package mu.node.rexweather.app.dto;

import java.util.List;

import mu.node.rexweather.app.models.CurrentWeatherBean;
import mu.node.rexweather.app.models.WeatherForecastBean;

public class FinalResultDto {

	private CurrentWeatherBean currentWeather;
	private List<WeatherForecastBean> weatherForecastBeen;

	public CurrentWeatherBean getCurrentWeather() {
		return currentWeather;
	}

	public void setCurrentWeather(CurrentWeatherBean currentWeather) {
		this.currentWeather = currentWeather;
	}

	public List<WeatherForecastBean> getWeatherForecastBeen() {
		return weatherForecastBeen;
	}

	public void setWeatherForecastBeen(List<WeatherForecastBean> weatherForecastBeen) {
		this.weatherForecastBeen = weatherForecastBeen;
	}
}
