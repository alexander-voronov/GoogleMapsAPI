package ru.geekbrains.googlemapsapi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private EditText textLatitude;
    private EditText textLongitude;

    private GoogleMap mMap;
    private Marker currentMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();
        requestPermissions();

    }

    // Инициализация Views
    private void initViews() {
        textLatitude = findViewById(R.id.editLat);
        textLongitude = findViewById(R.id.editLng);
    }
// Запрашиваем Permission’ы

    private void requestPermissions() {
// Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у
// пользователя
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
// Запрашиваем координаты
            requestLocation();
        } else {
// Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    // Запрашиваем координаты
    private void requestLocation() {
// Если Permission’а всё- таки нет, просто выходим: приложение не имеет
// смысла
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
// Получаем менеджер геолокаций
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
// Получаем наиболее подходящий провайдер геолокации по критериям.
// Но определить, какой провайдер использовать, можно и самостоятельно.
// В основном используются LocationManager.GPS_PROVIDER или
// LocationManager.NETWORK_PROVIDER, но можно использовать и
// LocationManager.PASSIVE_PROVIDER - для получения координат в

// пассивном режиме
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
// Будем получать геоположение через каждые 10 секунд или каждые
// 10 метров
            locationManager.requestLocationUpdates(provider, 10000, 10, new
                    LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double lat = location.getLatitude(); // Широта
                            String latitude = Double.toString(lat);
                            textLatitude.setText(latitude);
                            double lng = location.getLongitude(); // Долгота
                            String longitude = Double.toString(lng);
                            textLongitude.setText(longitude);
                            String accuracy = Float.toString(location.getAccuracy());
// Точность
                            LatLng currentPosition = new LatLng(lat, lng);
                            currentMarker.setPosition(currentPosition);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 12));
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle
                                extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
// Запрашиваем эти два Permission’а у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Результат запроса Permission’а у пользователя:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) { // Запрошенный нами
// Permission
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
// Все препоны пройдены и пермиссия дана
// Запросим координаты
                requestLocation();
            }
        }
    }

}