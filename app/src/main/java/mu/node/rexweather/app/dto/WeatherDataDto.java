package mu.node.rexweather.app.dto;

import android.accounts.NetworkErrorException;

import com.google.gson.annotations.SerializedName;

import io.reactivex.Observable;

/**
 * Base class for results returned by the weather web service.
 */
public class WeatherDataDto {
	@SerializedName("cod")
	private int httpCode;

	/**
	 * The web service always returns a HTTP header code of 200 and communicates errors
	 * through a 'cod' field in the JSON payload of the response body.
	 */
	public Observable<? extends WeatherDataDto> filterWebServiceErrors() {
		if (httpCode == 200) {
			return Observable.just(this);
		} else {
			return Observable.error(
					new NetworkErrorException("There was a problem fetching the weather data."));
		}
	}
}
