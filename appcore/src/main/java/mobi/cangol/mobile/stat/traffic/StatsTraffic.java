/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.stat.traffic;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/1/22.
 */
public class StatsTraffic {
    public static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    public static final String DATE_ACTION = "android.intent.action.DATE_CHANGE";
    public static final String NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static StatsTraffic instance;
    private Context context;
    private TrafficDbService trafficDbService;
    private BroadcastReceiver statsTrafficReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("getAction=" + intent.getAction());
            if (BOOT_ACTION.equals(intent.getAction())) {
                // 开机启动
                resetAppTraffic();
            } else if (NETWORK_ACTION.equals(intent.getAction())) {
                NetworkInfo.State wifiState = null;
                NetworkInfo.State mobileState = null;
                final ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                @SuppressLint("MissingPermission") final NetworkInfo networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfoWifi != null) {
                    wifiState = networkInfoWifi.getState();
                }
                @SuppressLint("MissingPermission") final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (networkInfo != null) {
                    mobileState = networkInfo.getState();
                }

                if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                    // 无线网络连接
                    Log.d("wifi connected calcTraffic");
                    calcAppTraffic(TimeUtils.getCurrentDate(), true);
                } else if (wifiState != null && NetworkInfo.State.DISCONNECTED == wifiState) {
                    // 无线网络中断
                    Log.d("wifi disconnect calcTraffic");
                    calcAppTraffic(TimeUtils.getCurrentDate(), true);
                } else if (mobileState != null && NetworkInfo.State.CONNECTED == mobileState) {
                    // 手机网络连接
                    Log.d("mobile connected calcTraffic");
                    calcAppTraffic(TimeUtils.getCurrentDate(), false);
                } else if (mobileState != null && NetworkInfo.State.DISCONNECTED == mobileState) {
                    // 手机网络中断
                    Log.d("mobile disconnect calcTraffic");
                    calcAppTraffic(TimeUtils.getCurrentDate(), false);
                } else {
                    //
                }

            } else {
                //day end
                Log.d("day end calcTraffic");
                endCalcAppTraffic();
            }
        }
    };

    private StatsTraffic(Context context) {
        this.context = context;
        this.onCreated();
    }

    public static StatsTraffic getInstance(Context context) {
        if (instance == null) {
            instance = new StatsTraffic(context.getApplicationContext());
        }
        return instance;
    }

    public void onCreated() {
        trafficDbService = new TrafficDbService(context);
        registerAlarmForDateTraffic();

        final IntentFilter intentFileter = new IntentFilter(BOOT_ACTION);
        intentFileter.addAction(DATE_ACTION);
        intentFileter.addAction(NETWORK_ACTION);
        context.registerReceiver(statsTrafficReceiver, intentFileter);
        if (trafficDbService.getAppTraffic(context.getApplicationInfo().uid) == null) {
            addAppTrafficStats(context.getApplicationInfo().uid, context.getApplicationInfo().packageName);
        }
    }

    public void onDestroy() {
        endCalcAppTraffic();
        context.unregisterReceiver(statsTrafficReceiver);
    }

    private void addAppTrafficStats(int uid, String packageName) {
        final AppTraffic appTraffic = new AppTraffic();
        appTraffic.uid = uid;
        appTraffic.packageName = packageName;
        appTraffic.totalRx = TrafficStats.getUidRxBytes(uid);
        appTraffic.totalTx = TrafficStats.getUidTxBytes(uid);
        appTraffic.mobileRx = 0;
        appTraffic.mobileTx = 0;
        appTraffic.wifiRx = 0;
        appTraffic.wifiTx = 0;
        trafficDbService.saveAppTraffic(appTraffic);
    }

    private void calcDateTraffic(AppTraffic appTraffic, String date, boolean wifi) {
        DateTraffic dateTraffic = trafficDbService.getDateTrafficByDate(appTraffic.uid, date);
        if (dateTraffic == null) {
            dateTraffic = new DateTraffic();
            dateTraffic.uid = appTraffic.uid;
            dateTraffic.date = date;
            dateTraffic.totalRx = 0;
            dateTraffic.totalTx = 0;
            dateTraffic.mobileRx = 0;
            dateTraffic.mobileTx = 0;
            dateTraffic.wifiRx = 0;
            dateTraffic.wifiTx = 0;
            dateTraffic.status = 0;
        }
        final long rx = TrafficStats.getUidRxBytes(appTraffic.uid);
        final long tx = TrafficStats.getUidTxBytes(appTraffic.uid);
        final long rxDelta = rx - appTraffic.totalRx;
        final long txDelta = tx - appTraffic.totalTx;
        dateTraffic.totalRx = dateTraffic.totalRx + rxDelta;
        dateTraffic.totalTx = dateTraffic.totalTx + txDelta;
        if (!wifi) {
            dateTraffic.mobileRx = dateTraffic.mobileRx + rxDelta;
            dateTraffic.mobileTx = dateTraffic.mobileTx + txDelta;
        } else {
            dateTraffic.wifiRx = dateTraffic.wifiRx + rxDelta;
            dateTraffic.wifiTx = dateTraffic.wifiTx + txDelta;
        }
        trafficDbService.saveDateTraffic(dateTraffic);
    }

    public void calcAppTraffic(String date, boolean wifi) {
        final List<AppTraffic> list = trafficDbService.getAppTrafficList();
        AppTraffic appTraffic = null;
        for (int i = 0; i < list.size(); i++) {
            appTraffic = list.get(i);
            calcDateTraffic(appTraffic, date, wifi);
            final long rx = TrafficStats.getUidRxBytes(appTraffic.uid);
            final long tx = TrafficStats.getUidTxBytes(appTraffic.uid);
            final long rxDelta = rx - appTraffic.totalRx;
            final long txDelta = tx - appTraffic.totalTx;
            appTraffic.totalRx = rx;
            appTraffic.totalTx = tx;
            if (!wifi) {
                appTraffic.mobileRx = appTraffic.mobileRx + rxDelta;
                appTraffic.mobileTx = appTraffic.mobileTx + txDelta;
            } else {
                appTraffic.wifiRx = appTraffic.wifiRx + rxDelta;
                appTraffic.wifiTx = appTraffic.wifiTx + txDelta;
            }
            trafficDbService.saveAppTraffic(appTraffic);
        }
    }

    public void resetAppTraffic() {
        final  List<AppTraffic> list = trafficDbService.getAppTrafficList();
        AppTraffic appTraffic = null;
        for (int i = 0; i < list.size(); i++) {
            appTraffic = list.get(i);
            appTraffic.totalRx = TrafficStats.getUidRxBytes(appTraffic.uid);
            appTraffic.totalTx = TrafficStats.getUidTxBytes(appTraffic.uid);
            trafficDbService.saveAppTraffic(appTraffic);
        }
    }

    public List<Map> getUnPostDateTraffic(int uid, String date) {
        final List<DateTraffic> list = trafficDbService.getDateTrafficByStatus(uid, date, 0);
        Map<String, String> map = null;
        DateTraffic dateTraffic = null;
        final List<Map> maps = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dateTraffic = list.get(i);
            map = new HashMap<>();
            map.put("date", dateTraffic.date);
            map.put("totalRx", String.valueOf(dateTraffic.totalRx));
            map.put("totalTx", String.valueOf(dateTraffic.totalTx));
            map.put("mobileRx", String.valueOf(dateTraffic.mobileRx));
            map.put("mobileTx", String.valueOf(dateTraffic.mobileTx));
            map.put("wifiRx", String.valueOf(dateTraffic.wifiRx));
            map.put("wifiTx", String.valueOf(dateTraffic.wifiTx));
            maps.add(map);
        }
        return maps;
    }

    public void saveUnPostDateTraffic(int uid, String currentDate) {
        final List<DateTraffic> list = trafficDbService.getDateTrafficByStatus(uid, currentDate, 0);
        for (int i = 0; i < list.size(); i++) {
            final DateTraffic traffic = list.get(i);
            traffic.status = 1;
            trafficDbService.saveDateTraffic(traffic);
        }

    }

    //day end|| app exit
    private void endCalcAppTraffic() {
        final String date = TimeUtils.getCurrentDate();
        NetworkInfo.State wifiState = null;
        NetworkInfo.State mobileState = null;
        final ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") final NetworkInfo networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfoWifi != null) {
            wifiState = networkInfoWifi.getState();
        }
        @SuppressLint("MissingPermission") final NetworkInfo networkInfoMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfoMobile != null) {
            mobileState = networkInfoMobile.getState();
        }
        if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
            // 无线网络连接
            calcAppTraffic(date, true);
        } else if (mobileState != null && NetworkInfo.State.CONNECTED == mobileState) {
            // 手机网络连接
            calcAppTraffic(date, false);
        }
    }

    public void registerAlarmForDateTraffic() {
        final  int day = 24 * 60 * 60 * 1000;
        final Intent intent = new Intent(DATE_ACTION);
        final PendingIntent sender = PendingIntent.getBroadcast(this.context, 1, intent, 0);

        // 开机之后到现在的运行时间(包括睡眠时间)
        long firstTime = SystemClock.elapsedRealtime();
        final long systemTime = System.currentTimeMillis();

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        final long time = selectTime - systemTime;
        firstTime += time;
        // 进行闹铃注册
        final  AlarmManager manager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, day, sender);

    }
}

