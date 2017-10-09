package com.jooksu.kobusp.pocketweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.location.DetectedActivity;
import com.jooksu.kobusp.pocketweather.api.OnErrorCallback;
import com.jooksu.kobusp.pocketweather.api.OnSuccessCallback;
import com.jooksu.kobusp.pocketweather.api.RestClient;
import com.jooksu.kobusp.pocketweather.constants.WEATHER_TYPES;
import com.jooksu.kobusp.pocketweather.models.Weather.WeatherInformation;
import com.jooksu.kobusp.pocketweather.models.Weather.WeatherModel;
import com.squareup.seismic.ShakeDetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

/**
 * Created by kobusp on 2017/10/06.
 * Main activity
 */

import static com.squareup.seismic.ShakeDetector.SENSITIVITY_LIGHT;

public class MainActivity extends AppCompatActivity implements
        ShakeDetector.Listener,
        OnLocationUpdatedListener,
        OnActivityUpdatedListener {

    private static final int LOCATION_PERMISSION_ID = 1001;

    private final ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {
            // Create a new TextView
            TextView t = new TextView(MainActivity.this);
            t.setTypeface(Typeface.createFromAsset(getAssets(), "weatherFont.ttf"));
            t.setTextSize(getResources().getDimension(R.dimen.icon_size));
            t.setText(getString(R.string.no_data_icon));
            t.setTextColor(Color.parseColor("#DADADA")); //Minimum API level screws us over here a bit
            return t;
        }
    };

    private ShakeDetector sd;
    private SensorManager sensorManager;
    private TextSwitcher weatherIcon;
    private TextView tvPlace;
    private TextView tvMin;
    private TextView tvMax;
    private TextView tvDate;
    private TextView tvCelsius;
    private SmartLocation smartLocation;
    private boolean dialogIsShown = false;
    private LocationGooglePlayServicesProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        setupShakeSensor();
        setupCelsiusJoke();
        setupWeatherText();
        setupLocation();
    }

    private void setupCelsiusJoke() {
        final ImageView joke = findViewById(R.id.iv_wrongDegrees);
        ConstraintLayout layout = findViewById(R.id.constraint);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joke.setVisibility(View.GONE);
                hideText(false);
            }
        });
        tvCelsius = findViewById(R.id.tv_celsius);
        tvCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joke.setVisibility(View.VISIBLE);
                hideText(true);
            }
        });
    }

    private void setupWeatherText() {
        weatherIcon = findViewById(R.id.tv_weatherIcon);
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        weatherIcon.setInAnimation(in);
        weatherIcon.setOutAnimation(out);
        weatherIcon.setFactory(mFactory);
    }

    private void hideText(boolean isHidden) {
        tvPlace = findViewById(R.id.tv_place);
        tvMin = findViewById(R.id.tv_min);
        tvMax = findViewById(R.id.tv_max);
        tvDate = findViewById(R.id.tv_date);
        tvCelsius = findViewById(R.id.tv_celsius);

        if (isHidden) {
            tvPlace.setVisibility(View.INVISIBLE);
            tvMin.setVisibility(View.INVISIBLE);
            tvMax.setVisibility(View.INVISIBLE);
            tvDate.setVisibility(View.INVISIBLE);
            tvCelsius.setVisibility(View.INVISIBLE);
        } else {
            tvPlace.setVisibility(View.VISIBLE);
            tvMin.setVisibility(View.VISIBLE);
            tvMax.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.VISIBLE);
            tvCelsius.setVisibility(View.VISIBLE);
        }
    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        smartLocation = new SmartLocation.Builder(this).logging(true).build();
        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);
    }

    @Override
    protected void onPause() {
        if (sd != null)
            sd.stop();
        if (smartLocation != null)
            smartLocation.activity().stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sd != null)
            sd.start(sensorManager);
        if (smartLocation != null)
            smartLocation.location(provider).start(this);
    }

    private void getLocationWeather(final Location location) {
        weatherIcon.setText(getString(R.string.getting_data_icon));
        RestClient.getWeather(location.getLongitude(), location.getLatitude(), MainActivity.this, new OnSuccessCallback<WeatherModel>() {
            @Override
            public void onSuccess(WeatherModel weatherModel) {
                populateScreen(weatherModel, location);
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(String message) {
                showEmptyErrorScreen();
            }
        });
    }

    private void showEmptyErrorScreen() {
        weatherIcon.setText(getString(R.string.problem));
    }

    private String getLocationName(WeatherModel weatherModel, Location location) {
        String placeName;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            placeName = addresses.get(0).getSubLocality() + ", " + addresses.get(0).getLocality() + ", \n" + addresses.get(0).getCountryName();
        } catch (Exception e) {
            placeName = weatherModel.getName() + "," + weatherModel.getLocationInformation().getCountry();
            return placeName;
        }
        return placeName;
    }

    private void populateScreen(WeatherModel weatherModel, Location location) {
        weatherIcon = findViewById(R.id.tv_weatherIcon);
        weatherIcon.setText(getWeatherIcon(weatherModel));
        tvPlace = findViewById(R.id.tv_place);
        tvMin = findViewById(R.id.tv_min);
        tvMax = findViewById(R.id.tv_max);
        tvDate = findViewById(R.id.tv_date);


        tvPlace.setText(getLocationName(weatherModel, location));
        tvMin.setText(getString(R.string.min_temperature, Math.round(weatherModel.getLocationWeatherDetails().getMinTemperature())));
        tvMax.setText(getString(R.string.max_temperature, Math.round(weatherModel.getLocationWeatherDetails().getMaxTemperature())));
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
        tvDate.setText(format.format(new Date()));

    }

    @Override
    public void hearShake() {
        weatherIcon = findViewById(R.id.tv_weatherIcon);
        weatherIcon.setText(getString(R.string.earthquake_icon));
        if (!dialogIsShown)
            showAskForUpdate();
    }

    private void showAskForUpdate() {
        dialogIsShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.shake_title)
                .setMessage(R.string.shake_detected)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        weatherIcon.setText(getString(R.string.getting_data_icon));
                        smartLocation.with(MainActivity.this).location().oneFix().start(MainActivity.this);
                        dialogIsShown = false;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsShown = false;
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void setupShakeSensor() {
        sd = new ShakeDetector(this);
        sd.start(sensorManager);
        sd.setSensitivity(SENSITIVITY_LIGHT);
    }

    private String getWeatherIcon(WeatherModel weatherModel) {
        WeatherInformation weatherInformation = weatherModel.getWeatherInformation()[0];
        if (weatherInformation.getId() == WEATHER_TYPES.Clear) {
            long now = new Date().getTime() / 1000L; //UNIX time conversion
            long sunrise = weatherModel.getLocationInformation().getSunrise();
            long sunset = weatherModel.getLocationInformation().getSunset();
            if ((now > sunrise) && (now < sunset))
                return getString(R.string.sunny_icon);
            return getString(R.string.clear_night_icon);
        }

        int id = weatherInformation.getId() / 100;
        String icon;
        switch (id) {
            case WEATHER_TYPES.Thunderstorm:
                icon = getString(R.string.thunder_icon);
                break;
            case WEATHER_TYPES.Drizzle:
                icon = getString(R.string.drizzle_icon);
                break;
            case WEATHER_TYPES.Rain:
                icon = getString(R.string.rainy_icon);
                break;
            case WEATHER_TYPES.Atmosphere:
                icon = getString(R.string.foggy_icon);
                break;
            case WEATHER_TYPES.Clouds:
                icon = getString(R.string.cloudy_icon);
                break;
            case WEATHER_TYPES.Snow:
                icon = getString(R.string.snowy_icon);
                break;
            default:
                icon = getString(R.string.no_data_icon);
                break;
        }
        return icon;
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        //Library wants it but not needed with my current implementation
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null)
            getLocationWeather(location);
    }

}
