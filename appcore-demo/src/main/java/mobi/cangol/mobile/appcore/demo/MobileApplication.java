package mobi.cangol.mobile.appcore.demo;

import androidx.appcompat.app.AppCompatDelegate;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * Created by weixuewu on 15/9/14.
 */
public class MobileApplication extends CoreApplication {

    public void onCreate() {
        this.setDevMode(true);
        this.setAsyncInit(false);
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        getSession().remove("");
    }

    @Override
    public void init() {
        if (DeviceInfo.isAppProcessByFile(this)) {
        }
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}
