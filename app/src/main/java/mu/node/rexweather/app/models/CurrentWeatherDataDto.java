package mu.node.rexweather.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import mu.node.rexweather.app.dto.WeatherDataDto;

/**
 * Data structure for current weather results returned by the web service.
 */
public class CurrentWeatherDataDto extends WeatherDataDto {
	@SerializedName("name")
	public String locationName;
	@SerializedName("dt")
	public long timestamp;
	public ArrayList<DescriptionBean> weather;
	public TempBean main;

}
