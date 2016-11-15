package mu.node.rexweather.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mu.node.rexweather.app.helpers.DayFormatter;
import mu.node.rexweather.app.helpers.TemperatureFormatter;
import mu.node.rexweather.app.models.WeatherForecastBean;

/**
 * Provides items for our list view.
 */
class WeatherForecastListAdapter extends ArrayAdapter<WeatherForecastBean> {

	private WeatherFragment weatherFragment;

	public WeatherForecastListAdapter(WeatherFragment weatherFragment, final List<WeatherForecastBean> weatherForecastBeen, final Context context) {
		super(context, 0, weatherForecastBeen);
		this.weatherFragment = weatherFragment;
	}

	@Override
	public boolean isEnabled(final int position) {
		return false;
	}

	@SuppressWarnings("ConstantConditions")
	@SuppressLint("InflateParams")
	@NonNull
	@Override
	public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
			convertView = layoutInflater.inflate(R.layout.weather_forecast_list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.day);
			viewHolder.descriptionTextView = (TextView) convertView
					.findViewById(R.id.description);
			viewHolder.maximumTemperatureTextView = (TextView) convertView
					.findViewById(R.id.maximum_temperature);
			viewHolder.minimumTemperatureTextView = (TextView) convertView
					.findViewById(R.id.minimum_temperature);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final WeatherForecastBean weatherForecastBean = (WeatherForecastBean) getItem(position);

		final DayFormatter dayFormatter = new DayFormatter(weatherFragment.getActivity());
		final String day = dayFormatter.format(weatherForecastBean.getTimestamp());
		viewHolder.dayTextView.setText(day);
		viewHolder.descriptionTextView.setText(weatherForecastBean.getDescription());
		viewHolder.maximumTemperatureTextView.setText(
				TemperatureFormatter.format(weatherForecastBean.getMaximumTemperature()));
		viewHolder.minimumTemperatureTextView.setText(
				TemperatureFormatter.format(weatherForecastBean.getMinimumTemperature()));

		return convertView;
	}

	/**
	 * Cache to avoid doing expensive findViewById() calls for each getView().
	 */
	private class ViewHolder {
		private TextView dayTextView;
		private TextView descriptionTextView;
		private TextView maximumTemperatureTextView;
		private TextView minimumTemperatureTextView;
	}
}
