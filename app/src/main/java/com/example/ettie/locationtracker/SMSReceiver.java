package com.example.ettie.locationtracker;

/**
 * Created by Ettie on 7/5/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver{

    private LocationManager lm;
    private LocationListener locationListener;

    private String senderNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        SmsMessage[] messages = null;
        String msgBody = "";

        if (extras != null) {
            Object[] pdus = (Object[])extras.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                if (i == 0) {
                    senderNumber = messages[i].getOriginatingAddress();
                }
                msgBody += messages[i].getMessageBody().toString();
            }
            if (msgBody.equals("LT SEND LOCATION")) {
                lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

                locationListener = new MyLocationListener();
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1000, locationListener);

                abortBroadcast();
            }
        }
    }

private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(senderNumber, null, "http://maps.google.com/maps?q=" +
                    location.getLatitude() + "," + location.getLongitude(), null, null);

            lm.removeUpdates(locationListener);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String string, int status, Bundle extras) {

    }
    }
}
