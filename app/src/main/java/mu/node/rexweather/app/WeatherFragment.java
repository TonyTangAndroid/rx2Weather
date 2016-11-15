package mu.node.rexweather.app;

import android.accounts.NetworkErrorException;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import hugo.weaving.DebugLog;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mu.node.rexweather.app.dto.FinalResultDto;
import mu.node.rexweather.app.helpers.TemperatureFormatter;
import mu.node.rexweather.app.models.CurrentWeatherBean;
import mu.node.rexweather.app.models.WeatherForecastBean;
import mu.node.rexweather.app.services.RestClient;

/**
 * Weather Fragment.
 * <p/>
 * Displays the current weather as well as a 7 day forecast for our location. Data is loaded
 * from a web service.
 */
public class WeatherFragment extends Fragment {

	private static final long LOCATION_TIMEOUT_SECONDS = 20;
	private static final String TAG = WeatherFragment.class.getCanonicalName();
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

	private CompositeDisposable compositeDisposable;
	private RxLocation rxLocation;
	private LocationRequest locationRequest;

	private SwipeRefreshLayout swipeRefreshLayout;
	private TextView tvLocationName;
	private TextView tvCurrentTemperature;
	private TextView tvAttribution;
	private TextView tvLastUpdateTime;
	private ListView lvForecastListView;


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		compositeDisposable = new CompositeDisposable();

		final View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
		tvLocationName = (TextView) rootView.findViewById(R.id.location_name);
		tvCurrentTemperature = (TextView) rootView.findViewById(R.id.current_temperature);

		lvForecastListView = (ListView) rootView.findViewById(R.id.weather_forecast_list);
		final WeatherForecastListAdapter adapter = new WeatherForecastListAdapter(this, new ArrayList<>(), getActivity());
		lvForecastListView.setAdapter(adapter);
		tvLastUpdateTime = (TextView) rootView.findViewById(R.id.tv_last_update_time);
		tvAttribution = (TextView) rootView.findViewById(R.id.attribution);
		tvAttribution.setVisibility(View.INVISIBLE);

		// Set up swipe refresh layout.
		swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_container);
		swipeRefreshLayout.setColorSchemeResources(R.color.brand_main, android.R.color.black, R.color.brand_main, android.R.color.black);
		swipeRefreshLayout.setOnRefreshListener(this::updateWeather);

		rxLocation = new RxLocation(getActivity());
		rxLocation.setDefaultTimeout(LOCATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(5000);

		updateWeather();
		return rootView;
	}

	@Override
	public void onDestroyView() {
		compositeDisposable.clear();
		super.onDestroyView();
	}


	private Observable<Location> getLocationObservable(boolean success) {
		if (success) {
			return rxLocation.location().updates(locationRequest)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.flatMap(this::getAddressFromLocation);

		} else {
			onFailureGettingCurrentLocation();
			return rxLocation.location().lastLocation()
					.doOnSuccess(this::logJustGetLastLocation)
					.flatMapObservable(this::getAddressFromLocation);
		}
	}

	@DebugLog
	private void logJustGetLastLocation(Location location) {

	}

	@DebugLog
	private Observable<Location> getAddressFromLocation(Location location) {
		return Observable.just(location).subscribeOn(Schedulers.io());
	}


	public void onDataReady(FinalResultDto weatherData) {

		// Update UI with current weather.
		final CurrentWeatherBean currentWeather = weatherData.getCurrentWeather();
		tvLocationName.setText(currentWeather.getLocationName());
		tvCurrentTemperature.setText(
				TemperatureFormatter.format(currentWeather.getTemperature()));

		// Update weather forecast list.
		final List<WeatherForecastBean> weatherForecastBeen = weatherData.getWeatherForecastBeen();
		final WeatherForecastListAdapter adapter = (WeatherForecastListAdapter)
				lvForecastListView.getAdapter();
		adapter.clear();
		adapter.addAll(weatherForecastBeen);

		tvLastUpdateTime.setText(DATE_FORMAT.format(new Date()));
		swipeRefreshLayout.setRefreshing(false);
		tvAttribution.setVisibility(View.VISIBLE);

	}

	/**
	 * Get weather data for the current location and update the UI.
	 */
	private void updateWeather() {
		swipeRefreshLayout.setRefreshing(true);

		compositeDisposable.add(
				rxLocation.settings().checkAndHandleResolution(locationRequest)
						.doOnSuccess(this::onLocationReady)
						.flatMapObservable(this::getLocationObservable)
						.flatMap(new Function<Location, ObservableSource<FinalResultDto>>() {
							@Override
							public ObservableSource<FinalResultDto> apply(Location location) throws Exception {
								final RestClient restClient = new RestClient();
								final double longitude = location.getLongitude();
								final double latitude = location.getLatitude();

								return Observable.zip(
										restClient.fetchCurrentWeather(longitude, latitude),
										restClient.fetchWeatherForecasts(longitude, latitude),
										(currentWeather, weatherForecasts) -> getStringWeatherForecastHashMap(currentWeather, weatherForecasts)
								);
							}
						})
						.subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(this::onDataReady, this::onError)
		);


	}

	@NonNull
	private FinalResultDto getStringWeatherForecastHashMap(CurrentWeatherBean currentWeather, List<WeatherForecastBean> weatherForecastBeen) {
		FinalResultDto weatherData = new FinalResultDto();
		weatherData.setCurrentWeather(currentWeather);
		weatherData.setWeatherForecastBeen(weatherForecastBeen);
		return weatherData;
	}

	private void onLocationReady(Boolean aBoolean) {
		swipeRefreshLayout.setRefreshing(true);
	}

	private void onError(Throwable error) {

		swipeRefreshLayout.setRefreshing(false);

		if (error instanceof TimeoutException) {
			onFailureGettingCurrentLocation();
		} else if (error instanceof NetworkErrorException) {
			onNetworkError();
		} else {
			Log.e(TAG, "onError", error);
			onOtherError(error.getMessage() != null ? error.getMessage() : getString(R.string.unknow_error));
		}


	}

	private void onOtherError(String errMessage) {
		Crouton.makeText(getActivity(), errMessage, Style.ALERT).show();
	}

	private void onNetworkError() {
		Crouton.makeText(getActivity(),
				R.string.error_fetch_weather, Style.ALERT).show();
	}

	private void onFailureGettingCurrentLocation() {
		Crouton.makeText(getActivity(),
				R.string.error_location_unavailable, Style.ALERT).show();
	}


}
