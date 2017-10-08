package com.jooksu.kobusp.pocketweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

import static com.squareup.seismic.ShakeDetector.SENSITIVITY_LIGHT;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener, OnLocationUpdatedListener, OnActivityUpdatedListener {

    private static final int LOCATION_PERMISSION_ID = 1001;

    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {
            // Create a new TextView
            TextView t = new TextView(MainActivity.this);
            t.setTypeface(Typeface.createFromAsset(getAssets(), "weatherFont.ttf"));
            t.setTextSize(getResources().getDimension(R.dimen.icon_size));
            t.setTextColor(Color.parseColor("#DADADA")); //Minimum API level screws us over here a bit
            return t;
        }
    };

    private ShakeDetector sd;
    private SensorManager sensorManager;
    private LocationGooglePlayServicesProvider provider;
    private TextSwitcher weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        setupShakeSensor();
        final ImageView joke = findViewById(R.id.iv_wrongDegrees);
        ConstraintLayout layout = findViewById(R.id.constraint);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joke.setVisibility(View.INVISIBLE);
            }
        });
        TextView tv_celsius = findViewById(R.id.tv_celsius);
        tv_celsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joke.setVisibility(View.VISIBLE);
            }
        });
        Button getWeather = findViewById(R.id.button);

        weatherIcon = findViewById(R.id.tv_weatherIcon);
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        weatherIcon.setInAnimation(in);
        weatherIcon.setOutAnimation(out);
        setupLocation();


        weatherIcon.setFactory(mFactory);
        getWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherIcon.setText(getString(R.string.getting_data_icon));
                RestClient.getWeather(25.7, 28.7,MainActivity.this, new OnSuccessCallback<WeatherModel>() {
                    @Override
                    public void onSuccess(WeatherModel weatherModel) {
                        populateScreen(weatherModel);
                    }
                }, new OnErrorCallback() {
                    @Override
                    public void onError(String message) {
                        message.toString();
                    }
                });

            }
        });

    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);
    }

    @Override
    protected void onPause() {
        if (sd != null)
        sd.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sd != null)
            sd.start(sensorManager);
    }

    private void getLocationWeather(Location location) {
        weatherIcon.setText(getString(R.string.getting_data_icon));
        RestClient.getWeather(location.getLongitude(),location.getLatitude(),  MainActivity.this, new OnSuccessCallback<WeatherModel>() {
            @Override
            public void onSuccess(WeatherModel weatherModel) {
                populateScreen(weatherModel);
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(String message) {
                message.toString();
            }
        });
    }

    private void populateScreen(WeatherModel weatherModel) {
        TextSwitcher weatherIcon = findViewById(R.id.tv_weatherIcon);
        weatherIcon.setText(getWeatherIcon(weatherModel.getWeatherInformation()[0]));

    }

    @Override public void hearShake() {
        TextSwitcher weatherIcon = findViewById(R.id.tv_weatherIcon);
        weatherIcon.setText(getString(R.string.earthquake_icon));
    }

    private void setupShakeSensor() {
        sd = new ShakeDetector(this);
        sd.start(sensorManager);
        sd.setSensitivity(SENSITIVITY_LIGHT);
    }

    private String getWeatherIcon(WeatherInformation weatherInformation) {
        if (weatherInformation.getId() == WEATHER_TYPES.Clear)
            return getString(R.string.sunny_icon);
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
        //Not needed really
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null)
            getLocationWeather(location);
    }

}
