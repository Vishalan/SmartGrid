package com.quickblox.q_municate.ui.features;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.q_municate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.q_municate.ApplicationSingleton;
import com.quickblox.q_municate.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TravelTrackResult extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // LogCat tag
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private boolean controlvar = false;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = ApplicationSingleton.AS_UPDATE_INTERVAL; // 10 sec
    private static int FATEST_INTERVAL = ApplicationSingleton.AS_FATEST_INTERVAL; // 5 sec
    private static int DISPLACEMENT = ApplicationSingleton.AS_DISPLACEMENT; // 10 meters


    ArrayList<String> outBoundUsers =  new ArrayList<String>();
    // UI elements
    //private TextView lblLocation;
    //private Button btnShowLocation, btnStartLocationUpdates, btnmaplocselect;
    private GoogleMap mMap;
    Button btnShowLocation,btnStartLocationUpdates;
    TextView alertuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracktravel_result);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        alertuser = (TextView)findViewById(R.id.textViewalertusers);
        btnShowLocation = (Button) findViewById(R.id.btn_display_location);
        btnStartLocationUpdates = (Button) findViewById(R.id.btn_start_location_update);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                controlvar=false;
                stopLocationUpdates();
                controlvar=false;
                stopLocationUpdates();
                Intent i = new Intent(TravelTrackResult.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });

        // Toggling the periodic location updates
        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                controlvar = true;
                togglePeriodicLocationUpdates();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void displayLocationnew() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            userLocationUpdate(latitude, longitude);
           // Toast.makeText(getApplicationContext(),
          //         latitude + ", " + longitude, Toast.LENGTH_LONG)
            //        .show();


            // lblLocation.setText(latitude + ", " + longitude);

        } else {


            //  lblLocation
            //         .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }
    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Toast.makeText(getApplicationContext(),
                    latitude + ", " + longitude, Toast.LENGTH_LONG)
                    .show();
           // lblLocation.setText(latitude + ", " + longitude);

        } else {


          //  lblLocation
           //         .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnStartLocationUpdates
                    .setText("STOP PERIODIC UPDATES");

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btnStartLocationUpdates
                    .setText("START PERIODIC UPDATES");

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(controlvar == true)
        {
            togglePeriodicLocationUpdates();
        }
        // Assign the new location
        //mLastLocation = location;
        // Displaying the new location on UI
        //Toast.makeText(getApplicationContext(),"Location changed", Toast.LENGTH_LONG).show();
        displayLocationnew(); // includes all call of procedures
    }

    public void userLocationUpdate(double lat , double lang)
    {
        final double tlat=lat;
        final double tlang=lang;
        QBLocation qlocation = new QBLocation(lat, lang, "Tracking");
        QBLocations.createLocation(qlocation, new QBEntityCallbackImpl<QBLocation>() {
            @Override
            public void onSuccess(QBLocation qbLocation, Bundle args) {
                //  Toast toast = Toast.makeText(getApplicationContext(),
                //           "User Location Updated", Toast.LENGTH_SHORT);
                //   toast.show();
                getAllUserLocations(tlat, tlang);

            }

            @Override
            public void onError(List<String> errors) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "User Location Updation failure:" + errors, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
    public  void getAllUserLocations(double lat , double lang)
    {
        final double tlat=lat;
        final double tlang=lang;
        ApplicationSingleton.noofusers = ApplicationSingleton.CurrentGroupIDs.size();
        QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
        Integer[] userids = new Integer[ApplicationSingleton.noofusers];
        int i=0;
        while(i< ApplicationSingleton.noofusers)
        {
            userids[i]= ApplicationSingleton.CurrentGroupIDs.get(i);
            i++;
        }
        getLocationsBuilder.setUserIds(userids); // VishalanChange: do loop here
        getLocationsBuilder.setLastOnly(); //VishalanChange: very important
        QBLocations.getLocations(getLocationsBuilder, new QBEntityCallbackImpl<ArrayList<QBLocation>>() {
            @Override
            public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {
                if (!ApplicationSingleton.locationclone.isEmpty()) {
                    ApplicationSingleton.locationclone.clear();
                }
                ApplicationSingleton.locationclone = (ArrayList) locations.clone();
                //Log.e("Vishalan", "Done clone");
                int i = 0;
                float tempdistanceInMeters;
                Location firstLocation = new Location("");
                Location secondLocation = new Location("");
                if (!outBoundUsers.isEmpty()) {
                    outBoundUsers.clear();
                }
                mMap.clear();
                while (i < ApplicationSingleton.noofusers) {
                    if (i == 0) {
                        firstLocation.setLatitude(ApplicationSingleton.locationclone.get(i).getLatitude());
                        firstLocation.setLongitude(ApplicationSingleton.locationclone.get(i).getLongitude());
                    } else {
                        secondLocation.setLatitude(ApplicationSingleton.locationclone.get(i).getLatitude());
                        secondLocation.setLongitude(ApplicationSingleton.locationclone.get(i).getLongitude());

                        tempdistanceInMeters = firstLocation.distanceTo(secondLocation);
                        // Log.e("Vishalan", "first: " + firstLocation + "second: " + secondLocation + "temp: " + tempdistanceInMeters + "threshold: " + ApplicationSingleton.proximitydistance);
                        Toast toast = Toast.makeText(getApplicationContext(), "temp: " + tempdistanceInMeters + "threshold: " + ApplicationSingleton.proximitydistance, Toast.LENGTH_SHORT);
                        toast.show();
                        if (tempdistanceInMeters > ApplicationSingleton.proximitydistance) {
                            Vibrator v = (Vibrator) TravelTrackResult.this.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1000);
                            outBoundUsers.add(ApplicationSingleton.CurrentGroupUsers.get(i).getEmail());
                        }
                    }
                    //add marker
                    mMap.addMarker(new MarkerOptions()
                            .title("User " + ApplicationSingleton.CurrentGroupUsers.get(i).getEmail().toString())
                            .position(
                                    new LatLng(ApplicationSingleton.locationclone.get(i).getLatitude(), ApplicationSingleton.locationclone.get(i).getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin)));

                    i++;
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(tlat, tlang)) // Sets the center of the map to
                                // Mountain View
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                if (!outBoundUsers.isEmpty()) {
                    alertuser.setTextColor(Color.parseColor("#FF0000"));
                    alertuser.setText("Alert User/s lost: " + outBoundUsers);
                } else {
                    alertuser.setTextColor(Color.parseColor("#008000"));
                    alertuser.setText("Everything is OK");
                }
                togglePeriodicLocationUpdates();
            }

            @Override
            public void onError(List<String> errors) {
                Log.e("Vishalan", "error: " + errors);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

}
