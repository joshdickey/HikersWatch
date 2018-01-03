package com.warpgatetechnologies.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private final int REQUEST_CODE = 1;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }
    }

    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            Log.i("startListening", "started Listening");
        }
    }

    private void updateLocationInfo(Location location) {
        Log.d("LocationInfo", location.toString());

        TextView latTextView = findViewById(R.id.txvlatitude);
        TextView lonTextView = findViewById(R.id.txvLongitude);
        TextView altTextView = findViewById(R.id.txvAltitude);
        TextView accTextView = findViewById(R.id.txvAccuracy);
        TextView addressTextView = findViewById(R.id.txvAddress);

        lonTextView.setText(String.format("Longitude: %s", location.getLongitude()));
        latTextView.setText(String.format("Latitude: %s", location.getLatitude()));
        altTextView.setText(String.format("Altitude: %s", location.getAltitude()));
        accTextView.setText(String.format("Accuracy: %s", location.getAccuracy()));

        addressTextView.setText(String.format("Address: \n%s", getAddress(location)));


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            startListening();

        }else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permissions
                String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permission, REQUEST_CODE);
            } else {
                //we already have permission

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,0,mLocationListener);

                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location!= null) {
                    updateLocationInfo(location);
                }

            }
        }

    }


    private String getAddress(Location location) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addressList != null && addressList.size() > 0) {

                String address = "";

                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare() + " ";
                }

                if (addressList.get(0).getLocality() != null) {
                    address += addressList.get(0).getLocality() + ",\n";
                }
                if (addressList.get(0).getPostalCode() != null) {
                    address += addressList.get(0).getPostalCode() + ",\n";
                }
                if (addressList.get(0).getCountryName() != null) {
                    address += addressList.get(0).getCountryName() + "  ";
                }


                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address Not Found";
    }




    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }


}