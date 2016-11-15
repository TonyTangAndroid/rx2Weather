package mu.node.rexweather.app.models;

public class CurrentWeatherBean extends WeatherForecastBean {
	private final float mTemperature;  // Current temperature.

	public CurrentWeatherBean(final String locationName,
							  final long timestamp,
							  final String description,
							  final float temperature,
							  final float minimumTemperature,
							  final float maximumTemperature) {

		super(locationName, timestamp, description, minimumTemperature, maximumTemperature);

		mTemperature = temperature;
	}

	public float getTemperature() {
		return mTemperature;
	}
}
