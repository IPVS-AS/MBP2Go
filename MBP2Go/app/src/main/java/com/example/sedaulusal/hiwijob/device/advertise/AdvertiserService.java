package com.example.sedaulusal.hiwijob.device.advertise;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.json.JSONException;

import java.util.Map;

/**
 * Created by rosso on 23.09.17.
 */

public class AdvertiserService extends Service {



  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return new AdvertiseBinder(this);
  }

  public static class AdvertiseBinder extends Binder {
    private AdvertiserService instance;

    public AdvertiseBinder(AdvertiserService instance) {
      this.instance = instance;
    }

    public AdvertiserService getService() {
      return instance;
    }
  }

}
