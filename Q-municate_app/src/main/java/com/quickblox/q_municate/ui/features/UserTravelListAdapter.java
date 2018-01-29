package com.quickblox.q_municate.ui.features;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.visha.samplechat.R;

import java.util.List;

/**
 * Created by visha on 17-01-2016.
 */
class TravelUser{
    String name;
    String status;
    boolean selected = false;

    public TravelUser(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class UserTravelListAdapter extends ArrayAdapter<TravelUser>{
    private List<TravelUser> traveluserlist;
    private Context context;
    public UserTravelListAdapter(List<TravelUser> traveluserlist,Context context) {
        super(context, R.layout.activity_travelusers_adapter, traveluserlist);
        this.traveluserlist = traveluserlist;
        this.context = context;
    }
    private static class TravelUserHolder {
        public TextView travelusername;
        public TextView traveluserstatus;
        public CheckBox chkboxtreaveluser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        TravelUserHolder trvlholder = new TravelUserHolder();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.activity_travelusers_adapter, null);
            trvlholder.travelusername = (TextView) v.findViewById(R.id.txtTravelUserName);
            trvlholder.traveluserstatus = (TextView) v.findViewById(R.id.txtTravelUserStatus);
            trvlholder.chkboxtreaveluser = (CheckBox) v.findViewById(R.id.checkBoxTravelUser);

            trvlholder.chkboxtreaveluser.setOnCheckedChangeListener((SmartGridTravelTrackFeature) context);
        }
        else
        {
            trvlholder = (TravelUserHolder) v.getTag();
        }
        TravelUser t = traveluserlist.get(position);
        trvlholder.travelusername.setText(t.getName());
        trvlholder.traveluserstatus.setText(t.getStatus());
        trvlholder.chkboxtreaveluser.setChecked(t.isSelected());
        trvlholder.chkboxtreaveluser.setTag(t);
        return v;
    }
}
