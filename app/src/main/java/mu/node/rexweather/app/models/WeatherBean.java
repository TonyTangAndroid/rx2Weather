package mu.node.rexweather.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ztang on 11/15/16.
 */
public class WeatherBean {
	@SerializedName("dt")
	public long timestamp;
	public TemperatureBean temp;
	public ArrayList<DescriptionBean> weather;
}
