package com.quickblox.q_municate.ui.features;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.quickblox.q_municate.R;
import com.google.android.gms.maps.model.LatLng;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.q_municate.ApplicationSingleton;
import com.quickblox.q_municate.ui.main.MainActivity;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by visha on 10-01-2016.
 */
public class SmartGridLocationFeature extends Activity implements View.OnClickListener{
    Spinner spinnerplaces;
    LatLng temp=new LatLng(0,0);
    ArrayList<LatLng> UserLocations =  new ArrayList<>();

    int i;
    ProgressBar progress;
    private String provider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartgrid_feature);
        spinnerplaces = (Spinner) findViewById(R.id.spinner_places);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Places, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerplaces.setAdapter(adapter);
        Button btngetuserlocations = (Button) findViewById(R.id.btn_get_userlocations);
        TextView textviewLocation = (TextView) findViewById(R.id.textview_location);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        btngetuserlocations.setOnClickListener(this);

        progress.setVisibility(View.VISIBLE);
        ApplicationSingleton.noofusers = ApplicationSingleton.CurrentGroupIDs.size();
        QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
        Integer[] userids = new Integer[ApplicationSingleton.noofusers];
        i=0;
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
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(ApplicationSingleton.CurrentGroupIDs.size());
                QBUsers.getUsersByIDs(ApplicationSingleton.CurrentGroupIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                        if (!ApplicationSingleton.CurrentGroupUsers.isEmpty()) {
                            ApplicationSingleton.CurrentGroupUsers.clear();
                        }
                        ApplicationSingleton.CurrentGroupUsers.addAll(users);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SmartGridLocationFeature.this);
                        dialog.setMessage("get occupants errors: " + errors).create().show();
                    }

                });
                Log.e("Vishalan", "Done clone");
            }

            @Override
            public void onError(List<String> errors) {
                Log.e("Vishalan", "error: " + errors);
            }
        });

        progress.setVisibility(View.GONE);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_userlocations:
                ApplicationSingleton.locationtype = spinnerplaces.getSelectedItem().toString();
                GetUserLocations();
                Intent i = new Intent(SmartGridLocationFeature.this,LocationResultActivity.class);
                startActivity(i);
                break;
        }
    }
    private void GetUserLocations() {
        i=0;
        temp = new LatLng(0,0);
        double templat=0;
        double templong=0;
        while(i< ApplicationSingleton.noofusers)
        {
            templat = templat + (double) ApplicationSingleton.locationclone.get(i).getLatitude();
            templong = templong + (double) ApplicationSingleton.locationclone.get(i).getLongitude();
            Log.e("Vishalan", "temp : " + i + " " + templat + "," + templong + " add:" + ApplicationSingleton.locationclone.get(i).getLongitude());
            UserLocations.add(new LatLng(ApplicationSingleton.locationclone.get(i).getLatitude(), ApplicationSingleton.locationclone.get(i).getLongitude()));
            i++;
        }

        ApplicationSingleton.centroid = new LatLng(templat/ ApplicationSingleton.noofusers, templong/ ApplicationSingleton.noofusers);
        Log.e("Vishalan", "centroid : " + ApplicationSingleton.centroid.latitude + "," + ApplicationSingleton.centroid.longitude);
        ///////////////////////////////////////
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SmartGridLocationFeature.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
