package com.quickblox.q_municate.ui.features;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visha.samplechat.ApplicationSingleton;
import com.example.visha.samplechat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.users.QBUsers;

import java.util.List;

/**
 * Created by visha on 10-01-2016.
 */
public class LocationMapSelect extends FragmentActivity implements OnMapClickListener {
    final int RQS_GooglePlayServices = 1;
    private GoogleMap myMap;

    TextView tvLocInfo;
    Button btnselectlocation;
    LatLng tempLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map_location);

        tvLocInfo = (TextView)findViewById(R.id.locinfo);
        btnselectlocation = (Button) findViewById(R.id.btnmaplocationset);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
        myMap = mySupportMapFragment.getMap();
        myMap.setMyLocationEnabled(true);

        myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        myMap.setOnMapClickListener(this);
        btnselectlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tempLocation==null)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "First tap on Map to Select Location", Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    ApplicationSingleton.UserLocation = tempLocation;
                    QBLocation location = new QBLocation(tempLocation.latitude, tempLocation.longitude, "I'm at PIzzeria");
                    QBLocations.createLocation(location, new QBEntityCallbackImpl<QBLocation>() {
                        @Override
                        public void onSuccess(QBLocation qbLocation, Bundle args) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "User Location Updated", Toast.LENGTH_SHORT);
                            toast.show();
                            if (ApplicationSingleton.newuserlogin) {
                                QBUsers.signOut(new QBEntityCallbackImpl() {
                                    @Override
                                    public void onSuccess() {
                                        ApplicationSingleton.newuserlogin = false;
                                        Intent i = new Intent(LocationMapSelect.this, MainActivity.class);
                                        finish();
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onError(List errors) {

                                    }
                                });

                            } else {
                                Intent i = new Intent(LocationMapSelect.this, MainActivity.class);
                                finish();
                                startActivity(i);
                            }

                        }

                        @Override
                        public void onError(List<String> errors) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "User Location Updation failure:" + errors, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                        getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(LocationMapSelect.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
           // Toast.makeText(getApplicationContext(),
           //         "isGooglePlayServicesAvailable SUCCESS",
           //         Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }

    }

    @Override
    public void onMapClick(LatLng point) {
        tvLocInfo.setText(point.toString());
        tempLocation = point;
        myMap.clear();
        myMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
        myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }


}
