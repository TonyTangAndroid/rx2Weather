package mu.node.rexweather.app.dto;

import java.util.ArrayList;

import mu.node.rexweather.app.models.WeatherBean;

/**
 * Data structure for weather forecast results returned by the web service.
 */
public class WeatherForecastListDataDto extends WeatherDataDto {
	public LocationCity city;
	public ArrayList<WeatherBean> list;

	public class LocationCity {
		public String name;
	}

}
