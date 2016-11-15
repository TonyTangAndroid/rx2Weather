package mu.node.rexweather.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Weather Activity.
 * <p/>
 * This is the main activity for our app. It simply serves as a container for the Weather Fragment.
 * We prefer to build our implementation in a fragment because that enables future reuse if, for
 * example we build a tablet version of this app.
 */
public class WeatherActivity extends Activity {

	private boolean hasCheckedGoogle;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new WeatherFragment())
					.commit();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		if (!hasCheckedGoogle) {
			checkPlayServicesAvailable();
		}
	}

	private void checkPlayServicesAvailable() {
		final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		final int status = apiAvailability.isGooglePlayServicesAvailable(this);

		if (status != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(status)) {
				apiAvailability.getErrorDialog(this, status, 1).show();

			} else {
				Crouton.makeText(this,
						R.string.no_google_play, Style.ALERT).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		hasCheckedGoogle = true;
	}
}
