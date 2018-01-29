package com.quickblox.q_municate;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.quickblox.core.QBSettings;
import com.quickblox.location.model.QBLocation;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

//import vc908.stickerfactory.StickersManager;

public class ApplicationSingleton extends Application {
    private static final String TAG = ApplicationSingleton.class.getSimpleName();

    public static final String APP_ID = "32824";
    public static final String AUTH_KEY = "UZZR3rPRuxmSvQL";
    public static final String AUTH_SECRET = "3KEypZgHuBqqbVR";
    public static final String STICKER_API_KEY = "847b82c49db21ecec88c510e377b452c";
    public static String USER_LOGIN = null;
    public static String USER_PASSWORD = null;
    public static  int permissioncheck = 0;

    public static int placesradius =1000;

    public static boolean chatloginstatus = false;
    public static String googleemail = null;
    public static String googlename = null;
    public static Bitmap googleimage = null;

    public static int noofusers = 0;

    public static int proximitydistance = 0;

    public static boolean newuserlogin =false;
    public static LatLng UserLocation;
    public static ArrayList<Integer> CurrentGroupIDs =  new ArrayList<>();
    public static List<QBUser> CurrentGroupUsers =  new ArrayList<>();
    public static ArrayList<QBLocation> locationclone = new ArrayList<>();
    public static LatLng centroid = new LatLng(0,0);
    public static String locationtype = null;

    private static ApplicationSingleton instance;
    public static ApplicationSingleton getInstance() {
        return instance;
    }

    public static int AS_UPDATE_INTERVAL = 10000; // 10 sec
    public static int AS_FATEST_INTERVAL = 5000; // 5 sec
    public static int AS_DISPLACEMENT = 10; // 10 meters
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        instance = this;

        /* Initialise QuickBlox SDK
        QBSettings.getInstance().setApplicationId(ApplicationSingleton.APP_ID);
        QBSettings.getInstance().setAuthorizationSecret(ApplicationSingleton.AUTH_SECRET);
        QBSettings.getInstance().setAuthorizationKey(ApplicationSingleton.AUTH_KEY);
        StickersManager.initialize(ApplicationSingleton.STICKER_API_KEY, this);
        */
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
       // StickersManager.initialize(STICKER_API_KEY, this);
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    public static LinkedHashMap sortHashMapIntFloat(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                float comp1 = (float)passedMap.get(key);
                float comp2 = (float)val;

                if (comp1==comp2){
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((Integer)key, (Float)val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
