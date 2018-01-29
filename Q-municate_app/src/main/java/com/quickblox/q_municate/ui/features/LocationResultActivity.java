package com.quickblox.q_municate.ui.features;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.quickblox.q_municate.ApplicationSingleton;
import com.quickblox.q_municate.R;
//import com.example.visha.samplechat.core.ChatService;
import com.quickblox.chat.QBChatService;
import com.quickblox.q_municate.helper.Place;
import com.quickblox.q_municate.helper.PlacesService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.quickblox.q_municate.ui.main.MainActivity;

/**
 * Created by visha on 11-01-2016.
 */
public class LocationResultActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private String[] places;
    private LocationManager locationManager;
    private Location loc;

    private int userindex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_result);
        initCompo();

        places = getResources().getStringArray(R.array.places);
        currentLocation();
        //loc.setLatitude(ApplicationSingleton.centroid.latitude);
        //loc.setLongitude(ApplicationSingleton.centroid.longitude);
        //mMap.clear();
       // new GetPlaces(LocationResultActivity.this, ApplicationSingleton.locationtype).execute();
        QBUser user = QBChatService.getInstance().getUser();
        userindex = ApplicationSingleton.CurrentGroupIDs.indexOf(user.getId());
        new GetPlacesSecondAttempt(LocationResultActivity.this, ApplicationSingleton.locationtype).execute();



    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(result.isEmpty())
            {
                Toast.makeText(LocationResultActivity.this, "No Places in appropriate Proximity", Toast.LENGTH_LONG).show();
                QBUser user = QBChatService.getInstance().getUser();
                userindex = ApplicationSingleton.CurrentGroupIDs.indexOf(user.getId());
                new GetPlacesSecondAttempt(LocationResultActivity.this, ApplicationSingleton.locationtype).execute();
                //VishalanChange: find other locations
            }
            else
            {
                for (int i = 0; i < result.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .title(result.get(i).getName())
                            .position(
                                    new LatLng(result.get(i).getLatitude(), result
                                            .get(i).getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin))
                            .snippet(result.get(i).getVicinity()));
                }
                for (int i=0;i< ApplicationSingleton.noofusers;i++)
                {
                    mMap.addMarker(new MarkerOptions()
                            .title(ApplicationSingleton.CurrentGroupUsers.get(i).getEmail())
                            .position(new LatLng(ApplicationSingleton.locationclone.get(i).getLatitude(), ApplicationSingleton.locationclone.get(i).getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pushpin)));
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(result.get(0).getLatitude(), result
                                .get(0).getLongitude())) // Sets the center of the map to
                                // Mountain View
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(getResources().getString(R.string.API_KEY_Places));
            ArrayList<Place> findPlaces = service.findPlaces(ApplicationSingleton.centroid.latitude,
                    ApplicationSingleton.centroid.longitude, places); // 28.632808 // 77.218276

            for (int i = 0; i < findPlaces.size(); i++) {

                Place placeDetail = findPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        }

    }

    private class GetPlacesSecondAttempt extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlacesSecondAttempt(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(result.isEmpty())
            {
                Toast.makeText(LocationResultActivity.this, "No Places in appropriate Proximity", Toast.LENGTH_LONG).show();
                //VishalanChange: find solution
            }
            else
            {
                for (int i = 0; i < result.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .title(result.get(i).getName())
                            .position(
                                    new LatLng(result.get(i).getLatitude(), result
                                            .get(i).getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin))
                            .snippet(result.get(i).getVicinity()));
                }
                for (int i=0;i< ApplicationSingleton.noofusers;i++)
                {
                    mMap.addMarker(new MarkerOptions()
                            .title(ApplicationSingleton.CurrentGroupUsers.get(i).getEmail())
                            .position(new LatLng(ApplicationSingleton.locationclone.get(i).getLatitude(), ApplicationSingleton.locationclone.get(i).getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pushpin)));
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(result.get(0).getLatitude(), result
                                .get(0).getLongitude())) // Sets the center of the map to
                                // Mountain View
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            ArrayList<Place> resultplaces = new ArrayList<Place>();
            PlacesService service = new PlacesService(getResources().getString(R.string.API_KEY_Places));
            HashMap<Integer,ArrayList<Place>> placesresult = new HashMap<Integer,ArrayList<Place>>();
            Location firstLocation = new Location("");
            firstLocation.setLatitude(ApplicationSingleton.locationclone.get(userindex).getLatitude());
            firstLocation.setLongitude(ApplicationSingleton.locationclone.get(userindex).getLongitude());

            for (int i=0;i< ApplicationSingleton.noofusers;i++)
            {
                if(i!=userindex)
                {
                    Location participantlocation  = new Location("");
                    participantlocation.setLatitude(ApplicationSingleton.locationclone.get(i).getLatitude());
                    participantlocation.setLongitude(ApplicationSingleton.locationclone.get(i).getLongitude());
                    float tempdiff = firstLocation.distanceTo(participantlocation);
                    ApplicationSingleton.placesradius = (int) tempdiff/2;
                }
                ArrayList<Place> findPlaces = service.findPlaces(ApplicationSingleton.locationclone.get(i).getLatitude(),
                        ApplicationSingleton.locationclone.get(i).getLongitude(), places);
                placesresult.put(i,findPlaces);
                HashMap<Integer,Float> distances = new HashMap<>();
                float distance1=9999999999f;
                int index1=0;
                float distance2=9999999999f;
                int index2=0;
                for(int j=0 ; j<findPlaces.size() ; j++)
                {
                    Location secondLocation = new Location("");
                    secondLocation.setLatitude(findPlaces.get(j).getLatitude());
                    secondLocation.setLongitude(findPlaces.get(j).getLongitude());
                    float tempdistance = firstLocation.distanceTo(secondLocation);
                    distances.put(j,tempdistance);
                   /* if(tempdistance<distance1)
                    {
                        distance2=distance1;
                        index2 = index1;
                        distance1=tempdistance;
                        index1=j;

                    }
                    else if(tempdistance<distance2)
                    {
                        distance2=tempdistance;
                        index2=j;
                    }*/
                }
                LinkedHashMap<Integer, Float> sortedDistances = new LinkedHashMap<>();
                sortedDistances = ApplicationSingleton.sortHashMapIntFloat(distances);
                List<Integer> keys  =  new ArrayList<Integer>(sortedDistances.keySet());
                List<Float> values = new ArrayList<Float>(sortedDistances.values());
                if(!values.isEmpty())
                {
                    float diffDistance = values.get(0);

                    int k = 1;
                    float diffthreshold = 500;
                    resultplaces.add(findPlaces.get(keys.get(0)));
                    while((k) < values.size())
                    {
                        if((values.get(k)-diffDistance) > diffthreshold)
                        {
                            break;
                        }
                        resultplaces.add(findPlaces.get(keys.get(k+1)));
                        k++;
                    }
                }

            }
            for (int i = 0; i < resultplaces.size(); i++) {

                Place placeDetail = resultplaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return resultplaces;
        }

    }

    private void initCompo() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(new Criteria(), false);

        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            loc = location;
            Log.e(TAG, "location : " + location);
        }

    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);
        }
    };

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LocationResultActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }

}
