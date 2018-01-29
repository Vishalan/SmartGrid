package com.quickblox.q_municate.ui.features;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.quickblox.q_municate.ApplicationSingleton;
import com.quickblox.q_municate.R;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.q_municate.ui.base.BaseActivity;
import com.quickblox.q_municate.ui.main.MainActivity;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by visha on 17-01-2016.
 */
public class SmartGridTravelTrackFeature extends BaseActivity implements
        android.widget.CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    Button btntracktravel,btngetuserstatus;
    ProgressBar progressBar;
    ListView UserStatus;
    UserTravelListAdapter trvlAdapter;
    ArrayList<TravelUser> traveluserslist =  new ArrayList<TravelUser>();
    Spinner spinprox;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_track);
        UserStatus = (ListView) findViewById(R.id.usersContainer);
        spinprox = (Spinner) findViewById(R.id.spinner_proximities);
        btntracktravel = (Button) findViewById(R.id.btn_track_travel);
        btngetuserstatus = (Button) findViewById(R.id.btn_get_user_status);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btntracktravel.setOnClickListener(this);
        btngetuserstatus.setOnClickListener(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.proximities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinprox.setAdapter(adapter);

        if(isConnectionEnabled()){

            getOnlineUsers();
        }

    }
    private void getOnlineUsers() {
        progressBar.setVisibility(View.VISIBLE);

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
                int i = 0;
                for (QBUser user : ApplicationSingleton.CurrentGroupUsers) {
                    long currentTime = System.currentTimeMillis();
                    long userLastRequestAtTime = user.getLastRequestAt().getTime();
                    //Toast.makeText(SmartGridTravelTrackFeature.this, "UsersRequestTime: " + userLastRequestAtTime, Toast.LENGTH_LONG).show();
                    // if user didn't do anything last 5 minutes (5*60*1000 milliseconds)
                    if ((currentTime - userLastRequestAtTime) > 5 * 60 * 1000) {
                        // user is offline now
                        traveluserslist.add(new TravelUser(user.getLogin(), "Offline"));
                    } else {
                        traveluserslist.add(new TravelUser(user.getLogin(), "Online"));
                    }
                    i++;

                }
                // AlertDialog.Builder dialog = new AlertDialog.Builder(SmartGridTravelTrackFeature.this);
                // dialog.setMessage(": " + traveluserslist).create().show();
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SmartGridTravelTrackFeature.this);
                dialog.setMessage("get occupants errors: " + errors).create().show();
            }

        });

        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = UserStatus.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            TravelUser p = traveluserslist.get(pos);
            p.setSelected(isChecked);

            Toast.makeText(
                    this,
                    "Clicked on User: " + p.getName() + ". State: is "
                            + isChecked, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_get_user_status:
                trvlAdapter = new UserTravelListAdapter(traveluserslist,SmartGridTravelTrackFeature.this);
                UserStatus.setAdapter(trvlAdapter);
               // AlertDialog.Builder dialog = new AlertDialog.Builder(SmartGridTravelTrackFeature.this);
               // dialog.setMessage(": " + traveluserslist).create().show();
                break;
            case R.id.btn_track_travel:
                ApplicationSingleton.proximitydistance= Integer.parseInt(spinprox.getSelectedItem().toString().replaceAll("[^0-9]", ""));
                Intent i = new Intent(SmartGridTravelTrackFeature.this,TravelTrackResult.class);
                startActivity(i);
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SmartGridTravelTrackFeature.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
